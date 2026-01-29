package depth.finvibe.gamification.modules.gamification.domain;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import depth.finvibe.gamification.shared.domain.TimeStampedBaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Getter
public class UserXp extends TimeStampedBaseEntity {
    @Id
    private UUID userId;

    @Builder.Default
    private Long totalXp = 0L;

    @Builder.Default
    private Integer level = 1;

    public void addXp(Long amount) {
        this.totalXp += amount;
        updateLevel();
    }

    private void updateLevel() {
        // 간단한 레벨 계산 로직 (예: 1000 XP당 1레벨)
        this.level = (int) (this.totalXp / 1000) + 1;
    }
}
