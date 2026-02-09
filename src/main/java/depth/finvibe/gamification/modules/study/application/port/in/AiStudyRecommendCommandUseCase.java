package depth.finvibe.gamification.modules.study.application.port.in;

import java.util.UUID;

public interface AiStudyRecommendCommandUseCase {
    void createOrGetTodayAiStudyRecommend(UUID userId);
}
