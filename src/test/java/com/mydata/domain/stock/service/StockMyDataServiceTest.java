package com.mydata.domain.stock.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mydata.domain.stock.client.dto.response.StockAccountListResponse;
import com.mydata.domain.stock.client.dto.response.StockAccountResponse;
import com.mydata.domain.stock.client.dto.response.StockAssetSummaryResponse;
import com.mydata.domain.stock.client.dto.response.StockHoldingListResponse;
import com.mydata.domain.stock.client.dto.response.StockHoldingResponse;
import com.mydata.domain.stock.client.dto.response.StockPortfolioResponse;
import com.mydata.domain.stock.client.dto.response.StockReturnResponse;
import com.mydata.domain.stock.client.internal.StockInternalClient;
import com.mydata.domain.stock.dto.response.AssetSummaryResponse;
import com.mydata.domain.stock.dto.response.HoldingResponse;
import com.mydata.domain.stock.dto.response.PortfolioResponse;
import com.mydata.domain.stock.dto.response.ReturnResponse;
import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
import com.mydata.domain.stock.exception.StockMyDataException;
import com.mydata.domain.stock.mapper.StockMyDataMapper;
import com.mydata.global.exception.ErrorCode;
import com.mydata.global.response.ApiResponse;
import feign.FeignException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockMyDataServiceTest {

  @Mock private StockInternalClient stockInternalClient;

  @Mock private StockMyDataMapper stockMyDataMapper;

  @InjectMocks private StockMyDataServiceImpl stockMyDataService;

  private static final String UID = "test-firebase-uid";
  private static final Long ACCOUNT_ID = 2001L;

  @Nested
  @DisplayName("증권 계좌 목록 조회 - getAccounts")
  class GetAccounts {

    @Test
    @DisplayName("성공 - 증권 계좌 목록 정상 반환")
    void success() {
      StockAccountListResponse listResponse = mock(StockAccountListResponse.class);
      StockAccountResponse accountResponse = mock(StockAccountResponse.class);
      given(listResponse.getContent()).willReturn(List.of(accountResponse));
      given(stockInternalClient.getAccounts(UID)).willReturn(ApiResponse.success(listResponse));

      StockAccountSummaryResponse mapped =
          StockAccountSummaryResponse.builder()
              .accountId(ACCOUNT_ID)
              .accountNumber("300-123-456789")
              .accountName("내 주식 계좌")
              .bankCode("039")
              .cashBalance(BigDecimal.valueOf(3_000_000))
              .build();
      given(stockMyDataMapper.toStockAccountSummaryList(List.of(accountResponse)))
          .willReturn(List.of(mapped));

      List<StockAccountSummaryResponse> result = stockMyDataService.getAccounts(UID);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).accountId()).isEqualTo(ACCOUNT_ID);
    }

    @Test
    @DisplayName("실패 - 응답 null → STOCK_ACCOUNT_NOT_FOUND")
    void error_nullResponse() {
      given(stockInternalClient.getAccounts(UID)).willReturn(null);

      assertThatThrownBy(() -> stockMyDataService.getAccounts(UID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.STOCK_ACCOUNT_NOT_FOUND));
    }

    @Test
    @DisplayName("실패 - data.content null → STOCK_ACCOUNT_NOT_FOUND")
    void error_nullContent() {
      StockAccountListResponse listResponse = mock(StockAccountListResponse.class);
      given(listResponse.getContent()).willReturn(null);
      given(stockInternalClient.getAccounts(UID)).willReturn(ApiResponse.success(listResponse));

      assertThatThrownBy(() -> stockMyDataService.getAccounts(UID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.STOCK_ACCOUNT_NOT_FOUND));
    }

    @Test
    @DisplayName("실패 - FeignException.NotFound → STOCK_ACCOUNT_NOT_FOUND")
    void error_notFound() {
      given(stockInternalClient.getAccounts(UID)).willThrow(mock(FeignException.NotFound.class));

      assertThatThrownBy(() -> stockMyDataService.getAccounts(UID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.STOCK_ACCOUNT_NOT_FOUND));
    }

    @Test
    @DisplayName("실패 - FeignException → MYDATA_003")
    void error_feignException() {
      given(stockInternalClient.getAccounts(UID)).willThrow(mock(FeignException.class));

      assertThatThrownBy(() -> stockMyDataService.getAccounts(UID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.MYDATA_003));
    }
  }

  @Nested
  @DisplayName("보유 종목 조회 - getHoldings")
  class GetHoldings {

    @Test
    @DisplayName("성공 - 보유 종목 목록 정상 반환")
    void success() {
      StockHoldingListResponse listResponse = mock(StockHoldingListResponse.class);
      StockHoldingResponse holdingResponse = mock(StockHoldingResponse.class);
      given(listResponse.getContent()).willReturn(List.of(holdingResponse));
      given(stockInternalClient.getHoldings(ACCOUNT_ID))
          .willReturn(ApiResponse.success(listResponse));

      HoldingResponse mapped =
          HoldingResponse.builder()
              .stockCode("005930")
              .stockName("삼성전자")
              .quantity(20L)
              .averagePrice(78_000L)
              .evaluationAmount(1_640_000.0)
              .profitRate(5.13)
              .build();
      given(stockMyDataMapper.toHoldingResponseList(List.of(holdingResponse)))
          .willReturn(List.of(mapped));

      List<HoldingResponse> result = stockMyDataService.getHoldings(ACCOUNT_ID);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).stockCode()).isEqualTo("005930");
    }

    @Test
    @DisplayName("실패 - 응답 null → STOCK_HOLDING_NOT_FOUND")
    void error_nullResponse() {
      given(stockInternalClient.getHoldings(ACCOUNT_ID)).willReturn(null);

      assertThatThrownBy(() -> stockMyDataService.getHoldings(ACCOUNT_ID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.STOCK_HOLDING_NOT_FOUND));
    }

    @Test
    @DisplayName("실패 - FeignException.NotFound → STOCK_HOLDING_NOT_FOUND")
    void error_notFound() {
      given(stockInternalClient.getHoldings(ACCOUNT_ID))
          .willThrow(mock(FeignException.NotFound.class));

      assertThatThrownBy(() -> stockMyDataService.getHoldings(ACCOUNT_ID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.STOCK_HOLDING_NOT_FOUND));
    }

    @Test
    @DisplayName("실패 - FeignException → MYDATA_003")
    void error_feignException() {
      given(stockInternalClient.getHoldings(ACCOUNT_ID)).willThrow(mock(FeignException.class));

      assertThatThrownBy(() -> stockMyDataService.getHoldings(ACCOUNT_ID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.MYDATA_003));
    }
  }

  @Nested
  @DisplayName("포트폴리오 조회 - getPortfolio")
  class GetPortfolio {

    @Test
    @DisplayName("성공 - 포트폴리오 정상 반환")
    void success() {
      StockPortfolioResponse portfolioResponse = mock(StockPortfolioResponse.class);
      given(stockInternalClient.getPortfolio(ACCOUNT_ID))
          .willReturn(ApiResponse.success(portfolioResponse));
      PortfolioResponse expected = mock(PortfolioResponse.class);
      given(stockMyDataMapper.toPortfolioResponse(portfolioResponse)).willReturn(expected);

      PortfolioResponse result = stockMyDataService.getPortfolio(ACCOUNT_ID);

      assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("실패 - 응답 null → STOCK_PORTFOLIO_NOT_FOUND")
    void error_nullResponse() {
      given(stockInternalClient.getPortfolio(ACCOUNT_ID)).willReturn(null);

      assertThatThrownBy(() -> stockMyDataService.getPortfolio(ACCOUNT_ID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.STOCK_PORTFOLIO_NOT_FOUND));
    }

    @Test
    @DisplayName("실패 - FeignException.NotFound → STOCK_PORTFOLIO_NOT_FOUND")
    void error_notFound() {
      given(stockInternalClient.getPortfolio(ACCOUNT_ID))
          .willThrow(mock(FeignException.NotFound.class));

      assertThatThrownBy(() -> stockMyDataService.getPortfolio(ACCOUNT_ID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.STOCK_PORTFOLIO_NOT_FOUND));
    }

    @Test
    @DisplayName("실패 - FeignException → MYDATA_003")
    void error_feignException() {
      given(stockInternalClient.getPortfolio(ACCOUNT_ID)).willThrow(mock(FeignException.class));

      assertThatThrownBy(() -> stockMyDataService.getPortfolio(ACCOUNT_ID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.MYDATA_003));
    }
  }

  @Nested
  @DisplayName("수익률 조회 - getReturns")
  class GetReturns {

    @Test
    @DisplayName("성공 - 수익률 정상 반환")
    void success() {
      StockReturnResponse returnResponse = mock(StockReturnResponse.class);
      given(stockInternalClient.getReturns(ACCOUNT_ID))
          .willReturn(ApiResponse.success(returnResponse));
      ReturnResponse expected = mock(ReturnResponse.class);
      given(stockMyDataMapper.toReturnResponse(returnResponse)).willReturn(expected);

      ReturnResponse result = stockMyDataService.getReturns(ACCOUNT_ID);

      assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("실패 - 응답 null → STOCK_RETURN_NOT_FOUND")
    void error_nullResponse() {
      given(stockInternalClient.getReturns(ACCOUNT_ID)).willReturn(null);

      assertThatThrownBy(() -> stockMyDataService.getReturns(ACCOUNT_ID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.STOCK_RETURN_NOT_FOUND));
    }

    @Test
    @DisplayName("실패 - FeignException.NotFound → STOCK_RETURN_NOT_FOUND")
    void error_notFound() {
      given(stockInternalClient.getReturns(ACCOUNT_ID))
          .willThrow(mock(FeignException.NotFound.class));

      assertThatThrownBy(() -> stockMyDataService.getReturns(ACCOUNT_ID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.STOCK_RETURN_NOT_FOUND));
    }

    @Test
    @DisplayName("실패 - FeignException → MYDATA_003")
    void error_feignException() {
      given(stockInternalClient.getReturns(ACCOUNT_ID)).willThrow(mock(FeignException.class));

      assertThatThrownBy(() -> stockMyDataService.getReturns(ACCOUNT_ID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.MYDATA_003));
    }
  }

  @Nested
  @DisplayName("자산 요약 조회 - getAssetSummary")
  class GetAssetSummary {

    @Test
    @DisplayName("성공 - 자산 요약 정상 반환")
    void success() {
      StockAssetSummaryResponse assetResponse = mock(StockAssetSummaryResponse.class);
      given(stockInternalClient.getAssetSummary(UID))
          .willReturn(ApiResponse.success(assetResponse));
      AssetSummaryResponse expected = mock(AssetSummaryResponse.class);
      given(stockMyDataMapper.toAssetSummaryResponse(assetResponse)).willReturn(expected);

      AssetSummaryResponse result = stockMyDataService.getAssetSummary(UID);

      assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("실패 - 응답 null → STOCK_ASSET_SUMMARY_NOT_FOUND")
    void error_nullResponse() {
      given(stockInternalClient.getAssetSummary(UID)).willReturn(null);

      assertThatThrownBy(() -> stockMyDataService.getAssetSummary(UID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.STOCK_ASSET_SUMMARY_NOT_FOUND));
    }

    @Test
    @DisplayName("실패 - FeignException.NotFound → STOCK_ASSET_SUMMARY_NOT_FOUND")
    void error_notFound() {
      given(stockInternalClient.getAssetSummary(UID))
          .willThrow(mock(FeignException.NotFound.class));

      assertThatThrownBy(() -> stockMyDataService.getAssetSummary(UID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.STOCK_ASSET_SUMMARY_NOT_FOUND));
    }

    @Test
    @DisplayName("실패 - FeignException → MYDATA_003")
    void error_feignException() {
      given(stockInternalClient.getAssetSummary(UID)).willThrow(mock(FeignException.class));

      assertThatThrownBy(() -> stockMyDataService.getAssetSummary(UID))
          .isInstanceOf(StockMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((StockMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.MYDATA_003));
    }
  }
}
