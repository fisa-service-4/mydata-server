package com.mydata.domain.stock.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mydata.domain.stock.dto.response.AssetSummaryResponse;
import com.mydata.domain.stock.dto.response.HoldingResponse;
import com.mydata.domain.stock.dto.response.PortfolioResponse;
import com.mydata.domain.stock.dto.response.ReturnResponse;
import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
import com.mydata.domain.stock.service.StockMyDataService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StockMyDataController.class)
class StockMyDataControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private StockMyDataService stockMyDataService;

  @Nested
  @DisplayName("주문 가능 계좌 조회 GET /mydata/v1/stock/accounts")
  class GetAccounts {

    @Test
    @DisplayName("성공 - 증권 계좌 목록 반환")
    void success() throws Exception {
      given(stockMyDataService.getAccounts(anyString()))
          .willReturn(
              List.of(
                  StockAccountSummaryResponse.builder()
                      .accountId(2001L)
                      .accountNumber("300-123-456789")
                      .accountName("내 주식 계좌")
                      .cashBalance(BigDecimal.valueOf(2800000))
                      .build()));

      mockMvc
          .perform(get("/mydata/v1/stock/accounts").header("X-Firebase-Uid", "test-uid"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data[0].accountId").value(2001))
          .andExpect(jsonPath("$.data[0].accountName").value("내 주식 계좌"))
          .andExpect(jsonPath("$.data[0].cashBalance").value(2800000));
    }

    @Test
    @DisplayName("실패 - X-Firebase-Uid 헤더 누락 시 400")
    void missingHeader_returns400() throws Exception {
      mockMvc
          .perform(get("/mydata/v1/stock/accounts"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.error.code").value("VALID_001"));
    }
  }

  @Nested
  @DisplayName("보유 종목 조회 GET /mydata/v1/stock/accounts/{accountId}/holdings")
  class GetHoldings {

    @Test
    @DisplayName("성공 - 보유 종목 목록 반환")
    void success() throws Exception {
      given(stockMyDataService.getHoldings(anyLong()))
          .willReturn(
              List.of(
                  HoldingResponse.builder()
                      .stockCode("005930")
                      .stockName("삼성전자")
                      .quantity(20L)
                      .averagePrice(78000L)
                      .currentPrice(82000.0)
                      .evaluationAmount(1640000.0)
                      .profitRate(5.12)
                      .build()));

      mockMvc
          .perform(get("/mydata/v1/stock/accounts/2001/holdings"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data[0].stockCode").value("005930"))
          .andExpect(jsonPath("$.data[0].stockName").value("삼성전자"))
          .andExpect(jsonPath("$.data[0].evaluationAmount").value(1640000))
          .andExpect(jsonPath("$.data[0].profitRate").value(5.12));
    }
  }

  @Nested
  @DisplayName("포트폴리오 조회 GET /mydata/v1/stock/accounts/{accountId}/portfolio")
  class GetPortfolio {

    @Test
    @DisplayName("성공 - 포트폴리오 정보 반환")
    void success() throws Exception {
      given(stockMyDataService.getPortfolio(anyLong()))
          .willReturn(
              PortfolioResponse.builder()
                  .totalEvaluationAmount(15000000L)
                  .totalPurchaseAmount(13200000L)
                  .totalProfitAmount(1800000L)
                  .totalProfitRate(13.64)
                  .build());

      mockMvc
          .perform(get("/mydata/v1/stock/accounts/2001/portfolio"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.totalEvaluationAmount").value(15000000))
          .andExpect(jsonPath("$.data.totalPurchaseAmount").value(13200000))
          .andExpect(jsonPath("$.data.totalProfitAmount").value(1800000))
          .andExpect(jsonPath("$.data.totalProfitRate").value(13.64));
    }
  }

  @Nested
  @DisplayName("수익률 조회 GET /mydata/v1/stock/accounts/{accountId}/returns")
  class GetReturns {

    @Test
    @DisplayName("성공 - 수익률 정보 반환")
    void success() throws Exception {
      given(stockMyDataService.getReturns(anyLong()))
          .willReturn(
              ReturnResponse.builder()
                  .totalPurchaseAmount(13200000L)
                  .totalEvaluationAmount(15000000L)
                  .totalProfitAmount(1800000L)
                  .totalProfitRate(13.64)
                  .build());

      mockMvc
          .perform(get("/mydata/v1/stock/accounts/2001/returns"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.totalPurchaseAmount").value(13200000))
          .andExpect(jsonPath("$.data.totalEvaluationAmount").value(15000000))
          .andExpect(jsonPath("$.data.totalProfitRate").value(13.64));
    }
  }

  @Nested
  @DisplayName("전체 자산 요약 조회 GET /mydata/v1/stock/assets/summary")
  class GetAssetSummary {

    @Test
    @DisplayName("성공 - 전체 자산 요약 반환")
    void success() throws Exception {
      given(stockMyDataService.getAssetSummary(anyString()))
          .willReturn(
              AssetSummaryResponse.builder()
                  .totalAssetAmount(18000000L)
                  .totalPurchaseAmount(13200000L)
                  .totalEvaluationAmount(15000000L)
                  .totalProfitAmount(1800000L)
                  .totalProfitRate(13.64)
                  .build());

      mockMvc
          .perform(get("/mydata/v1/stock/assets/summary").header("X-Firebase-Uid", "test-uid"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.totalAssetAmount").value(18000000))
          .andExpect(jsonPath("$.data.totalEvaluationAmount").value(15000000))
          .andExpect(jsonPath("$.data.totalProfitRate").value(13.64));
    }

    @Test
    @DisplayName("실패 - X-Firebase-Uid 헤더 누락 시 400")
    void missingHeader_returns400() throws Exception {
      mockMvc
          .perform(get("/mydata/v1/stock/assets/summary"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.error.code").value("VALID_001"));
    }
  }
}
