package depth.finvibe.gamification.modules.gamification.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import depth.finvibe.gamification.modules.gamification.domain.Squad;

public interface SquadJpaRepository extends JpaRepository<Squad, Long> {
}
