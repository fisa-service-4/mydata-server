package com.mydata.domain.aggregation.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mydata.domain.aggregation.dto.response.AssetDistributionResponse;
import com.mydata.domain.aggregation.dto.response.DashboardResponse;
import com.mydata.domain.aggregation.dto.response.TotalAssetSummaryResponse;
import com.mydata.domain.aggregation.service.AggregationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AggregationController.class)
class AggregationControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AggregationService aggregationService;

  @Nested
  @DisplayName("통합 자산 요약 조회 GET /mydata/v1/assets/summary")
  class GetAssetSummary {

    @Test
    @DisplayName("성공 - 정상 응답 반환")
    void success() throws Exception {
      given(aggregationService.getAssetSummary(anyString()))
          .willReturn(
              TotalAssetSummaryResponse.builder()
                  .totalAssetAmount(18000000L)
                  .totalBankAssetAmount(3000000L)
                  .totalStockAssetAmount(15000000L)
                  .investmentRatio(83.33)
                  .build());

      mockMvc
          .perform(get("/mydata/v1/assets/summary").header("X-Firebase-Uid", "test-uid"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.totalAssetAmount").value(18000000))
          .andExpect(jsonPath("$.data.totalBankAssetAmount").value(3000000))
          .andExpect(jsonPath("$.data.totalStockAssetAmount").value(15000000))
          .andExpect(jsonPath("$.data.investmentRatio").value(83.33));
    }

    @Test
    @DisplayName("실패 - X-User-Id 헤더 누락 시 400")
    void missingHeader_returns400() throws Exception {
      mockMvc
          .perform(get("/mydata/v1/assets/summary"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.error.code").value("VALID_001"));
    }
  }

  @Nested
  @DisplayName("자산 분포 조회 GET /mydata/v1/assets/distribution")
  class GetAssetDistribution {

    @Test
    @DisplayName("성공 - 은행/증권 비율 정상 반환")
    void success() throws Exception {
      given(aggregationService.getAssetDistribution(anyString()))
          .willReturn(
              AssetDistributionResponse.builder()
                  .totalAssetAmount(18000000L)
                  .totalBankAssetAmount(3000000L)
                  .totalStockAssetAmount(15000000L)
                  .bankRatio(16.67)
                  .stockRatio(83.33)
                  .build());

      mockMvc
          .perform(get("/mydata/v1/assets/distribution").header("X-Firebase-Uid", "test-uid"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.bankRatio").value(16.67))
          .andExpect(jsonPath("$.data.stockRatio").value(83.33));
    }

    @Test
    @DisplayName("실패 - X-User-Id 헤더 누락 시 400")
    void missingHeader_returns400() throws Exception {
      mockMvc
          .perform(get("/mydata/v1/assets/distribution"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.error.code").value("VALID_001"));
    }
  }

  @Nested
  @DisplayName("통합 자산 대시보드 조회 GET /mydata/v1/assets/dashboard")
  class GetDashboard {

    @Test
    @DisplayName("성공 - 전체 대시보드 데이터 반환")
    void success() throws Exception {
      given(aggregationService.getDashboard(anyString()))
          .willReturn(
              DashboardResponse.builder()
                  .totalAssetAmount(18000000L)
                  .totalBankAssetAmount(3000000L)
                  .totalStockAssetAmount(15000000L)
                  .investmentRatio(83.33)
                  .bankAccountCount(2)
                  .holdingCount(5)
                  .totalProfitRate(13.64)
                  .build());

      mockMvc
          .perform(get("/mydata/v1/assets/dashboard").header("X-Firebase-Uid", "test-uid"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.totalAssetAmount").value(18000000))
          .andExpect(jsonPath("$.data.investmentRatio").value(83.33))
          .andExpect(jsonPath("$.data.bankAccountCount").value(2))
          .andExpect(jsonPath("$.data.holdingCount").value(5))
          .andExpect(jsonPath("$.data.totalProfitRate").value(13.64));
    }

    @Test
    @DisplayName("성공 - 증권 자산 없는 사용자 (investmentRatio = 0, holdingCount = 0)")
    void success_noStockAsset() throws Exception {
      given(aggregationService.getDashboard(anyString()))
          .willReturn(
              DashboardResponse.builder()
                  .totalAssetAmount(3000000L)
                  .totalBankAssetAmount(3000000L)
                  .totalStockAssetAmount(0L)
                  .investmentRatio(0.0)
                  .bankAccountCount(1)
                  .holdingCount(0)
                  .totalProfitRate(0.0)
                  .build());

      mockMvc
          .perform(get("/mydata/v1/assets/dashboard").header("X-Firebase-Uid", "test-uid"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.totalStockAssetAmount").value(0))
          .andExpect(jsonPath("$.data.investmentRatio").value(0.0))
          .andExpect(jsonPath("$.data.holdingCount").value(0));
    }

    @Test
    @DisplayName("실패 - X-User-Id 헤더 누락 시 400")
    void missingHeader_returns400() throws Exception {
      mockMvc
          .perform(get("/mydata/v1/assets/dashboard"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.error.code").value("VALID_001"));
    }
  }
}
