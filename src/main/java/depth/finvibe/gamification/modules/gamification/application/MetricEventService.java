package depth.finvibe.gamification.modules.gamification.application;

import java.time.Instant;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import depth.finvibe.gamification.modules.gamification.application.port.in.MetricCommandUseCase;
import depth.finvibe.gamification.modules.gamification.application.port.in.MetricEventCommandUseCase;
import depth.finvibe.gamification.modules.gamification.domain.enums.MetricEventType;
import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import depth.finvibe.gamification.modules.gamification.domain.error.GamificationErrorCode;
import depth.finvibe.gamification.shared.error.DomainException;

@Service
@RequiredArgsConstructor
public class MetricEventService implements MetricEventCommandUseCase {

    private final MetricCommandUseCase metricCommandUseCase;

    @Override
    public void updateUserMetricByEventType(MetricEventType eventType, UUID userId, Double delta, Instant occurredAt) {
        if (eventType == null) {
            throw new DomainException(GamificationErrorCode.INVALID_METRIC_TYPE);
        }

        switch (eventType) {
            case LOGIN -> {
                metricCommandUseCase.updateUserMetric(
                        UserMetricType.LOGIN_COUNT_PER_DAY,
                        userId,
                        getOrDefaultDelta(delta, 1.0),
                        occurredAt);
                metricCommandUseCase.updateUserMetric(
                        UserMetricType.LAST_LOGIN_DATETIME,
                        userId,
                        null,
                        occurredAt);
            }
            case AI_CONTENT_COMPLETED -> metricCommandUseCase.updateUserMetric(
                    UserMetricType.AI_CONTENT_COMPLETE_COUNT,
                    userId,
                    getOrDefaultDelta(delta, 1.0),
                    occurredAt);
            case HOLDING_STOCK_COUNT_CHANGED -> metricCommandUseCase.updateUserMetric(
                    UserMetricType.HOLDING_STOCK_COUNT,
                    userId,
                    requireDelta(delta),
                    occurredAt);
            case CHALLENGE_COMPLETED -> metricCommandUseCase.updateUserMetric(
                    UserMetricType.CHALLENGE_COMPLETION_COUNT,
                    userId,
                    getOrDefaultDelta(delta, 1.0),
                    occurredAt);
        }
    }

    private double getOrDefaultDelta(Double delta, double defaultValue) {
        return delta == null ? defaultValue : delta;
    }

    private double requireDelta(Double delta) {
        if (delta == null) {
            throw new DomainException(GamificationErrorCode.INVALID_METRIC_DELTA);
        }
        return delta;
    }
}
