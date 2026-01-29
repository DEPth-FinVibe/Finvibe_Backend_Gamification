package depth.finvibe.gamification.modules.gamification.application.port.out;

import depth.finvibe.gamification.modules.gamification.domain.SquadRankingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SquadRankingHistoryRepository extends JpaRepository<SquadRankingHistory, Long> {
    Optional<SquadRankingHistory> findFirstBySquadIdOrderByRecordDateDescIdDesc(Long squadId);
}
