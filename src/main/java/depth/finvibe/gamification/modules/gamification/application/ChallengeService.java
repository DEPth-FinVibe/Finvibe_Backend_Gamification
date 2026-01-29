package depth.finvibe.gamification.modules.gamification.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import depth.finvibe.gamification.modules.gamification.application.port.out.*;
import depth.finvibe.gamification.shared.dto.XpRewardEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import depth.finvibe.gamification.modules.gamification.application.port.in.ChallengeCommandUseCase;
import depth.finvibe.gamification.modules.gamification.domain.PersonalChallenge;
import depth.finvibe.gamification.modules.gamification.domain.PersonalChallengeReward;
import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import depth.finvibe.gamification.modules.gamification.domain.enums.WeeklyEventType;
import depth.finvibe.gamification.modules.gamification.domain.vo.ChallengeCondition;
import depth.finvibe.gamification.modules.gamification.domain.vo.Period;
import depth.finvibe.gamification.modules.gamification.domain.vo.Reward;
import depth.finvibe.gamification.modules.gamification.dto.ChallengeDto;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class ChallengeService implements ChallengeCommandUseCase {

    private final ChallengeGenerator challengeGenerator;
    private final PersonalChallengeRepository personalChallengeRepository;
    private final MetricRepository metricRepository;
    private final PersonalChallengeRewardRepository personalChallengeRewardRepository;
    private final XpRewardEventPublisher xpRewardEventPublisher;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void generatePersonalChallenges() {
        List<ChallengeDto.ChallengeGenerationResponse> generated = challengeGenerator.generate();

        if(generated.size() != 3) {
            throw new IllegalStateException("Generated challenges size is not equal to 3");
        }

        List<PersonalChallenge> personalChallenges = generated.stream()
                .map(this::toPersonalChallenge)
                .toList();

        personalChallengeRepository.saveAll(personalChallenges);
    }

    private PersonalChallenge toPersonalChallenge(ChallengeDto.ChallengeGenerationResponse response) {
        return PersonalChallenge.of(
                response.getTitle(),
                response.getDescription(),
                ChallengeCondition.of(response.getMetricType(), response.getTargetValue()),
                Period.ofWeek(LocalDate.now()),
                Reward.of(response.getRewardXp(), null)
        );
    }

    @Override
    @Transactional
    public void rewardPersonalChallenges() {
        Period currentPeriod = Period.ofWeek(LocalDate.now());
        List<PersonalChallenge> personalChallenges = personalChallengeRepository.findAllByPeriod(currentPeriod);

        if (personalChallenges.isEmpty()) {
            return;
        }

        List<PersonalChallengeReward> rewards = new ArrayList<>();

        personalChallenges.forEach(this::rewardUsersByChallenge);

        personalChallengeRewardRepository.saveAll(rewards);
    }

    private void rewardUsersByChallenge(PersonalChallenge personalChallenge) {
        ChallengeCondition condition = personalChallenge.getCondition();

        List<UUID> achievedUserIds = metricRepository.findUsersAchieved(condition.getMetricType(), condition.getTargetValue());

        List<PersonalChallengeReward> toSave = achievedUserIds.stream()
                .map(userId -> toPersonalChallengeReward(personalChallenge, userId))
                .toList();

        personalChallengeRewardRepository.saveAll(toSave);

        rewardXpToEachUsers(personalChallenge, achievedUserIds);
    }



    private void rewardXpToEachUsers(PersonalChallenge personalChallenge, List<UUID> achievedUserIds) {
        achievedUserIds.forEach(userId -> publishXpRewardEvent(
                userId,
                String.format("[%s] 챌린지 보상", personalChallenge.getTitle()),
                personalChallenge.getReward().getRewardXp()
        ));
    }

    private void publishXpRewardEvent(UUID userId, String reason, Long rewardXp) {
        // 어플리케이션 이벤트로 일단 처리
        applicationEventPublisher.publishEvent(
                XpRewardEvent.of(
                        userId.toString(),
                        reason,
                        rewardXp
                )
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleXpRewardEvent(XpRewardEvent event) {
        // 커밋이 완료된 후 외부 이벤트를 발행.
        xpRewardEventPublisher.publishXpRewardEvent(event);
    }

    private static PersonalChallengeReward toPersonalChallengeReward(PersonalChallenge personalChallenge, UUID userId) {
        return PersonalChallengeReward.of(
                personalChallenge.getId(),
                userId,
                personalChallenge.getPeriod(),
                personalChallenge.getReward()
        );
    }

    @Override
    @Transactional
    public void rewardWeeklyChallenges() {
        LocalDate today = LocalDate.now();

        // 주말 거래 토너먼트: 현재 수익률 상위 10명
        List<UUID> weekendTournamentUsers = metricRepository.findTopUsersByMetric(
                UserMetricType.CURRENT_RETURN_RATE,
                10
        );
        rewardWeeklyEventUsers(WeeklyEventType.WEEKEND_TRADING_TOURNAMENT, weekendTournamentUsers, 1000L);

        // 챌린지 이벤트: 지난 주 챌린지 3개 이상 달성한 유저
        Period lastWeekPeriod = Period.ofWeek(today.minusWeeks(1));
        List<UUID> challengeEventUsers = metricRepository.findUsersAchievedInPeriod(
                UserMetricType.CHALLENGE_COMPLETION_COUNT,
                3.0,
                lastWeekPeriod
        );
        rewardWeeklyEventUsers(WeeklyEventType.CHALLENGE_EVENT, challengeEventUsers, 50L);
    }

    private void rewardWeeklyEventUsers(WeeklyEventType eventType, List<UUID> userIds, Long rewardXp) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        String reason = String.format("[%s] 주간 이벤트 보상", getWeeklyEventTitle(eventType));
        userIds.forEach(userId -> publishXpRewardEvent(userId, reason, rewardXp));
    }

    private static String getWeeklyEventTitle(WeeklyEventType eventType) {
        return switch (eventType) {
            case WEEKEND_TRADING_TOURNAMENT -> "주말 거래 토너먼트";
            case CHALLENGE_EVENT -> "챌린지 이벤트";
        };
    }
}
