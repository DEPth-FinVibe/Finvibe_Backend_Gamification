package depth.finvibe.gamification.modules.gamification.domain;

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
public class SquadXp extends TimeStampedBaseEntity {
    @Id
    private Long squadId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_id")
    private Squad squad;

    @Builder.Default
    private Long totalXp = 0L;

    @Builder.Default
    private Long weeklyXp = 0L;

    @Builder.Default
    private Double weeklyXpChangeRate = 0.0;

    public void addXp(Long amount) {
        this.totalXp += amount;
        this.weeklyXp += amount;
        updateChangeRate();
    }

    public void resetWeeklyXp() {
        this.weeklyXp = 0L;
        updateChangeRate();
    }

    private void updateChangeRate() {
        // 변동률 계산 로직 (필요 시 지난주 데이터와 비교)
        if (totalXp > 0) {
            this.weeklyXpChangeRate = (double) weeklyXp / (totalXp - weeklyXp) * 100;
        }
    }
}
