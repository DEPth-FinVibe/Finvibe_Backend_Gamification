package depth.finvibe.gamification.modules.study.application.port.out;


import depth.finvibe.gamification.shared.dto.TradeDto;

import java.time.LocalDate;
import java.util.List;

public interface TradeServiceClient {
    List<TradeDto.TradeHistoryResponse> getUserTradeHistories(
            String userId,
            LocalDate fromDate,
            LocalDate toDate
    );
}
