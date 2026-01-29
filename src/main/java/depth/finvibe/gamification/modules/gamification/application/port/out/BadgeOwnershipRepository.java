package depth.finvibe.gamification.modules.gamification.application.port.out;

import depth.finvibe.gamification.modules.gamification.domain.BadgeOwnership;

public interface BadgeOwnershipRepository {
    void save(BadgeOwnership badgeOwnership);

    boolean isExist(BadgeOwnership badgeOwnership);
}
