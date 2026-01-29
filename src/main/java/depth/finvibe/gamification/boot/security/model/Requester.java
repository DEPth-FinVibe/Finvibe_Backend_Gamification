package depth.finvibe.gamification.boot.security.model;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Requester {
  private UUID uuid;
  private UserRole role;
}
