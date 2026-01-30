package depth.finvibe.gamification.modules.gamification.application;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import depth.finvibe.gamification.modules.gamification.application.port.in.BadgeCommandUseCase;
import depth.finvibe.gamification.modules.gamification.application.port.out.BadgeOwnershipRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.MetricRepository;
import depth.finvibe.gamification.modules.gamification.domain.BadgeOwnership;
import depth.finvibe.gamification.modules.gamification.domain.UserMetric;
import depth.finvibe.gamification.modules.gamification.domain.enums.Badge;
import depth.finvibe.gamification.modules.gamification.domain.enums.CollectPeriod;
import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("MetricService 단위 테스트")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MetricServiceTest {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Mock
    private MetricRepository metricRepository;

    @Mock
    private BadgeOwnershipRepository badgeOwnershipRepository;

    @Mock
    private BadgeCommandUseCase badgeCommandUseCase;

    @InjectMocks
    private MetricService metricService;

    @Test
    @DisplayName("메트릭 업데이트 시 지식 탐구자 배지를 지급한다")
    void update_metric_grants_knowledge_seeker_badge() {
        UUID userId = UUID.randomUUID();

        when(metricRepository.findByUserIdAndType(userId, UserMetricType.AI_CONTENT_COMPLETE_COUNT, CollectPeriod.ALLTIME))
                .thenReturn(Optional.of(UserMetric.builder()
                        .userId(userId)
                        .type(UserMetricType.AI_CONTENT_COMPLETE_COUNT)
                        .collectPeriod(CollectPeriod.ALLTIME)
                        .value(2.0)
                        .build()));
        when(metricRepository.findByUserIdAndType(userId, UserMetricType.AI_CONTENT_COMPLETE_COUNT, CollectPeriod.WEEKLY))
                .thenReturn(Optional.empty());
        when(badgeOwnershipRepository.isExist(BadgeOwnership.of(Badge.KNOWLEDGE_SEEKER, userId)))
                .thenReturn(false);

        metricService.updateUserMetric(UserMetricType.AI_CONTENT_COMPLETE_COUNT, userId, 1.0, Instant.now());

        ArgumentCaptor<UserMetric> captor = ArgumentCaptor.forClass(UserMetric.class);
        verify(metricRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues()).anyMatch(metric ->
                metric.getCollectPeriod() == CollectPeriod.ALLTIME && metric.getValue().equals(3.0));
        verify(badgeCommandUseCase).grantBadgeToUser(userId, Badge.KNOWLEDGE_SEEKER);
    }

    @Test
    @DisplayName("연속 접속은 마지막 접속일 기준으로 증가한다")
    void update_login_streak_increments_when_consecutive_day() {
        UUID userId = UUID.randomUUID();
        Instant lastLogin = LocalDateTime.of(2026, 1, 29, 12, 0)
                .atZone(KST)
                .toInstant();
        Instant currentLogin = LocalDateTime.of(2026, 1, 30, 9, 0)
                .atZone(KST)
                .toInstant();

        when(metricRepository.findByUserIdAndType(userId, UserMetricType.LAST_LOGIN_DATETIME, CollectPeriod.ALLTIME))
                .thenReturn(Optional.of(UserMetric.builder()
                        .userId(userId)
                        .type(UserMetricType.LAST_LOGIN_DATETIME)
                        .collectPeriod(CollectPeriod.ALLTIME)
                        .value((double) lastLogin.toEpochMilli())
                        .build()));
        when(metricRepository.findByUserIdAndType(userId, UserMetricType.LOGIN_STREAK_DAYS, CollectPeriod.ALLTIME))
                .thenReturn(Optional.of(UserMetric.builder()
                        .userId(userId)
                        .type(UserMetricType.LOGIN_STREAK_DAYS)
                        .collectPeriod(CollectPeriod.ALLTIME)
                        .value(6.0)
                        .build()));
        when(badgeOwnershipRepository.isExist(BadgeOwnership.of(Badge.DILIGENT_INVESTOR, userId)))
                .thenReturn(false);

        metricService.updateUserMetric(UserMetricType.LAST_LOGIN_DATETIME, userId, null, currentLogin);

        ArgumentCaptor<UserMetric> captor = ArgumentCaptor.forClass(UserMetric.class);
        verify(metricRepository, times(2)).save(captor.capture());
        List<UserMetric> saved = captor.getAllValues();

        assertThat(saved).anyMatch(metric ->
                metric.getType() == UserMetricType.LOGIN_STREAK_DAYS && metric.getValue().equals(7.0));
        assertThat(saved).anyMatch(metric ->
                metric.getType() == UserMetricType.LAST_LOGIN_DATETIME);
        verify(badgeCommandUseCase).grantBadgeToUser(userId, Badge.DILIGENT_INVESTOR);
    }

    @Test
    @DisplayName("연속 접속 당일 재접속은 스트릭을 증가시키지 않는다")
    void update_login_streak_ignores_same_day_login() {
        UUID userId = UUID.randomUUID();
        Instant lastLogin = LocalDateTime.of(2026, 1, 30, 8, 0)
                .atZone(KST)
                .toInstant();
        Instant currentLogin = LocalDateTime.of(2026, 1, 30, 20, 0)
                .atZone(KST)
                .toInstant();

        when(metricRepository.findByUserIdAndType(userId, UserMetricType.LAST_LOGIN_DATETIME, CollectPeriod.ALLTIME))
                .thenReturn(Optional.of(UserMetric.builder()
                        .userId(userId)
                        .type(UserMetricType.LAST_LOGIN_DATETIME)
                        .collectPeriod(CollectPeriod.ALLTIME)
                        .value((double) lastLogin.toEpochMilli())
                        .build()));
        metricService.updateUserMetric(UserMetricType.LAST_LOGIN_DATETIME, userId, null, currentLogin);

        verify(metricRepository, times(1)).save(any(UserMetric.class));
        verify(badgeCommandUseCase, never()).grantBadgeToUser(eq(userId), any(Badge.class));
    }
}
