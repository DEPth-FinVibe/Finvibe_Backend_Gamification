package depth.finvibe.gamification.shared.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import depth.finvibe.gamification.modules.gamification.domain.enums.MetricEventType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMetricUpdatedEvent {
    private String userId;
    private MetricEventType eventType;
    private Double delta;
    private Instant occurredAt;
}
