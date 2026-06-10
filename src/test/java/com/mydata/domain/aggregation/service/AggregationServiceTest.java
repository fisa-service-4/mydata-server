package com.mydata.domain.aggregation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.mydata.domain.aggregation.dto.response.AssetDistributionResponse;
import com.mydata.domain.aggregation.dto.response.DashboardResponse;
import com.mydata.domain.aggregation.dto.response.TotalAssetSummaryResponse;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.exception.BankMyDataException;
import com.mydata.domain.bank.service.BankMyDataService;
import com.mydata.domain.stock.dto.response.AssetSummaryResponse;
import com.mydata.domain.stock.dto.response.HoldingResponse;
import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
import com.mydata.domain.stock.exception.StockMyDataException;
import com.mydata.domain.stock.service.StockMyDataService;
import com.mydata.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AggregationServiceTest {

  @Mock private BankMyDataService bankMyDataService;

  @Mock private StockMyDataService stockMyDataService;

  @InjectMocks private AggregationServiceImpl aggregationService;

  private static final String UID = "test-firebase-uid";

  private AccountSummaryResponse bankAccount(long balance) {
    return AccountSummaryResponse.builder()
        .accountId(1001L)
        .accountNumber("110-123-456789")
        .accountName("내 급여통장")
        .bankCode("088")
        .balance(balance)
        .build();
  }

  private AssetSummaryResponse stockSummary(
      long evaluation, long purchase, long profit, double rate) {
    return AssetSummaryResponse.builder()
        .totalAssetAmount(evaluation)
        .totalEvaluationAmount(evaluation)
        .totalPurchaseAmount(purchase)
        .totalProfitAmount(profit)
        .totalProfitRate(rate)
        .build();
  }

  private StockAccountSummaryResponse stockAccount(long accountId) {
    return StockAccountSummaryResponse.builder()
        .accountId(accountId)
        .accountNumber("300-123-456789")
        .accountName("내 주식 계좌")
        .bankCode("039")
        .cashBalance(BigDecimal.ZERO)
        .build();
  }

  @Nested
  @DisplayName("통합 자산 요약 조회 - getAssetSummary")
  class GetAssetSummary {

    @Test
    @DisplayName("성공 - 은행+증권 자산 합산 반환")
    void success_withBothAssets() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount(3_000_000L)));
      given(stockMyDataService.getAssetSummary(UID))
          .willReturn(stockSummary(15_000_000L, 13_200_000L, 1_800_000L, 13.64));

      TotalAssetSummaryResponse result = aggregationService.getAssetSummary(UID);

      assertThat(result.totalAssetAmount()).isEqualTo(18_000_000L);
      assertThat(result.totalBankAssetAmount()).isEqualTo(3_000_000L);
      assertThat(result.totalStockAssetAmount()).isEqualTo(15_000_000L);
      assertThat(result.investmentRatio()).isEqualTo(83.33);
    }

    @Test
    @DisplayName("성공 - 계좌 여러 개일 때 은행 잔액 합산")
    void success_multipleAccounts() {
      given(bankMyDataService.getAccounts(UID))
          .willReturn(List.of(bankAccount(1_000_000L), bankAccount(2_000_000L)));
      given(stockMyDataService.getAssetSummary(UID)).willReturn(stockSummary(0L, 0L, 0L, 0.0));

      TotalAssetSummaryResponse result = aggregationService.getAssetSummary(UID);

      assertThat(result.totalBankAssetAmount()).isEqualTo(3_000_000L);
    }

    @Test
    @DisplayName("성공 - 증권 자산 없음(STOCK_ASSET_SUMMARY_NOT_FOUND) → 증권 자산 0 처리")
    void success_stockNotFound() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount(3_000_000L)));
      given(stockMyDataService.getAssetSummary(UID))
          .willThrow(new StockMyDataException(ErrorCode.STOCK_ASSET_SUMMARY_NOT_FOUND));

      TotalAssetSummaryResponse result = aggregationService.getAssetSummary(UID);

      assertThat(result.totalAssetAmount()).isEqualTo(3_000_000L);
      assertThat(result.totalStockAssetAmount()).isEqualTo(0L);
      assertThat(result.investmentRatio()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("성공 - 은행 계좌 없음(ACCOUNT_001) → 은행 자산 0 처리")
    void success_bankNotFound() {
      given(bankMyDataService.getAccounts(UID))
          .willThrow(new BankMyDataException(ErrorCode.ACCOUNT_001));
      given(stockMyDataService.getAssetSummary(UID))
          .willReturn(stockSummary(15_000_000L, 13_200_000L, 1_800_000L, 13.64));

      TotalAssetSummaryResponse result = aggregationService.getAssetSummary(UID);

      assertThat(result.totalBankAssetAmount()).isEqualTo(0L);
      assertThat(result.totalStockAssetAmount()).isEqualTo(15_000_000L);
      assertThat(result.totalAssetAmount()).isEqualTo(15_000_000L);
    }

    @Test
    @DisplayName("성공 - 총 자산 0일 때 investmentRatio = 0.0")
    void success_allZero() {
      given(bankMyDataService.getAccounts(UID))
          .willThrow(new BankMyDataException(ErrorCode.ACCOUNT_001));
      given(stockMyDataService.getAssetSummary(UID))
          .willThrow(new StockMyDataException(ErrorCode.STOCK_ASSET_SUMMARY_NOT_FOUND));

      TotalAssetSummaryResponse result = aggregationService.getAssetSummary(UID);

      assertThat(result.totalAssetAmount()).isEqualTo(0L);
      assertThat(result.investmentRatio()).isEqualTo(0.0);
    }
  }

  @Nested
  @DisplayName("자산 분포 조회 - getAssetDistribution")
  class GetAssetDistribution {

    @Test
    @DisplayName("성공 - 은행/증권 비율 정상 계산")
    void success_withAssets() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount(3_000_000L)));
      given(stockMyDataService.getAssetSummary(UID))
          .willReturn(stockSummary(15_000_000L, 13_200_000L, 1_800_000L, 13.64));

      AssetDistributionResponse result = aggregationService.getAssetDistribution(UID);

      assertThat(result.totalAssetAmount()).isEqualTo(18_000_000L);
      assertThat(result.bankRatio()).isEqualTo(16.67);
      assertThat(result.stockRatio()).isEqualTo(83.33);
    }

    @Test
    @DisplayName("성공 - 총 자산 0일 때 bankRatio/stockRatio = 0.0")
    void success_allZero() {
      given(bankMyDataService.getAccounts(UID))
          .willThrow(new BankMyDataException(ErrorCode.ACCOUNT_001));
      given(stockMyDataService.getAssetSummary(UID))
          .willThrow(new StockMyDataException(ErrorCode.STOCK_ASSET_SUMMARY_NOT_FOUND));

      AssetDistributionResponse result = aggregationService.getAssetDistribution(UID);

      assertThat(result.totalAssetAmount()).isEqualTo(0L);
      assertThat(result.bankRatio()).isEqualTo(0.0);
      assertThat(result.stockRatio()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("성공 - 은행 자산만 있을 때 bankRatio = 100.0, stockRatio = 0.0")
    void success_bankOnly() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount(3_000_000L)));
      given(stockMyDataService.getAssetSummary(UID))
          .willThrow(new StockMyDataException(ErrorCode.STOCK_ASSET_SUMMARY_NOT_FOUND));

      AssetDistributionResponse result = aggregationService.getAssetDistribution(UID);

      assertThat(result.bankRatio()).isEqualTo(100.0);
      assertThat(result.stockRatio()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("성공 - 은행/증권 비율의 합은 100")
    void success_ratioSumEquals100() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount(5_000_000L)));
      given(stockMyDataService.getAssetSummary(UID))
          .willReturn(stockSummary(5_000_000L, 5_000_000L, 0L, 0.0));

      AssetDistributionResponse result = aggregationService.getAssetDistribution(UID);

      assertThat(result.bankRatio() + result.stockRatio()).isEqualTo(100.0);
    }
  }

  @Nested
  @DisplayName("통합 자산 대시보드 조회 - getDashboard")
  class GetDashboard {

    @Test
    @DisplayName("성공 - bankAccountCount는 조회된 계좌 수와 일치")
    void success_bankAccountCount() {
      given(bankMyDataService.getAccounts(UID))
          .willReturn(
              List.of(bankAccount(2_000_000L), bankAccount(1_000_000L), bankAccount(500_000L)));
      given(stockMyDataService.getAccounts(UID))
          .willThrow(new StockMyDataException(ErrorCode.STOCK_ACCOUNT_NOT_FOUND));

      DashboardResponse result = aggregationService.getDashboard(UID);

      assertThat(result.bankAccountCount()).isEqualTo(3);
      assertThat(result.totalBankAssetAmount()).isEqualTo(3_500_000L);
    }

    @Test
    @DisplayName("성공 - holdingCount는 보유 종목 수와 일치")
    void success_holdingCount() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount(1_000_000L)));
      given(stockMyDataService.getAccounts(UID)).willReturn(List.of(stockAccount(2001L)));
      given(stockMyDataService.getHoldings(2001L))
          .willReturn(
              List.of(
                  HoldingResponse.builder()
                      .stockCode("005930")
                      .stockName("삼성전자")
                      .quantity(null)
                      .averagePrice(null)
                      .evaluationAmount(null)
                      .build(),
                  HoldingResponse.builder()
                      .stockCode("000660")
                      .stockName("SK하이닉스")
                      .quantity(null)
                      .averagePrice(null)
                      .evaluationAmount(null)
                      .build()));

      DashboardResponse result = aggregationService.getDashboard(UID);

      assertThat(result.holdingCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("성공 - 증권 계좌 없음(STOCK_ACCOUNT_NOT_FOUND) → holdingCount=0, 증권 자산=0")
    void success_noStockAccounts() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount(3_000_000L)));
      given(stockMyDataService.getAccounts(UID))
          .willThrow(new StockMyDataException(ErrorCode.STOCK_ACCOUNT_NOT_FOUND));

      DashboardResponse result = aggregationService.getDashboard(UID);

      assertThat(result.totalStockAssetAmount()).isEqualTo(0L);
      assertThat(result.holdingCount()).isEqualTo(0);
      assertThat(result.investmentRatio()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("성공 - 은행/증권 모두 없을 때 totalAssetAmount = 0")
    void success_allEmpty() {
      given(bankMyDataService.getAccounts(UID))
          .willThrow(new BankMyDataException(ErrorCode.ACCOUNT_001));
      given(stockMyDataService.getAccounts(UID))
          .willThrow(new StockMyDataException(ErrorCode.STOCK_ACCOUNT_NOT_FOUND));

      DashboardResponse result = aggregationService.getDashboard(UID);

      assertThat(result.totalAssetAmount()).isEqualTo(0L);
      assertThat(result.bankAccountCount()).isEqualTo(0);
      assertThat(result.holdingCount()).isEqualTo(0);
      assertThat(result.totalProfitRate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("성공 - 보유 종목 없음(STOCK_HOLDING_NOT_FOUND) → holdingCount=0")
    void success_holdingNotFound() {
      given(bankMyDataService.getAccounts(UID)).willReturn(Collections.emptyList());
      given(stockMyDataService.getAccounts(UID)).willReturn(List.of(stockAccount(2001L)));
      given(stockMyDataService.getHoldings(anyLong()))
          .willThrow(new StockMyDataException(ErrorCode.STOCK_HOLDING_NOT_FOUND));

      DashboardResponse result = aggregationService.getDashboard(UID);

      assertThat(result.holdingCount()).isEqualTo(0);
      assertThat(result.totalStockAssetAmount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("성공 - 증권 자산 있을 때 금액 합산 및 수익률 계산 검증 (중복 합산 방지)")
    void success_withStockAssets() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount(1_000_000L)));
      given(stockMyDataService.getAccounts(UID)).willReturn(List.of(stockAccount(2001L)));
      given(stockMyDataService.getHoldings(2001L))
          .willReturn(
              List.of(
                  HoldingResponse.builder()
                      .stockCode("005930")
                      .stockName("삼성전자")
                      .quantity(10L)
                      .averagePrice(70_000L)
                      .evaluationAmount(800_000.0)
                      .build()));

      DashboardResponse result = aggregationService.getDashboard(UID);

      assertThat(result.totalBankAssetAmount()).isEqualTo(1_000_000L);
      assertThat(result.totalStockAssetAmount()).isEqualTo(800_000L);
      assertThat(result.totalAssetAmount()).isEqualTo(1_800_000L);
      assertThat(result.holdingCount()).isEqualTo(1);
      assertThat(result.totalProfitRate()).isEqualTo(14.29);
    }
  }
}
