package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import depth.finvibe.gamification.modules.gamification.application.port.out.SquadRankingHistoryRepository;
import depth.finvibe.gamification.modules.gamification.domain.SquadRankingHistory;

@Repository
public class SquadRankingHistoryRepositoryImpl implements SquadRankingHistoryRepository {

    @Override
    public void save(SquadRankingHistory history) {

    }

    @Override
    public Optional<SquadRankingHistory> findFirstBySquadIdOrderByRecordDateDescIdDesc(Long squadId) {
        return Optional.empty();
    }
}
