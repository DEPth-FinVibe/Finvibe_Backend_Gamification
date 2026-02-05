package depth.finvibe.gamification.modules.study.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class StudyMetric {
    @Id
    private UUID userId;

    private Long xpEarned;

    private Long timeSpentMinutes;
}
