package com.mydata.domain.mydata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mydata.domain.aggregation.service.AggregationService;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.exception.BankMyDataException;
import com.mydata.domain.bank.service.BankMyDataService;
import com.mydata.domain.mydata.dto.request.ConnectRequest;
import com.mydata.domain.mydata.dto.response.ConnectResponse;
import com.mydata.domain.mydata.dto.response.ConnectionResponse;
import com.mydata.domain.mydata.dto.response.SyncResponse;
import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
import com.mydata.domain.stock.exception.StockMyDataException;
import com.mydata.domain.stock.service.StockMyDataService;
import com.mydata.global.exception.ErrorCode;
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
class MyDataServiceTest {

  @Mock private BankMyDataService bankMyDataService;

  @Mock private StockMyDataService stockMyDataService;

  @Mock private AggregationService aggregationService;

  @InjectMocks private MyDataServiceImpl myDataService;

  private static final String UID = "test-firebase-uid";

  private AccountSummaryResponse bankAccount() {
    return AccountSummaryResponse.builder()
        .accountId(1001L)
        .accountNumber("110-123-456789")
        .accountName("내 급여통장")
        .bankCode("088")
        .balance(3_500_000L)
        .build();
  }

  private StockAccountSummaryResponse stockAccount() {
    return StockAccountSummaryResponse.builder()
        .accountId(2001L)
        .accountNumber("300-123-456789")
        .accountName("내 주식 계좌")
        .bankCode("039")
        .cashBalance(BigDecimal.valueOf(3_000_000))
        .build();
  }

  @Nested
  @DisplayName("마이데이터 연동 - connect")
  class Connect {

    private ConnectRequest request = ConnectRequest.builder().provider("SHINHAN_BANK").build();

    @Test
    @DisplayName("성공 - 은행+증권 모두 연동")
    void success_bothLinked() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount()));
      given(stockMyDataService.getAccounts(UID)).willReturn(List.of(stockAccount()));

      ConnectResponse result = myDataService.connect(request, UID);

      assertThat(result.bankLinked()).isTrue();
      assertThat(result.stockLinked()).isTrue();
      assertThat(result.connected()).isTrue();
    }

    @Test
    @DisplayName("성공 - 은행만 연동 (증권 실패)")
    void success_bankOnlyLinked() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount()));
      given(stockMyDataService.getAccounts(UID))
          .willThrow(new StockMyDataException(ErrorCode.STOCK_ACCOUNT_NOT_FOUND));

      ConnectResponse result = myDataService.connect(request, UID);

      assertThat(result.bankLinked()).isTrue();
      assertThat(result.stockLinked()).isFalse();
      assertThat(result.connected()).isTrue();
    }

    @Test
    @DisplayName("성공 - 증권만 연동 (은행 실패)")
    void success_stockOnlyLinked() {
      given(bankMyDataService.getAccounts(UID))
          .willThrow(new BankMyDataException(ErrorCode.ACCOUNT_001));
      given(stockMyDataService.getAccounts(UID)).willReturn(List.of(stockAccount()));

      ConnectResponse result = myDataService.connect(request, UID);

      assertThat(result.bankLinked()).isFalse();
      assertThat(result.stockLinked()).isTrue();
      assertThat(result.connected()).isTrue();
    }

    @Test
    @DisplayName("성공 - 은행+증권 모두 실패 → connected=false")
    void success_noneLinked() {
      given(bankMyDataService.getAccounts(UID))
          .willThrow(new BankMyDataException(ErrorCode.ACCOUNT_001));
      given(stockMyDataService.getAccounts(UID))
          .willThrow(new StockMyDataException(ErrorCode.STOCK_ACCOUNT_NOT_FOUND));

      ConnectResponse result = myDataService.connect(request, UID);

      assertThat(result.bankLinked()).isFalse();
      assertThat(result.stockLinked()).isFalse();
      assertThat(result.connected()).isFalse();
    }
  }

  @Nested
  @DisplayName("연동 목록 조회 - getConnections")
  class GetConnections {

    @Test
    @DisplayName("성공 - 은행+증권 계좌 목록 반환")
    void success_both() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount()));
      given(stockMyDataService.getAccounts(UID)).willReturn(List.of(stockAccount()));

      ConnectionResponse result = myDataService.getConnections(UID);

      assertThat(result.bankAccounts()).hasSize(1);
      assertThat(result.stockAccounts()).hasSize(1);
    }

    @Test
    @DisplayName("성공 - 은행 조회 실패 시 bankAccounts 빈 리스트")
    void success_bankFailed() {
      given(bankMyDataService.getAccounts(UID))
          .willThrow(new BankMyDataException(ErrorCode.ACCOUNT_001));
      given(stockMyDataService.getAccounts(UID)).willReturn(List.of(stockAccount()));

      ConnectionResponse result = myDataService.getConnections(UID);

      assertThat(result.bankAccounts()).isEmpty();
      assertThat(result.stockAccounts()).hasSize(1);
    }

    @Test
    @DisplayName("성공 - 증권 조회 실패 시 stockAccounts 빈 리스트")
    void success_stockFailed() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount()));
      given(stockMyDataService.getAccounts(UID))
          .willThrow(new StockMyDataException(ErrorCode.STOCK_ACCOUNT_NOT_FOUND));

      ConnectionResponse result = myDataService.getConnections(UID);

      assertThat(result.bankAccounts()).hasSize(1);
      assertThat(result.stockAccounts()).isEmpty();
    }

    @Test
    @DisplayName("성공 - 모두 실패 시 두 목록 모두 빈 리스트")
    void success_allFailed() {
      given(bankMyDataService.getAccounts(UID))
          .willThrow(new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR));
      given(stockMyDataService.getAccounts(UID))
          .willThrow(new StockMyDataException(ErrorCode.MYDATA_003));

      ConnectionResponse result = myDataService.getConnections(UID);

      assertThat(result.bankAccounts()).isEmpty();
      assertThat(result.stockAccounts()).isEmpty();
    }
  }

  @Nested
  @DisplayName("동기화 요청 - sync")
  class Sync {

    @Test
    @DisplayName("성공 - 모든 서비스 정상 동작 시 synced=true 반환 및 하위 서비스 호출 검증")
    void success() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount()));
      given(stockMyDataService.getAccounts(UID)).willReturn(List.of(stockAccount()));

      SyncResponse result = myDataService.sync(UID);

      assertThat(result.synced()).isTrue();
      assertThat(result.syncedAt()).isNotNull();

      verify(bankMyDataService, times(1)).getAccounts(UID);
      verify(stockMyDataService, times(1)).getAccounts(UID);
      verify(aggregationService, times(1)).getAssetSummary(UID);
    }

    @Test
    @DisplayName("성공 - 은행 계좌 조회 실패해도 synced=true")
    void success_bankFailed() {
      given(bankMyDataService.getAccounts(UID))
          .willThrow(new BankMyDataException(ErrorCode.ACCOUNT_001));
      given(stockMyDataService.getAccounts(anyString())).willReturn(List.of(stockAccount()));

      SyncResponse result = myDataService.sync(UID);

      assertThat(result.synced()).isTrue();
    }

    @Test
    @DisplayName("성공 - 모든 서비스 실패해도 synced=true")
    void success_allServicesFailed() {
      given(bankMyDataService.getAccounts(UID))
          .willThrow(new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR));
      given(stockMyDataService.getAccounts(UID))
          .willThrow(new StockMyDataException(ErrorCode.MYDATA_003));

      SyncResponse result = myDataService.sync(UID);

      assertThat(result.synced()).isTrue();
      assertThat(result.syncedAt()).isNotNull();
    }

    @Test
    @DisplayName("성공 - syncedAt은 현재 시각으로 설정")
    void success_syncedAtIsPresent() {
      given(bankMyDataService.getAccounts(UID)).willReturn(List.of(bankAccount()));
      given(stockMyDataService.getAccounts(UID)).willReturn(List.of(stockAccount()));

      SyncResponse result = myDataService.sync(UID);

      assertThat(result.syncedAt()).isNotNull();
    }
  }
}
