package com.mydata.domain.stock.service;

import com.mydata.domain.stock.dto.response.*;
import java.util.List;

public interface StockMyDataService {

  List<StockAccountSummaryResponse> getAccounts(String firebaseUid);

  List<HoldingResponse> getHoldings(Long accountId);

  PortfolioResponse getPortfolio(Long accountId);

  ReturnResponse getReturns(Long accountId);

  AssetSummaryResponse getAssetSummary(String firebaseUid);
}
