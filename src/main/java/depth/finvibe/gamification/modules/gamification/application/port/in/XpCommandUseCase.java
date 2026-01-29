package depth.finvibe.gamification.modules.gamification.application.port.in;

import java.util.UUID;

public interface XpCommandUseCase {
    /**
     * 사용자에게 XP를 부여합니다.
     *
     * @param userId 사용자 ID
     * @param value 부여할 XP 값
     * @param reason 부여 사유
     */
    void grantUserXp(UUID userId, Long value, String reason);
}
