package depth.finvibe.gamification.modules.gamification.infra.messaging;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import depth.finvibe.gamification.modules.gamification.application.port.out.UserMetricUpdatedEventPublisher;
import depth.finvibe.gamification.shared.dto.UserMetricUpdatedEvent;

@Component
@RequiredArgsConstructor
public class UserMetricUpdatedEventPublisherImpl implements UserMetricUpdatedEventPublisher {
    private static final String UPDATE_USER_METRIC_TOPIC = "gamification.update-user-metric.v1";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishUserMetricUpdatedEvent(UserMetricUpdatedEvent event) {
        kafkaTemplate.send(UPDATE_USER_METRIC_TOPIC, event);
    }
}
