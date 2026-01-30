package depth.finvibe.gamification.modules.gamification.application;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import depth.finvibe.gamification.modules.gamification.application.port.in.MetricCommandUseCase;
import depth.finvibe.gamification.modules.gamification.domain.enums.MetricEventType;
import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import depth.finvibe.gamification.modules.gamification.domain.error.GamificationErrorCode;
import depth.finvibe.gamification.shared.error.DomainException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@DisplayName("MetricEventService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MetricEventServiceTest {

    @Mock
    private MetricCommandUseCase metricCommandUseCase;

    @InjectMocks
    private MetricEventService metricEventService;

    @Test
    @DisplayName("로그인 이벤트는 로그인 카운트와 마지막 로그인 시간을 갱신한다")
    void login_event_updates_login_metrics() {
        UUID userId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        metricEventService.updateUserMetricByEventType(MetricEventType.LOGIN, userId, null, occurredAt);

        verify(metricCommandUseCase).updateUserMetric(UserMetricType.LOGIN_COUNT_PER_DAY, userId, 1.0, occurredAt);
        verify(metricCommandUseCase).updateUserMetric(UserMetricType.LAST_LOGIN_DATETIME, userId, null, occurredAt);
    }

    @Test
    @DisplayName("보유 종목 변경 이벤트는 증분 값이 필요하다")
    void holding_stock_count_changed_requires_delta() {
        UUID userId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        assertThatThrownBy(() -> metricEventService.updateUserMetricByEventType(
                MetricEventType.HOLDING_STOCK_COUNT_CHANGED,
                userId,
                null,
                occurredAt))
                .isInstanceOf(DomainException.class)
                .extracting("errorCode")
                .isEqualTo(GamificationErrorCode.INVALID_METRIC_DELTA);
    }
}
