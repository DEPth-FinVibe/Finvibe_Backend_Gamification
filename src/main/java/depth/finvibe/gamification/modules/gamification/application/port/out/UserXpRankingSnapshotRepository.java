package depth.finvibe.gamification.modules.gamification.application.port.out;

import java.time.LocalDate;
import java.util.List;

import depth.finvibe.gamification.modules.gamification.domain.UserXpRankingSnapshot;
import depth.finvibe.gamification.modules.gamification.domain.enums.RankingPeriod;

public interface UserXpRankingSnapshotRepository {
    void replaceSnapshots(
            RankingPeriod periodType,
            LocalDate periodStartDate,
            List<UserXpRankingSnapshot> snapshots);

    List<UserXpRankingSnapshot> findTopByPeriod(
            RankingPeriod periodType,
            LocalDate periodStartDate,
            int size);
}
