package depth.finvibe.gamification.modules.gamification.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MetricEventType {
    LOGIN("로그인"),
    AI_CONTENT_COMPLETED("AI 투자자 콘텐츠 수료"),
    HOLDING_STOCK_COUNT_CHANGED("보유 종목 개수 변경"),
    CHALLENGE_COMPLETED("챌린지 완료");

    private final String description;
}
