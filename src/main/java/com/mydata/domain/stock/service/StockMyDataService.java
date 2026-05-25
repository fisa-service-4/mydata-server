package com.mydata.domain.stock.service;

import com.mydata.domain.stock.dto.response.*;
import java.util.List;

public interface StockMyDataService {

  List<StockAccountSummaryResponse> getAccounts(Long userId);

  List<HoldingResponse> getHoldings(Long userId, Long accountId);

  PortfolioResponse getPortfolio(Long userId, Long accountId);

  ReturnResponse getReturns(Long userId, Long accountId);

  AssetSummaryResponse getAssetSummary(Long userId);
}
