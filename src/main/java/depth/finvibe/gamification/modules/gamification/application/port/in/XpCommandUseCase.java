package depth.finvibe.gamification.modules.gamification.application.port.in;

import java.util.UUID;

public interface XpCommandUseCase {
    void grantUserXp(UUID userId, Long value, String reason);
}
