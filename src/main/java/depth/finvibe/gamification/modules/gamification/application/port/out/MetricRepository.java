package depth.finvibe.gamification.modules.gamification.application.port.out;

import java.util.List;
import java.util.UUID;

import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import depth.finvibe.gamification.modules.gamification.domain.vo.Period;

public interface MetricRepository {
    List<UUID> findUsersAchieved(UserMetricType metricType, Double targetValue);

    /**
     * 특정 메트릭의 현재 값 기준 상위 N명의 사용자 ID를 조회
     *
     * @param metricType 메트릭 타입
     * @param limit 상위 N명
     * @return 상위 N명의 사용자 ID 목록
     */
    List<UUID> findTopUsersByMetric(UserMetricType metricType, int limit);

    /**
     * 특정 기간 동안 목표값 이상을 달성한 사용자 ID를 조회
     *
     * @param metricType 메트릭 타입
     * @param targetValue 목표값
     * @param period 조회 기간
     * @return 목표값 이상을 달성한 사용자 ID 목록
     */
    List<UUID> findUsersAchievedInPeriod(UserMetricType metricType, Double targetValue, Period period);
}
