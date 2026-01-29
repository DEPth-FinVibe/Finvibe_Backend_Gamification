package depth.finvibe.gamification.modules.gamification.application.port.out;

import depth.finvibe.gamification.modules.gamification.domain.Squad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SquadRepository extends JpaRepository<Squad, Long> {
}
