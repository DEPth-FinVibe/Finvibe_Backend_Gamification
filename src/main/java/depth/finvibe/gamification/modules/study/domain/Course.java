package depth.finvibe.gamification.modules.study.domain;

import depth.finvibe.gamification.shared.domain.TimeStampedBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class Course extends TimeStampedBaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private UUID owner;

    private Boolean isGlobal;

    public static Course of(String title, String description, UUID owner) {
        return Course.builder()
                .title(title)
                .description(description)
                .owner(owner)
                .isGlobal(false)
                .build();
    }
}
