package depth.finvibe.gamification.modules.gamification.domain.idclass;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class UserMetricId implements Serializable {
  private UserMetricType type;
  private UUID userId;
}
