package depth.finvibe.gamification.modules.gamification.domain.enums;

public enum UserMetricType {
    SAVING_COUNT_PER_DAY, // 저축 횟수 (1일당 1회)
    CURRENT_RETURN_RATE, // 현재 수익률
    STOCK_BUY_COUNT, // 주식 구매 횟수
    STOCK_SELL_COUNT, // 주식 판매 횟수
    PORTFOLIO_COUNT_WITH_STOCKS, // 포트폴리오 개수 (주식이 있는 것만)
    HOLDING_STOCK_COUNT, // 보유 종목 개수
    NEWS_COMMENT_COUNT, // 뉴스에 달린 댓글 수
    NEWS_LIKE_COUNT, // 뉴스에 남긴 좋아요 수
    DISCUSSION_POST_COUNT, // 새 토론 게시글 횟수
    DISCUSSION_COMMENT_COUNT, // 토론에 달린 댓글 횟수
    DISCUSSION_LIKE_COUNT, // 토론에 좋아요를 누른 횟수
    AI_CONTENT_VIEW_COUNT, // AI 투자자 콘텐츠 조회 횟수
    CHALLENGE_COMPLETION_COUNT // 챌린지 달성 횟수
}
