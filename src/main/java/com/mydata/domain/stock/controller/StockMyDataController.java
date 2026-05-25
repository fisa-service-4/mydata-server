package com.mydata.domain.stock.controller;

import com.mydata.domain.stock.dto.response.*;
import com.mydata.domain.stock.service.StockMyDataService;
import com.mydata.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Stock MyData API", description = "증권 마이데이터 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mydata/v1/stock")
public class StockMyDataController {

  private final StockMyDataService stockMyDataService;

  @Operation(summary = "주문 가능 계좌 조회")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "성공",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": true,
                              "data": [
                                {
                                  "accountId": 2001,
                                  "accountNumber": "300-123-456789",
                                  "accountName": "내 주식 계좌",
                                  "availableCash": 2800000
                                }
                              ],
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "증권 계좌 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "STOCK_ACCOUNT_001", "message": "증권 계좌를 찾을 수 없습니다." },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/accounts")
  public ApiResponse<List<StockAccountSummaryResponse>> getAccounts(
      @RequestHeader("X-User-Id") Long userId) {

    return ApiResponse.success(stockMyDataService.getAccounts(userId));
  }

  @Operation(summary = "보유 종목 조회")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "성공",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": true,
                              "data": [
                                {
                                  "stockCode": "005930",
                                  "stockName": "삼성전자",
                                  "quantity": 20,
                                  "averagePrice": 78000,
                                  "currentPrice": 82000,
                                  "evaluationAmount": 1640000,
                                  "profitRate": 5.12
                                }
                              ],
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "보유 종목 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "STOCK_HOLDING_001", "message": "보유 종목이 없습니다." },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/accounts/{accountId}/holdings")
  public ApiResponse<List<HoldingResponse>> getHoldings(
      @RequestHeader("X-User-Id") Long userId, @PathVariable @Positive Long accountId) {

    return ApiResponse.success(stockMyDataService.getHoldings(userId, accountId));
  }

  @Operation(summary = "포트폴리오 조회")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "성공",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": true,
                              "data": {
                                "totalEvaluationAmount": 15000000,
                                "totalPurchaseAmount": 13200000,
                                "totalProfitAmount": 1800000,
                                "totalProfitRate": 13.64
                              },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "포트폴리오 정보 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "STOCK_PORTFOLIO_001", "message": "포트폴리오 정보가 없습니다." },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/accounts/{accountId}/portfolio")
  public ApiResponse<PortfolioResponse> getPortfolio(
      @RequestHeader("X-User-Id") Long userId, @PathVariable @Positive Long accountId) {

    return ApiResponse.success(stockMyDataService.getPortfolio(userId, accountId));
  }

  @Operation(summary = "수익률 조회")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "성공",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": true,
                              "data": {
                                "totalPurchaseAmount": 13200000,
                                "totalEvaluationAmount": 15000000,
                                "totalProfitAmount": 1800000,
                                "totalProfitRate": 13.64
                              },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "수익률 정보 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "STOCK_RETURN_001", "message": "수익률 정보가 없습니다." },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/accounts/{accountId}/returns")
  public ApiResponse<ReturnResponse> getReturns(
      @RequestHeader("X-User-Id") Long userId, @PathVariable @Positive Long accountId) {

    return ApiResponse.success(stockMyDataService.getReturns(userId, accountId));
  }

  @Operation(summary = "전체 자산 요약 조회")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "성공",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": true,
                              "data": {
                                "totalAssetAmount": 18000000,
                                "totalPurchaseAmount": 13200000,
                                "totalEvaluationAmount": 15000000,
                                "totalProfitAmount": 1800000,
                                "totalProfitRate": 13.64
                              },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "자산 요약 정보 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "STOCK_ASSET_001", "message": "자산 요약 정보가 없습니다." },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/assets/summary")
  public ApiResponse<AssetSummaryResponse> getAssetSummary(
      @RequestHeader("X-User-Id") Long userId) {

    return ApiResponse.success(stockMyDataService.getAssetSummary(userId));
  }
}
