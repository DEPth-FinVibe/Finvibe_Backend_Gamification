package depth.finvibe.gamification.modules.gamification.application.port.out;

import depth.finvibe.gamification.modules.gamification.domain.UserXp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserXpRepository extends JpaRepository<UserXp, UUID> {
    Optional<UserXp> findByUserId(UUID userId);
    List<UserXp> findAllByUserIdInOrderByWeeklyXpDesc(List<UUID> userIds);
}
