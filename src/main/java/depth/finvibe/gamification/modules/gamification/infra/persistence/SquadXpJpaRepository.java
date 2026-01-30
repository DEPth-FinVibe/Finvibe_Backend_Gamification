package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import depth.finvibe.gamification.modules.gamification.domain.SquadXp;

public interface SquadXpJpaRepository extends JpaRepository<SquadXp, Long> {
    Optional<SquadXp> findBySquadId(Long squadId);

    List<SquadXp> findAllByOrderByTotalXpDesc();
}
