package depth.finvibe.gamification.modules.gamification.application.port.out;

import java.util.List;
import java.util.UUID;

import depth.finvibe.gamification.modules.gamification.domain.BadgeOwnership;
import depth.finvibe.gamification.modules.gamification.domain.enums.Badge;

public interface BadgeOwnershipRepository {
    void save(BadgeOwnership badgeOwnership);

    boolean isExist(BadgeOwnership badgeOwnership);

    List<BadgeOwnership> findByUserId(UUID userId);

    long countByBadge(Badge badge);

    List<UUID> findUserIdsByBadge(Badge badge);

    List<BadgeOwnership> findRecentBadges(int limit);
}
