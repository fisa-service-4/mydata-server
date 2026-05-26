package com.mydata.domain.aggregation.controller;

import com.mydata.domain.aggregation.dto.response.AssetDistributionResponse;
import com.mydata.domain.aggregation.dto.response.DashboardResponse;
import com.mydata.domain.aggregation.dto.response.TotalAssetSummaryResponse;
import com.mydata.domain.aggregation.service.AggregationService;
import com.mydata.global.logging.TraceIdConstants;
import com.mydata.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Aggregation API", description = "통합 자산 Aggregation API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mydata/v1/assets")
public class AggregationController {

  private final AggregationService aggregationService;

  @Operation(summary = "통합 자산 요약 조회", description = "은행/증권 자산을 합산한 통합 자산 요약을 조회합니다.")
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
                                "totalBankAssetAmount": 3000000,
                                "totalStockAssetAmount": 15000000,
                                "investmentRatio": 83.33
                              },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (헤더 누락 또는 유효하지 않은 값)",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "VALID_001", "message": "X-User-Id 헤더가 필요합니다" },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "마이데이터 연동 실패",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "MYDATA_003", "message": "마이데이터 연동 실패" },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/summary")
  public ApiResponse<TotalAssetSummaryResponse> getAssetSummary(
      @RequestHeader(TraceIdConstants.USER_ID_HEADER) @Positive Long userId) {

    return ApiResponse.success(aggregationService.getAssetSummary(userId));
  }

  @Operation(summary = "자산 분포 조회", description = "은행/증권 자산 비율 분포를 조회합니다.")
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
                                "totalBankAssetAmount": 3000000,
                                "totalStockAssetAmount": 15000000,
                                "bankRatio": 16.67,
                                "stockRatio": 83.33
                              },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (헤더 누락 또는 유효하지 않은 값)",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "VALID_001", "message": "X-User-Id 헤더가 필요합니다" },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "마이데이터 연동 실패",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "MYDATA_003", "message": "마이데이터 연동 실패" },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/distribution")
  public ApiResponse<AssetDistributionResponse> getAssetDistribution(
      @RequestHeader(TraceIdConstants.USER_ID_HEADER) @Positive Long userId) {

    return ApiResponse.success(aggregationService.getAssetDistribution(userId));
  }

  @Operation(
      summary = "통합 자산 대시보드 조회",
      description = "총 자산, 계좌 수, 보유 종목 수, 수익률을 포함한 대시보드 데이터를 조회합니다.")
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
                                "totalBankAssetAmount": 3000000,
                                "totalStockAssetAmount": 15000000,
                                "investmentRatio": 83.33,
                                "bankAccountCount": 2,
                                "holdingCount": 5,
                                "totalProfitRate": 13.64
                              },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (헤더 누락 또는 유효하지 않은 값)",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "VALID_001", "message": "X-User-Id 헤더가 필요합니다" },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "마이데이터 연동 실패",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "MYDATA_003", "message": "마이데이터 연동 실패" },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/dashboard")
  public ApiResponse<DashboardResponse> getDashboard(
      @RequestHeader(TraceIdConstants.USER_ID_HEADER) @Positive Long userId) {

    return ApiResponse.success(aggregationService.getDashboard(userId));
  }
}
