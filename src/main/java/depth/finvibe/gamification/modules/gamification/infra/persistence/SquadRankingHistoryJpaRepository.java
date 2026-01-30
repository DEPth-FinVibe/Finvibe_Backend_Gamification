package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import depth.finvibe.gamification.modules.gamification.domain.SquadRankingHistory;

public interface SquadRankingHistoryJpaRepository extends JpaRepository<SquadRankingHistory, Long> {
    Optional<SquadRankingHistory> findFirstBySquadIdOrderByRecordDateDescIdDesc(Long squadId);
}
