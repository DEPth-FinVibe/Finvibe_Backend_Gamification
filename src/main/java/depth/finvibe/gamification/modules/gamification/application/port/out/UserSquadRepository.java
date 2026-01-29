package depth.finvibe.gamification.modules.gamification.application.port.out;

import depth.finvibe.gamification.modules.gamification.domain.UserSquad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSquadRepository extends JpaRepository<UserSquad, UUID> {
    Optional<UserSquad> findByUserId(UUID userId);
    List<UserSquad> findAllBySquadId(Long squadId);
}
