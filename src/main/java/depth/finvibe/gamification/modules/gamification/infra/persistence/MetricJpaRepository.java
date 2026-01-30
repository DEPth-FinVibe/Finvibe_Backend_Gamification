package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import depth.finvibe.gamification.modules.gamification.domain.UserMetric;
import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import depth.finvibe.gamification.modules.gamification.domain.idclass.UserMetricId;

public interface MetricJpaRepository extends JpaRepository<UserMetric, UserMetricId> {
    List<UserMetric> findByTypeAndValueGreaterThanEqual(UserMetricType type, Double value);

    List<UserMetric> findByTypeOrderByValueDesc(UserMetricType type, Pageable pageable);

    Optional<UserMetric> findByUserIdAndType(UUID userId, UserMetricType type);

    List<UserMetric> findByUserId(UUID userId);

    List<UserMetric> findByUserIdAndTypeIn(UUID userId, List<UserMetricType> types);
}
