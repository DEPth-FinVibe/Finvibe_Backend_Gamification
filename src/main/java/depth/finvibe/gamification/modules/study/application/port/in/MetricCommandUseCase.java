package depth.finvibe.gamification.modules.study.application.port.in;

import depth.finvibe.gamification.boot.security.model.Requester;

public interface MetricCommandUseCase {
    void oneMinutePing(Requester requester, Long lessonId);
}
