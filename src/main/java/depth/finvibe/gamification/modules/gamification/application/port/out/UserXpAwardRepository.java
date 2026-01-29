package depth.finvibe.gamification.modules.gamification.application.port.out;

import depth.finvibe.gamification.modules.gamification.domain.UserXpAward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserXpAwardRepository extends JpaRepository<UserXpAward, Long> {
    List<UserXpAward> findByUserId(UUID userId);

    /**
     * XP 총합 기준 상위 N명의 사용자 ID를 조회
     *
     * @param limit 상위 N명
     * @return 상위 N명의 사용자 ID 목록 (XP 총합 내림차순)
     */
    List<UUID> findTopUsersByTotalXp(int limit);
}