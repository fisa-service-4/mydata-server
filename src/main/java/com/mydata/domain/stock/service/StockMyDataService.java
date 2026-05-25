package com.mydata.domain.stock.service;

import com.mydata.domain.stock.dto.response.HoldingResponse;
import com.mydata.domain.stock.dto.response.PortfolioResponse;
import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
import java.util.List;

public interface StockMyDataService {

  List<StockAccountSummaryResponse> getAccounts(Long userId);

  List<HoldingResponse> getHoldings(Long userId, Long accountId);

  PortfolioResponse getPortfolio(Long userId, Long accountId);
}
