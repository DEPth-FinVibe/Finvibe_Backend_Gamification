package depth.finvibe.gamification.modules.gamification.domain.vo;

import java.time.LocalDate;

import depth.finvibe.gamification.modules.gamification.domain.error.GamificationErrorCode;
import depth.finvibe.gamification.shared.error.DomainException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Embeddable
public class Period {

  private LocalDate startDate;
  private LocalDate endDate;

  public static Period of(LocalDate startDate, LocalDate endDate) {
        if(startDate == null || endDate == null) {
            throw new DomainException(GamificationErrorCode.INVALID_PERIOD_START_DATE_OR_END_DATE);
        }

        if(startDate.isAfter(endDate)) {
            throw new DomainException(GamificationErrorCode.INVALID_PERIOD_START_DATE_IS_GREATER_THAN_END_DATE);
        }

        return Period.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
