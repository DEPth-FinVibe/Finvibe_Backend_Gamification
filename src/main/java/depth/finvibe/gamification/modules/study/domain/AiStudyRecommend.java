package depth.finvibe.gamification.modules.study.domain;

import depth.finvibe.gamification.shared.domain.TimeStampedBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class AiStudyRecommend extends TimeStampedBaseEntity {
    @Id
    private UUID userId;

    private String content;

    public void updateContent(String content) {
        this.content = content;
    }
}
