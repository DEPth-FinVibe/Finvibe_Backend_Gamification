package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import depth.finvibe.gamification.modules.gamification.application.port.out.SquadXpRepository;
import depth.finvibe.gamification.modules.gamification.domain.SquadXp;

@Repository
public class SquadXpRepositoryImpl implements SquadXpRepository {

    @Override
    public void save(SquadXp squadXp) {

    }

    @Override
    public void saveAll(List<SquadXp> squadXps) {

    }

    @Override
    public Optional<SquadXp> findBySquadId(Long squadId) {
        return Optional.empty();
    }

    @Override
    public List<SquadXp> findAllByOrderByTotalXpDesc() {
        return List.of();
    }
}
