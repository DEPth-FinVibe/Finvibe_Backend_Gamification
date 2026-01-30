package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpAwardRepository;
import depth.finvibe.gamification.modules.gamification.domain.UserXpAward;

@Repository
public class UserXpAwardRepositoryImpl implements UserXpAwardRepository {

    @Override
    public void save(UserXpAward userXpAward) {

    }

    @Override
    public List<UserXpAward> findByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public List<UUID> findTopUsersByTotalXp(int limit) {
        return List.of();
    }
}
