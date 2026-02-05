package depth.finvibe.gamification.modules.study.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import depth.finvibe.gamification.shared.domain.TimeStampedBaseEntity;

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

    @Enumerated(EnumType.STRING)
    private CourseDifficulty difficulty;

    private UUID owner;

    private Boolean isGlobal;

    public static Course of(String title, String description, CourseDifficulty difficulty, UUID owner) {
        return Course.builder()
                .title(title)
                .description(description)
                .difficulty(difficulty)
                .owner(owner)
                .isGlobal(false)
                .build();
    }
}
