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

    /**
     * 주간 스쿼드 랭킹을 정산하고 초기화합니다.
     * 매주 정해진 시간에 스케줄러에 의해 호출됩니다.
     */
    void updateWeeklySquadRanking();
}
