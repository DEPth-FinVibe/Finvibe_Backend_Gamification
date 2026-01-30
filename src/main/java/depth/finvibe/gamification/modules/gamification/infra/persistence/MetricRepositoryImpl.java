package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import depth.finvibe.gamification.modules.gamification.application.port.out.MetricRepository;
import depth.finvibe.gamification.modules.gamification.domain.UserMetric;
import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import depth.finvibe.gamification.modules.gamification.domain.vo.Period;

@Repository
@RequiredArgsConstructor
public class MetricRepositoryImpl implements MetricRepository {
    private final MetricJpaRepository metricJpaRepository;

    @Override
    public List<UUID> findUsersAchieved(UserMetricType metricType, Double targetValue) {
        return metricJpaRepository.findByTypeAndValueGreaterThanEqual(metricType, targetValue).stream()
                .map(UserMetric::getUserId)
                .toList();
    }

    @Override
    public List<UUID> findTopUsersByMetric(UserMetricType metricType, int limit) {
        return metricJpaRepository.findByTypeOrderByValueDesc(metricType, PageRequest.of(0, limit)).stream()
                .map(UserMetric::getUserId)
                .toList();
    }

    @Override
    public List<UUID> findUsersAchievedInPeriod(UserMetricType metricType, Double targetValue, Period period) {
        return findUsersAchieved(metricType, targetValue);
    }

    @Override
    public Optional<UserMetric> findByUserIdAndType(UUID userId, UserMetricType type) {
        return metricJpaRepository.findByUserIdAndType(userId, type);
    }

    @Override
    public List<UserMetric> findAllByUserId(UUID userId) {
        return metricJpaRepository.findByUserId(userId);
    }

    @Override
    public List<UserMetric> findAllByUserIdAndTypes(UUID userId, List<UserMetricType> types) {
        return metricJpaRepository.findByUserIdAndTypeIn(userId, types);
    }
}
