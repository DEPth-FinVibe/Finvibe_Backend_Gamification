package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import depth.finvibe.gamification.modules.gamification.domain.UserXpRankingSnapshot;
import depth.finvibe.gamification.modules.gamification.domain.enums.RankingPeriod;

public interface UserXpRankingSnapshotJpaRepository extends JpaRepository<UserXpRankingSnapshot, Long> {
    void deleteByPeriodTypeAndPeriodStartDate(RankingPeriod periodType, LocalDate periodStartDate);

    List<UserXpRankingSnapshot> findByPeriodTypeAndPeriodStartDateOrderByRankingAsc(
            RankingPeriod periodType,
            LocalDate periodStartDate,
            Pageable pageable);
}
