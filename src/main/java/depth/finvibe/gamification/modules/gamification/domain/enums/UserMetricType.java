package depth.finvibe.gamification.modules.gamification.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserMetricType {
    LOGIN_COUNT_PER_DAY("접속 횟수 (1일당 1회)", true),
    CURRENT_RETURN_RATE("현재 수익률", false),
    STOCK_BUY_COUNT("주식 구매 횟수", true),
    STOCK_SELL_COUNT("주식 판매 횟수", true),
    PORTFOLIO_COUNT_WITH_STOCKS("포트폴리오 개수 (주식이 있는 것만)", false),
    HOLDING_STOCK_COUNT("보유 종목 개수", false),
    NEWS_COMMENT_COUNT("뉴스에 달린 댓글 수", true),
    NEWS_LIKE_COUNT("뉴스에 남긴 좋아요 수", true),
    DISCUSSION_POST_COUNT("새 토론 게시글 횟수", true),
    DISCUSSION_COMMENT_COUNT("토론에 달린 댓글 횟수", true),
    DISCUSSION_LIKE_COUNT("토론에 좋아요를 누른 횟수", true),
    AI_CONTENT_COMPLETE_COUNT("AI 투자자 콘텐츠 완료 횟수", true),
    CHALLENGE_COMPLETION_COUNT("챌린지 달성 횟수", true),
    LOGIN_STREAK_DAYS("연속 접속 일수", false),
    LAST_LOGIN_DATETIME("최근 접속 일시", false);

    private final String description;
    private final boolean weeklyCollect;
}
