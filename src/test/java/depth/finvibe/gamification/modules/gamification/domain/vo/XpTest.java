package depth.finvibe.gamification.modules.gamification.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import depth.finvibe.gamification.modules.gamification.domain.error.GamificationErrorCode;
import depth.finvibe.gamification.shared.error.DomainException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Xp 도메인 규칙")
class XpTest {

  @Test
  @DisplayName("정상 값이면 Xp가 생성된다")
  void create_success() {
    Xp xp = Xp.of(10L, "챌린지 보상");

    assertThat(xp.getValue()).isEqualTo(10L);
    assertThat(xp.getReason()).isEqualTo("챌린지 보상");
  }

  @Test
  @DisplayName("XP 값이 null이거나 0 이하이면 예외가 발생한다")
  void create_fail_when_value_is_invalid() {
    assertThatThrownBy(() -> Xp.of(null, "사유"))
        .isInstanceOf(DomainException.class)
        .extracting("errorCode")
        .isEqualTo(GamificationErrorCode.INVALID_XP_VALUE);

    assertThatThrownBy(() -> Xp.of(0L, "사유"))
        .isInstanceOf(DomainException.class)
        .extracting("errorCode")
        .isEqualTo(GamificationErrorCode.INVALID_XP_VALUE);

    assertThatThrownBy(() -> Xp.of(-1L, "사유"))
        .isInstanceOf(DomainException.class)
        .extracting("errorCode")
        .isEqualTo(GamificationErrorCode.INVALID_XP_VALUE);
  }

  @Test
  @DisplayName("XP 사유가 비어있으면 예외가 발생한다")
  void create_fail_when_reason_is_blank() {
    assertThatThrownBy(() -> Xp.of(10L, null))
        .isInstanceOf(DomainException.class)
        .extracting("errorCode")
        .isEqualTo(GamificationErrorCode.INVALID_XP_REASON);

    assertThatThrownBy(() -> Xp.of(10L, ""))
        .isInstanceOf(DomainException.class)
        .extracting("errorCode")
        .isEqualTo(GamificationErrorCode.INVALID_XP_REASON);

    assertThatThrownBy(() -> Xp.of(10L, "   "))
        .isInstanceOf(DomainException.class)
        .extracting("errorCode")
        .isEqualTo(GamificationErrorCode.INVALID_XP_REASON);
  }
}
