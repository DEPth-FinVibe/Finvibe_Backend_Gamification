package depth.finvibe.gamification.modules.study.application.port.out;

import depth.finvibe.gamification.shared.dto.UserMetricUpdatedEvent;

public interface UserMetricUpdatedEventPublisher {
    void publishUserMetricUpdatedEvent(UserMetricUpdatedEvent event);
}
