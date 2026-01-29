package depth.finvibe.gamification.modules.gamification.application.port.in;

public interface ChallengeCommandUseCase {

    void generatePersonalChallenges();

    void rewardPersonalChallenges();

    void rewardWeeklyChallenges();
}
