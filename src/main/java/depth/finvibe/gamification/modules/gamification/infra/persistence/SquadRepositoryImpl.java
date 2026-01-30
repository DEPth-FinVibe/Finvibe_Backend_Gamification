package depth.finvibe.gamification.modules.gamification.infra.persistence;

import depth.finvibe.gamification.modules.gamification.application.port.out.SquadRepository;
import depth.finvibe.gamification.modules.gamification.domain.Squad;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SquadRepositoryImpl implements SquadRepository {
    @Override
    public void save(Squad squad) {

    }

    @Override
    public void delete(Squad squad) {

    }

    @Override
    public List<Squad> findAll() {
        return List.of();
    }

    @Override
    public Optional<Squad> findById(Long id) {
        return Optional.empty();
    }
}
