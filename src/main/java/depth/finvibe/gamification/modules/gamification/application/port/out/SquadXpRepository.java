package depth.finvibe.gamification.modules.gamification.application.port.out;

import depth.finvibe.gamification.modules.gamification.domain.SquadXp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SquadXpRepository extends JpaRepository<SquadXp, Long> {
    Optional<SquadXp> findBySquadId(Long squadId);
    List<SquadXp> findAllByOrderByTotalXpDesc();
}
