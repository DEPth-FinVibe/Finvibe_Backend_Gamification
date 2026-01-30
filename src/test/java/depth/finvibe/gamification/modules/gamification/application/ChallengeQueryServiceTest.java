package depth.finvibe.gamification.modules.gamification.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import depth.finvibe.gamification.modules.gamification.application.port.out.MetricRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.PersonalChallengeRepository;
import depth.finvibe.gamification.modules.gamification.domain.PersonalChallenge;
import depth.finvibe.gamification.modules.gamification.domain.UserMetric;
import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import depth.finvibe.gamification.modules.gamification.domain.vo.ChallengeCondition;
import depth.finvibe.gamification.modules.gamification.domain.vo.Period;
import depth.finvibe.gamification.modules.gamification.domain.vo.Reward;
import depth.finvibe.gamification.modules.gamification.dto.ChallengeDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("ChallengeQueryService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ChallengeQueryServiceTest {

    @Mock
    private PersonalChallengeRepository personalChallengeRepository;

    @Mock
    private MetricRepository metricRepository;

    @InjectMocks
    private ChallengeQueryService challengeQueryService;

    @Test
    @DisplayName("개인 챌린지 조회 시 유저 메트릭을 매핑한다")
    void get_personal_challenges_maps_metrics() {
        PersonalChallenge challengeA = PersonalChallenge.builder()
                .id(1L)
                .title("챌린지A")
                .description("설명")
                .condition(ChallengeCondition.of(UserMetricType.CURRENT_RETURN_RATE, 5.0))
                .period(Period.ofWeek(LocalDate.now()))
                .reward(Reward.of(100L, null))
                .build();
        PersonalChallenge challengeB = PersonalChallenge.builder()
                .id(2L)
                .title("챌린지B")
                .description("설명")
                .condition(ChallengeCondition.of(UserMetricType.CHALLENGE_COMPLETION_COUNT, 3.0))
                .period(Period.ofWeek(LocalDate.now()))
                .reward(Reward.of(50L, null))
                .build();
        UUID userId = UUID.randomUUID();

        when(personalChallengeRepository.findAllByPeriod(any(Period.class)))
                .thenReturn(List.of(challengeA, challengeB));
        when(metricRepository.findAllByUserIdAndTypes(any(UUID.class), any(List.class)))
                .thenReturn(List.of(
                        UserMetric.builder()
                                .type(UserMetricType.CURRENT_RETURN_RATE)
                                .userId(userId)
                                .value(7.5)
                                .build()
                ));

        List<ChallengeDto.ChallengeResponse> result = challengeQueryService.getPersonalChallenges(userId);

        assertThat(result).hasSize(2);
        ChallengeDto.ChallengeResponse responseA = result.stream()
                .filter(response -> response.getMetricType() == UserMetricType.CURRENT_RETURN_RATE)
                .findFirst()
                .orElseThrow();
        ChallengeDto.ChallengeResponse responseB = result.stream()
                .filter(response -> response.getMetricType() == UserMetricType.CHALLENGE_COMPLETION_COUNT)
                .findFirst()
                .orElseThrow();
        assertThat(responseA.getCurrentValue()).isEqualTo(7.5);
        assertThat(responseB.getCurrentValue()).isEqualTo(0.0);
    }
}
