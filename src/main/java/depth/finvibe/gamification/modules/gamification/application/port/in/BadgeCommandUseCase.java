package depth.finvibe.gamification.modules.gamification.application.port.in;

import depth.finvibe.gamification.modules.gamification.domain.enums.Badge;

import java.util.UUID;

public interface BadgeCommandUseCase {
    void grantBadgeToUser(UUID userId, Badge badge);
}
