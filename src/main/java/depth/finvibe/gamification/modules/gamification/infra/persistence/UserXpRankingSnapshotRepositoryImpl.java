package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpRankingSnapshotRepository;
import depth.finvibe.gamification.modules.gamification.domain.UserXpRankingSnapshot;
import depth.finvibe.gamification.modules.gamification.domain.enums.RankingPeriod;

@Repository
@RequiredArgsConstructor
public class UserXpRankingSnapshotRepositoryImpl implements UserXpRankingSnapshotRepository {
    private final UserXpRankingSnapshotJpaRepository userXpRankingSnapshotJpaRepository;

    @Override
    @Transactional
    public void replaceSnapshots(
            RankingPeriod periodType,
            LocalDate periodStartDate,
            List<UserXpRankingSnapshot> snapshots) {
        userXpRankingSnapshotJpaRepository.deleteByPeriodTypeAndPeriodStartDate(periodType, periodStartDate);

        if (!snapshots.isEmpty()) {
            userXpRankingSnapshotJpaRepository.saveAll(snapshots);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserXpRankingSnapshot> findTopByPeriod(RankingPeriod periodType, LocalDate periodStartDate, int size) {
        int pageSize = Math.max(size, 1);
        return userXpRankingSnapshotJpaRepository.findByPeriodTypeAndPeriodStartDateOrderByRankingAsc(
                periodType,
                periodStartDate,
                PageRequest.of(0, pageSize));
    }
}
