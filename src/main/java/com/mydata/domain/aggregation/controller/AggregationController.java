package com.mydata.domain.aggregation.controller;

import com.mydata.domain.aggregation.dto.response.AssetDistributionResponse;
import com.mydata.domain.aggregation.dto.response.AssetSummaryResponse;
import com.mydata.domain.aggregation.dto.response.DashboardResponse;
import com.mydata.domain.aggregation.service.AggregationService;
import com.mydata.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Aggregation API", description = "통합 자산 Aggregation API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/mydata/v1/assets")
public class AggregationController {

  private final AggregationService aggregationService;

  @Operation(summary = "통합 자산 요약 조회")
  @GetMapping("/summary")
  public ApiResponse<AssetSummaryResponse> getAssetSummary(
      @RequestHeader("X-User-Id") Long userId) {

    return ApiResponse.success(aggregationService.getAssetSummary(userId));
  }

  @Operation(summary = "자산 분포 조회")
  @GetMapping("/distribution")
  public ApiResponse<AssetDistributionResponse> getAssetDistribution(
      @RequestHeader("X-User-Id") Long userId) {

    return ApiResponse.success(aggregationService.getAssetDistribution(userId));
  }

  @Operation(summary = "통합 자산 대시보드 조회")
  @GetMapping("/dashboard")
  public ApiResponse<DashboardResponse> getDashboard(@RequestHeader("X-User-Id") Long userId) {

    return ApiResponse.success(aggregationService.getDashboard(userId));
  }
}
