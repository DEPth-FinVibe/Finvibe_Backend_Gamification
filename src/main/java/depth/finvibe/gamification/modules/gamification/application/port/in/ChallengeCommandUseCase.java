package depth.finvibe.gamification.modules.gamification.application.port.in;

public interface ChallengeCommandUseCase {

    /**
     * 개인별 챌린지를 생성합니다.
     */
    void generatePersonalChallenges();

    /**
     * 달성된 개인 챌린지에 대해 보상을 지급합니다.
     */
    void rewardPersonalChallenges();

    /**
     * 주간 챌린지 보상을 지급합니다.
     */
    void rewardWeeklyChallenges();
}
