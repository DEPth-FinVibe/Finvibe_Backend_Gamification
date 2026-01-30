package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpRepository;
import depth.finvibe.gamification.modules.gamification.domain.UserXp;

@Repository
public class UserXpRepositoryImpl implements UserXpRepository {

    @Override
    public void save(UserXp userXp) {

    }

    @Override
    public void saveAll(List<UserXp> userXps) {

    }

    @Override
    public List<UserXp> findAll() {
        return List.of();
    }

    @Override
    public Optional<UserXp> findByUserId(UUID userId) {
        return Optional.empty();
    }

    @Override
    public List<UserXp> findAllByUserIdInOrderByWeeklyXpDesc(List<UUID> userIds) {
        return List.of();
    }
}
