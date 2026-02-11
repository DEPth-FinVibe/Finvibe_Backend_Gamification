package depth.finvibe.gamification.modules.gamification.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import depth.finvibe.gamification.modules.gamification.application.port.out.ChallengeGenerator;
import depth.finvibe.gamification.modules.gamification.application.port.out.MetricRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.PersonalChallengeRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.PersonalChallengeRewardRepository;
import depth.finvibe.gamification.shared.messaging.UserMetricUpdatedEventPublisher;
import depth.finvibe.gamification.modules.gamification.application.port.out.XpRewardEventPublisher;
import depth.finvibe.gamification.modules.gamification.domain.PersonalChallenge;
import depth.finvibe.gamification.modules.gamification.domain.PersonalChallengeReward;
import depth.finvibe.gamification.modules.gamification.domain.enums.CollectPeriod;
import depth.finvibe.gamification.modules.gamification.domain.enums.MetricEventType;
import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import depth.finvibe.gamification.modules.gamification.domain.vo.ChallengeCondition;
import depth.finvibe.gamification.modules.gamification.domain.vo.Period;
import depth.finvibe.gamification.modules.gamification.domain.vo.Reward;
import depth.finvibe.gamification.modules.gamification.dto.ChallengeDto;
import depth.finvibe.gamification.shared.dto.UserMetricUpdatedEvent;
import depth.finvibe.gamification.shared.dto.XpRewardEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ChallengeService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeGenerator challengeGenerator;

    @Mock
    private PersonalChallengeRepository personalChallengeRepository;

    @Mock
    private MetricRepository metricRepository;

    @Mock
    private PersonalChallengeRewardRepository personalChallengeRewardRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private XpRewardEventPublisher xpRewardEventPublisher;

    @Mock
    private UserMetricUpdatedEventPublisher userMetricUpdatedEventPublisher;

    @InjectMocks
    private ChallengeService challengeService;

    @Test
    @DisplayName("개인 챌린지 생성 시 3개를 저장한다")
    void generate_personal_challenges_saves_three() {
        when(challengeGenerator.generate()).thenReturn(List.of(
                challengeResponse("제목1"),
                challengeResponse("제목2"),
                challengeResponse("제목3")
        ));

        challengeService.generatePersonalChallenges();

        ArgumentCaptor<List<PersonalChallenge>> captor = ArgumentCaptor.forClass(List.class);
        verify(personalChallengeRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(3);
    }

    @Test
    @DisplayName("개인 챌린지 생성 수가 3개가 아니면 예외가 발생한다")
    void generate_personal_challenges_throws_when_invalid_size() {
        when(challengeGenerator.generate()).thenReturn(List.of(
                challengeResponse("제목1"),
                challengeResponse("제목2")
        ));

        assertThatThrownBy(() -> challengeService.generatePersonalChallenges())
                .isInstanceOf(IllegalStateException.class);

        verify(personalChallengeRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("보상할 개인 챌린지가 없으면 아무 작업도 하지 않는다")
    void reward_personal_challenges_returns_when_empty() {
        when(personalChallengeRepository.findAllByPeriod(any(Period.class))).thenReturn(List.of());

        challengeService.rewardPersonalChallenges();

        verify(metricRepository, never()).findUsersAchieved(any(), any(), any());
        verify(personalChallengeRewardRepository, never()).saveAll(anyList());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("개인 챌린지 보상 시 달성 유저에게 XP와 메트릭 이벤트를 발행한다")
    void reward_personal_challenges_publishes_events() {
        PersonalChallenge challenge = PersonalChallenge.builder()
                .id(10L)
                .title("챌린지")
                .description("설명")
                .condition(ChallengeCondition.of(UserMetricType.CURRENT_RETURN_RATE, 5.0))
                .period(Period.ofWeek(LocalDate.now()))
                .reward(Reward.of(100L, null))
                .build();
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        when(personalChallengeRepository.findAllByPeriod(any(Period.class))).thenReturn(List.of(challenge));
        when(metricRepository.findUsersAchieved(UserMetricType.CURRENT_RETURN_RATE, CollectPeriod.ALLTIME, 5.0))
                .thenReturn(List.of(user1, user2));

        challengeService.rewardPersonalChallenges();

        ArgumentCaptor<List<PersonalChallengeReward>> captor = ArgumentCaptor.forClass(List.class);
        verify(personalChallengeRewardRepository, times(2)).saveAll(captor.capture());
        assertThat(captor.getAllValues().stream().anyMatch(list -> list.size() == 2)).isTrue();
        verify(applicationEventPublisher, times(2)).publishEvent(any(XpRewardEvent.class));
        verify(applicationEventPublisher, times(2)).publishEvent(any(UserMetricUpdatedEvent.class));
    }

    @Test
    @DisplayName("트랜잭션 커밋 후 Kafka 이벤트를 발행한다")
    void handle_xp_reward_event_for_kafka() {
        XpRewardEvent event = XpRewardEvent.of("user", "reason", 10L);

        challengeService.handleXpRewardEventForKafka(event);

        verify(xpRewardEventPublisher).publishXpRewardEvent(event);
    }

    @Test
    @DisplayName("트랜잭션 커밋 후 사용자 메트릭 Kafka 이벤트를 발행한다")
    void handle_user_metric_updated_event_for_kafka() {
        UserMetricUpdatedEvent event = UserMetricUpdatedEvent.builder()
                .userId("user")
                .eventType(MetricEventType.CHALLENGE_COMPLETED)
                .delta(1.0)
                .build();

        challengeService.handleUserMetricUpdatedEventForKafka(event);

        verify(userMetricUpdatedEventPublisher).publishUserMetricUpdatedEvent(event);
    }

    @Test
    @DisplayName("주간 이벤트 보상 시 대상 유저에게 XP 이벤트를 발행한다")
    void reward_weekly_challenges_publishes_events() {
        when(metricRepository.findTopUsersByMetric(eq(UserMetricType.CURRENT_RETURN_RATE), eq(CollectPeriod.ALLTIME), anyInt()))
                .thenReturn(List.of(UUID.randomUUID(), UUID.randomUUID()));
        when(metricRepository.findUsersAchieved(eq(UserMetricType.CHALLENGE_COMPLETION_COUNT), eq(CollectPeriod.WEEKLY), eq(3.0)))
                .thenReturn(List.of(UUID.randomUUID()));

        challengeService.rewardWeeklyChallenges();

        verify(applicationEventPublisher, times(3)).publishEvent(any(XpRewardEvent.class));
    }

    private ChallengeDto.ChallengeGenerationResponse challengeResponse(String title) {
        return ChallengeDto.ChallengeGenerationResponse.builder()
                .title(title)
                .description("설명")
                .metricType(UserMetricType.CURRENT_RETURN_RATE)
                .targetValue(5.0)
                .rewardXp(100L)
                .build();
    }
}
