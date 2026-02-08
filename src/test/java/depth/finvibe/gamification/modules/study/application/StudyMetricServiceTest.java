package depth.finvibe.gamification.modules.study.application;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import depth.finvibe.gamification.boot.security.model.Requester;
import depth.finvibe.gamification.modules.study.application.port.out.LessonRepository;
import depth.finvibe.gamification.modules.study.application.port.out.StudyMetricRepository;
import depth.finvibe.gamification.modules.study.domain.StudyMetric;
import depth.finvibe.gamification.modules.study.dto.StudyMetricDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("StudyMetricService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class StudyMetricServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private StudyMetricRepository studyMetricRepository;

    @InjectMocks
    private StudyMetricService studyMetricService;

    @Test
    @DisplayName("내 학습 지표 조회 시 저장된 지표를 반환한다")
    void get_my_metric_returns_saved_metric() {
        UUID userId = UUID.randomUUID();
        Requester requester = Requester.builder()
                .uuid(userId)
                .build();
        Instant lastPingAt = Instant.parse("2026-02-09T09:10:11Z");
        StudyMetric metric = StudyMetric.builder()
                .userId(userId)
                .xpEarned(120L)
                .timeSpentMinutes(90L)
                .lastPingAt(lastPingAt)
                .build();

        when(studyMetricRepository.findByUserId(userId)).thenReturn(Optional.of(metric));

        StudyMetricDto.MyMetricResponse result = studyMetricService.getMyMetric(requester);

        assertThat(result.getXpEarned()).isEqualTo(120L);
        assertThat(result.getTimeSpentMinutes()).isEqualTo(90L);
        assertThat(result.getLastPingAt()).isEqualTo(lastPingAt);
    }

    @Test
    @DisplayName("내 학습 지표 조회 시 데이터가 없으면 기본값을 반환한다")
    void get_my_metric_returns_default_when_metric_not_found() {
        UUID userId = UUID.randomUUID();
        Requester requester = Requester.builder()
                .uuid(userId)
                .build();

        when(studyMetricRepository.findByUserId(userId)).thenReturn(Optional.empty());

        StudyMetricDto.MyMetricResponse result = studyMetricService.getMyMetric(requester);

        assertThat(result.getXpEarned()).isEqualTo(0L);
        assertThat(result.getTimeSpentMinutes()).isEqualTo(0L);
        assertThat(result.getLastPingAt()).isNull();
    }
}
