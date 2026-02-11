package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import depth.finvibe.gamification.modules.gamification.domain.UserXpRankingSnapshot;
import depth.finvibe.gamification.modules.gamification.domain.enums.RankingPeriod;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("UserXpRankingSnapshotRepositoryImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserXpRankingSnapshotRepositoryImplTest {

    @Mock
    private UserXpRankingSnapshotJpaRepository userXpRankingSnapshotJpaRepository;

    @Test
    @DisplayName("스냅샷 교체 시 삭제 후 저장을 수행한다")
    void replace_snapshots_deletes_then_saves() {
        UserXpRankingSnapshotRepositoryImpl repository =
                new UserXpRankingSnapshotRepositoryImpl(userXpRankingSnapshotJpaRepository);
        LocalDate periodStartDate = LocalDate.of(2026, 2, 9);
        UserXpRankingSnapshot snapshot = UserXpRankingSnapshot.of(
                RankingPeriod.WEEKLY,
                periodStartDate,
                periodStartDate.plusDays(6),
                UUID.randomUUID(),
                "유저",
                1,
                1000L,
                100L,
                50L,
                100.0,
                java.time.LocalDateTime.now());

        repository.replaceSnapshots(RankingPeriod.WEEKLY, periodStartDate, List.of(snapshot));

        InOrder inOrder = inOrder(userXpRankingSnapshotJpaRepository);
        inOrder.verify(userXpRankingSnapshotJpaRepository)
                .deleteByPeriodTypeAndPeriodStartDate(RankingPeriod.WEEKLY, periodStartDate);
        inOrder.verify(userXpRankingSnapshotJpaRepository).saveAllAndFlush(List.of(snapshot));
    }

    @Test
    @DisplayName("스냅샷 목록이 비어있으면 삭제만 수행한다")
    void replace_snapshots_deletes_only_when_empty() {
        UserXpRankingSnapshotRepositoryImpl repository =
                new UserXpRankingSnapshotRepositoryImpl(userXpRankingSnapshotJpaRepository);
        LocalDate periodStartDate = LocalDate.of(2026, 2, 9);

        repository.replaceSnapshots(RankingPeriod.WEEKLY, periodStartDate, List.of());

        verify(userXpRankingSnapshotJpaRepository)
                .deleteByPeriodTypeAndPeriodStartDate(RankingPeriod.WEEKLY, periodStartDate);
        verify(userXpRankingSnapshotJpaRepository, never()).saveAllAndFlush(anyList());
    }
}
