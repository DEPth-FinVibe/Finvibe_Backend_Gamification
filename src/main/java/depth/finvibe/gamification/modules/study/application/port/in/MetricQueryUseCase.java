package depth.finvibe.gamification.modules.study.application.port.in;

import depth.finvibe.gamification.boot.security.model.Requester;
import depth.finvibe.gamification.modules.study.dto.StudyMetricDto;

public interface MetricQueryUseCase {
    StudyMetricDto.MyMetricResponse getMyMetric(Requester requester);
}
