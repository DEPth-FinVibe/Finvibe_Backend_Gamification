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
import depth.finvibe.gamification.modules.gamification.application.port.out.PersonalChallengeRewardRepository;
import depth.finvibe.gamification.modules.gamification.domain.PersonalChallenge;
import depth.finvibe.gamification.modules.gamification.domain.PersonalChallengeReward;
import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import depth.finvibe.gamification.modules.gamification.domain.vo.ChallengeCondition;
import depth.finvibe.gamification.modules.gamification.domain.vo.Period;
import depth.finvibe.gamification.modules.gamification.domain.vo.Reward;
import depth.finvibe.gamification.modules.gamification.dto.ChallengeDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChallengeQueryServiceCompletedTest {

    @InjectMocks
    private ChallengeQueryService challengeQueryService;

    @Mock
    private PersonalChallengeRepository personalChallengeRepository;

    @Mock
    private PersonalChallengeRewardRepository personalChallengeRewardRepository;

    @Mock
    private MetricRepository metricRepository;

    @Test
    @DisplayName("월별 챌린지 완료 내역 조회 성공")
    void get_completed_challenges_success() {
        // Given
        UUID userId = UUID.randomUUID();
        int year = 2025;
        int month = 1;

        Period weekPeriod = Period.ofWeek(LocalDate.of(2025, 1, 6));
        PersonalChallenge challenge = PersonalChallenge.builder()
                .id(1L)
                .title("주간 AI 콘텐츠 챌린지")
                .description("이번 주 5회 AI 콘텐츠 완료하기")
                .condition(ChallengeCondition.of(UserMetricType.AI_CONTENT_COMPLETE_COUNT, 5.0))
                .period(weekPeriod)
                .reward(Reward.of(100L, null))
                .build();

        PersonalChallengeReward reward = PersonalChallengeReward.builder()
                .id(1L)
                .challengeId(1L)
                .userId(userId)
                .period(weekPeriod)
                .reward(Reward.of(100L, null))
                .build();

        when(personalChallengeRewardRepository.findAllByUserIdAndPeriod(any(UUID.class), any(Period.class)))
                .thenReturn(List.of(reward));
        when(personalChallengeRepository.findAllByIds(anyList()))
                .thenReturn(List.of(challenge));

        // When
        List<ChallengeDto.ChallengeHistoryResponse> result = challengeQueryService.getCompletedChallenges(userId, year, month);

        // Then
        assertThat(result).hasSize(1);
        ChallengeDto.ChallengeHistoryResponse response = result.get(0);
        assertThat(response.getChallengeId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("주간 AI 콘텐츠 챌린지");
        assertThat(response.getDescription()).isEqualTo("이번 주 5회 AI 콘텐츠 완료하기");
        assertThat(response.getMetricType()).isEqualTo(UserMetricType.AI_CONTENT_COMPLETE_COUNT);
        assertThat(response.getTargetValue()).isEqualTo(5.0);
        assertThat(response.getRewardXp()).isEqualTo(100L);
        assertThat(response.getStartDate()).isEqualTo(weekPeriod.getStartDate());
        assertThat(response.getEndDate()).isEqualTo(weekPeriod.getEndDate());
    }

    @Test
    @DisplayName("완료 내역이 없으면 빈 리스트 반환")
    void get_completed_challenges_empty() {
        // Given
        UUID userId = UUID.randomUUID();
        int year = 2025;
        int month = 1;

        when(personalChallengeRewardRepository.findAllByUserIdAndPeriod(any(UUID.class), any(Period.class)))
                .thenReturn(List.of());

        // When
        List<ChallengeDto.ChallengeHistoryResponse> result = challengeQueryService.getCompletedChallenges(userId, year, month);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("여러 개의 완료 내역 조회 성공")
    void get_completed_challenges_multiple() {
        // Given
        UUID userId = UUID.randomUUID();
        int year = 2025;
        int month = 1;

        Period week1 = Period.ofWeek(LocalDate.of(2025, 1, 6));
        Period week2 = Period.ofWeek(LocalDate.of(2025, 1, 13));

        PersonalChallenge challenge1 = PersonalChallenge.builder()
                .id(1L)
                .title("챌린지 1")
                .description("설명 1")
                .condition(ChallengeCondition.of(UserMetricType.AI_CONTENT_COMPLETE_COUNT, 5.0))
                .period(week1)
                .reward(Reward.of(100L, null))
                .build();

        PersonalChallenge challenge2 = PersonalChallenge.builder()
                .id(2L)
                .title("챌린지 2")
                .description("설명 2")
                .condition(ChallengeCondition.of(UserMetricType.DISCUSSION_POST_COUNT, 3.0))
                .period(week2)
                .reward(Reward.of(150L, null))
                .build();

        PersonalChallengeReward reward1 = PersonalChallengeReward.builder()
                .id(1L)
                .challengeId(1L)
                .userId(userId)
                .period(week1)
                .reward(Reward.of(100L, null))
                .build();

        PersonalChallengeReward reward2 = PersonalChallengeReward.builder()
                .id(2L)
                .challengeId(2L)
                .userId(userId)
                .period(week2)
                .reward(Reward.of(150L, null))
                .build();

        when(personalChallengeRewardRepository.findAllByUserIdAndPeriod(any(UUID.class), any(Period.class)))
                .thenReturn(List.of(reward1, reward2));
        when(personalChallengeRepository.findAllByIds(anyList()))
                .thenReturn(List.of(challenge1, challenge2));

        // When
        List<ChallengeDto.ChallengeHistoryResponse> result = challengeQueryService.getCompletedChallenges(userId, year, month);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getChallengeId()).isEqualTo(1L);
        assertThat(result.get(1).getChallengeId()).isEqualTo(2L);
    }
}
