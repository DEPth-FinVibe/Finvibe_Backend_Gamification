package depth.finvibe.gamification.modules.gamification.application.port.out;

import depth.finvibe.gamification.modules.gamification.domain.BadgeOwnership;

import java.util.List;
import java.util.UUID;

public interface BadgeOwnershipRepository {
    void save(BadgeOwnership badgeOwnership);

    boolean isExist(BadgeOwnership badgeOwnership);

    List<BadgeOwnership> findByUserId(UUID userId);
}
