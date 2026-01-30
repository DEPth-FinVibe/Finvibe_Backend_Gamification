package depth.finvibe.gamification.modules.gamification.infra.persistence;

import depth.finvibe.gamification.modules.gamification.application.port.out.UserSquadRepository;
import depth.finvibe.gamification.modules.gamification.domain.UserSquad;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserSquadRepositoryImpl implements UserSquadRepository {
    @Override
    public void save(UserSquad userSquad) {

    }

    @Override
    public Optional<UserSquad> findByUserId(UUID userId) {
        return Optional.empty();
    }

    @Override
    public List<UserSquad> findAllBySquadId(Long squadId) {
        return List.of();
    }
}
