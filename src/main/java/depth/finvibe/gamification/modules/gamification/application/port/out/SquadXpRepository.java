package depth.finvibe.gamification.modules.gamification.application.port.out;

import depth.finvibe.gamification.modules.gamification.domain.SquadXp;

import java.util.List;
import java.util.Optional;

public interface SquadXpRepository {
    void save(SquadXp squadXp);
    void saveAll(List<SquadXp> squadXps);
    Optional<SquadXp> findBySquadId(Long squadId);
    List<SquadXp> findAllByOrderByTotalXpDesc();
}
