package depth.finvibe.gamification.modules.gamification.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import depth.finvibe.gamification.modules.gamification.domain.UserMetric;
import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import depth.finvibe.gamification.modules.gamification.domain.vo.Period;

public interface MetricRepository {
    List<UUID> findUsersAchieved(UserMetricType metricType, Double targetValue);

    List<UUID> findTopUsersByMetric(UserMetricType metricType, int limit);

    List<UUID> findUsersAchievedInPeriod(UserMetricType metricType, Double targetValue, Period period);

    Optional<UserMetric> findByUserIdAndType(UUID userId, UserMetricType type);

    List<UserMetric> findAllByUserId(UUID userId);

    List<UserMetric> findAllByUserIdAndTypes(UUID userId, List<UserMetricType> types);
}
