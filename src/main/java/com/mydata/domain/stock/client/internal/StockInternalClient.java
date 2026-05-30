package com.mydata.domain.stock.client.internal;

import com.mydata.domain.stock.client.dto.response.*;
import com.mydata.global.config.FeignConfig;
import com.mydata.global.logging.TraceIdConstants;
import com.mydata.global.response.ApiResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "stockInternalClient",
    url = "${external.baas.url}",
    configuration = FeignConfig.class)
public interface StockInternalClient {

  @GetMapping("/baas/v1/stock/accounts")
  ApiResponse<StockAccountListResponse> getAccounts(
      @RequestHeader(TraceIdConstants.FIREBASE_UID_HEADER) String firebaseUid);

  @GetMapping("/baas/v1/stock/accounts/{accountId}/holdings")
  ApiResponse<List<StockHoldingResponse>> getHoldings(@PathVariable Long accountId);

  @GetMapping("/baas/v1/stock/accounts/{accountId}/portfolio")
  ApiResponse<StockPortfolioResponse> getPortfolio(@PathVariable Long accountId);

  @GetMapping("/baas/v1/stock/accounts/{accountId}/returns")
  ApiResponse<StockReturnResponse> getReturns(@PathVariable Long accountId);

  @GetMapping("/baas/v1/stock/assets/summary")
  ApiResponse<StockAssetSummaryResponse> getAssetSummary(
      @RequestHeader(TraceIdConstants.FIREBASE_UID_HEADER) String firebaseUid);
}
