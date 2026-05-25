package com.mydata.domain.stock.service;

import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
import java.util.List;

public interface StockMyDataService {

  List<StockAccountSummaryResponse> getAccounts(Long userId);
}
