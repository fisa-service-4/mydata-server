package com.mydata.domain.stock.controller;

import com.mydata.domain.stock.dto.response.HoldingResponse;
import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
import com.mydata.domain.stock.service.StockMyDataService;
import com.mydata.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Stock MyData API", description = "증권 마이데이터 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/mydata/v1/stock")
public class StockMyDataController {

  private final StockMyDataService stockMyDataService;

  @Operation(summary = "주문 가능 계좌 조회")
  @GetMapping("/accounts")
  public ApiResponse<List<StockAccountSummaryResponse>> getAccounts(
      @RequestHeader("X-User-Id") Long userId) {

    return ApiResponse.success(stockMyDataService.getAccounts(userId));
  }

  @Operation(summary = "보유 종목 조회")
  @GetMapping("/accounts/{accountId}/holdings")
  public ApiResponse<List<HoldingResponse>> getHoldings(
      @RequestHeader("X-User-Id") Long userId, @PathVariable Long accountId) {

    return ApiResponse.success(stockMyDataService.getHoldings(userId, accountId));
  }
}
