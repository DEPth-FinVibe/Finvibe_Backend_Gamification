package depth.finvibe.gamification.modules.gamification.application;

import depth.finvibe.gamification.modules.gamification.application.port.in.ChallengeQueryUseCase;
import depth.finvibe.gamification.modules.gamification.application.port.out.MetricRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.PersonalChallengeRepository;
import depth.finvibe.gamification.modules.gamification.domain.PersonalChallenge;
import depth.finvibe.gamification.modules.gamification.domain.UserMetric;
import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import depth.finvibe.gamification.modules.gamification.domain.vo.Period;
import depth.finvibe.gamification.modules.gamification.dto.ChallengeDto;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeQueryService implements ChallengeQueryUseCase {

    private final PersonalChallengeRepository personalChallengeRepository;
    private final MetricRepository metricRepository;

    @Override
    public List<ChallengeDto.ChallengeResponse> getPersonalChallenges(UUID userId) {
        Period currentPeriod = Period.ofWeek(LocalDate.now());
        List<PersonalChallenge> challenges = personalChallengeRepository.findAllByPeriod(currentPeriod);

        Map<UserMetricType, Double> userMetrics = getRequiredMetrics(userId, challenges);

        return challenges.stream()
                .map(challenge -> {
                    Double currentValue = userMetrics.getOrDefault(challenge.getCondition().getMetricType(), 0.0);
                    return ChallengeDto.ChallengeResponse.from(challenge, currentValue);
                })
                .toList();
    }

    private @NonNull Map<UserMetricType, Double> getRequiredMetrics(UUID userId, List<PersonalChallenge> challenges) {
        // 챌린지에서 사용하는 메트릭 타입들만 추출
        List<UserMetricType> requiredTypes = challenges.stream()
                .map(challenge -> challenge.getCondition().getMetricType())
                .distinct()
                .toList();

        // 필요한 메트릭들만 한 번에 조회하여 Map으로 변환
        return metricRepository.findAllByUserIdAndTypes(userId, requiredTypes).stream()
            .collect(Collectors.toMap(
                UserMetric::getType,
                UserMetric::getValue
            ));
    }
}