package depth.finvibe.gamification.modules.study.application.port.out;

import java.util.List;

public interface KeywordGenerator {
    List<String> generateKeywords(List<String> interestStocks);
}
