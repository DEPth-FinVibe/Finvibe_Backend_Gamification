package depth.finvibe.gamification.modules.gamification.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import depth.finvibe.gamification.modules.gamification.application.port.out.BadgeOwnershipRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.MetricRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpRankingSnapshotRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpRepository;
import depth.finvibe.gamification.modules.gamification.domain.BadgeOwnership;
import depth.finvibe.gamification.modules.gamification.domain.UserMetric;
import depth.finvibe.gamification.modules.gamification.domain.UserXp;
import depth.finvibe.gamification.modules.gamification.domain.UserXpRankingSnapshot;
import depth.finvibe.gamification.modules.gamification.domain.enums.Badge;
import depth.finvibe.gamification.modules.gamification.domain.enums.CollectPeriod;
import depth.finvibe.gamification.modules.gamification.domain.enums.RankingPeriod;
import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import depth.finvibe.gamification.modules.gamification.dto.InternalGamificationDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("InternalGamificationQueryService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class InternalGamificationQueryServiceTest {

    @Mock
    private BadgeOwnershipRepository badgeOwnershipRepository;

    @Mock
    private UserXpRepository userXpRepository;

    @Mock
    private UserXpRankingSnapshotRepository userXpRankingSnapshotRepository;

    @Mock
    private MetricRepository metricRepository;

    @InjectMocks
    private InternalGamificationQueryService internalGamificationQueryService;

    @Test
    @DisplayName("유저 요약 조회 시 보유 뱃지, 순위, XP, 수익률을 반환한다")
    void get_user_summary_returns_badges_ranking_xp_and_return_rate() {
        UUID userId = UUID.randomUUID();
        BadgeOwnership badgeOwnership = BadgeOwnership.of(Badge.FIRST_PROFIT, userId);
        UserXp userXp = UserXp.of(userId, "테스터");
        userXp.addXp(1500L);
        UserMetric returnRateMetric = UserMetric.builder()
                .userId(userId)
                .type(UserMetricType.CURRENT_RETURN_RATE)
                .collectPeriod(CollectPeriod.ALLTIME)
                .value(12.5)
                .build();

        LocalDate periodStart = LocalDate.now(ZoneId.of("Asia/Seoul"))
                .minusDays(LocalDate.now(ZoneId.of("Asia/Seoul")).getDayOfWeek().getValue() - 1L);
        UserXpRankingSnapshot snapshot = UserXpRankingSnapshot.of(
                RankingPeriod.WEEKLY,
                periodStart,
                periodStart.plusDays(6),
                userId,
                "테스터",
                3,
                1500L,
                500L,
                200L,
                150.0,
                LocalDateTime.now(ZoneId.of("Asia/Seoul")));

        when(badgeOwnershipRepository.findByUserId(userId)).thenReturn(List.of(badgeOwnership));
        when(userXpRepository.findByUserId(userId)).thenReturn(Optional.of(userXp));
        when(userXpRankingSnapshotRepository.findByPeriodAndUserId(eq(RankingPeriod.WEEKLY), any(LocalDate.class), eq(userId)))
                .thenReturn(Optional.of(snapshot));
        when(metricRepository.findByUserIdAndType(
                userId,
                UserMetricType.CURRENT_RETURN_RATE,
                CollectPeriod.ALLTIME)).thenReturn(Optional.of(returnRateMetric));

        InternalGamificationDto.UserSummaryResponse response = internalGamificationQueryService.getUserSummary(userId);

        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getBadges()).hasSize(1);
        assertThat(response.getBadges().get(0).getBadge()).isEqualTo(Badge.FIRST_PROFIT);
        assertThat(response.getRanking()).isEqualTo(3);
        assertThat(response.getTotalXp()).isEqualTo(1500L);
        assertThat(response.getCurrentReturnRate()).isEqualTo(12.5);
    }

    @Test
    @DisplayName("유저 데이터가 없으면 기본값으로 반환한다")
    void get_user_summary_returns_default_values_when_data_missing() {
        UUID userId = UUID.randomUUID();

        when(badgeOwnershipRepository.findByUserId(userId)).thenReturn(List.of());
        when(userXpRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userXpRankingSnapshotRepository.findByPeriodAndUserId(eq(RankingPeriod.WEEKLY), any(LocalDate.class), eq(userId)))
                .thenReturn(Optional.empty());
        when(metricRepository.findByUserIdAndType(
                userId,
                UserMetricType.CURRENT_RETURN_RATE,
                CollectPeriod.ALLTIME)).thenReturn(Optional.empty());

        InternalGamificationDto.UserSummaryResponse response = internalGamificationQueryService.getUserSummary(userId);

        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getBadges()).isEmpty();
        assertThat(response.getRanking()).isNull();
        assertThat(response.getTotalXp()).isEqualTo(0L);
        assertThat(response.getCurrentReturnRate()).isNull();
    }

    @Test
    @DisplayName("유저 요약 조회 시 주간 스냅샷 기준으로 순위를 조회한다")
    void get_user_summary_reads_ranking_by_weekly_snapshot() {
        UUID userId = UUID.randomUUID();

        when(badgeOwnershipRepository.findByUserId(userId)).thenReturn(List.of());
        when(userXpRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userXpRankingSnapshotRepository.findByPeriodAndUserId(eq(RankingPeriod.WEEKLY), any(LocalDate.class), eq(userId)))
                .thenReturn(Optional.empty());
        when(metricRepository.findByUserIdAndType(
                userId,
                UserMetricType.CURRENT_RETURN_RATE,
                CollectPeriod.ALLTIME)).thenReturn(Optional.empty());

        internalGamificationQueryService.getUserSummary(userId);

        verify(userXpRankingSnapshotRepository).findByPeriodAndUserId(eq(RankingPeriod.WEEKLY), any(LocalDate.class), eq(userId));
    }
}
