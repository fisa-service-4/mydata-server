package com.mydata.domain.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mydata.domain.bank.client.dto.response.BankAccountDetailResponse;
import com.mydata.domain.bank.client.dto.response.BankAccountListResponse;
import com.mydata.domain.bank.client.dto.response.BankAccountResponse;
import com.mydata.domain.bank.client.dto.response.BankBalanceResponse;
import com.mydata.domain.bank.client.dto.response.BankTransactionListResponse;
import com.mydata.domain.bank.client.dto.response.BankTransactionResponse;
import com.mydata.domain.bank.client.internal.BankInternalClient;
import com.mydata.domain.bank.dto.request.TransactionSearchRequest;
import com.mydata.domain.bank.dto.response.AccountDetailResponse;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.dto.response.BalanceResponse;
import com.mydata.domain.bank.dto.response.CategoryResponse;
import com.mydata.domain.bank.dto.response.TransactionResponse;
import com.mydata.domain.bank.exception.BankMyDataException;
import com.mydata.domain.bank.mapper.BankMyDataMapper;
import com.mydata.domain.card.client.dto.response.CardApprovalItemResponse;
import com.mydata.domain.card.client.dto.response.CardApprovalListResponse;
import com.mydata.domain.card.client.dto.response.CardItemResponse;
import com.mydata.domain.card.client.dto.response.CardListResponse;
import com.mydata.domain.card.client.internal.CardInternalClient;
import com.mydata.global.exception.ErrorCode;
import com.mydata.global.response.ApiResponse;
import feign.FeignException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BankMyDataServiceTest {

  @Mock private BankInternalClient bankInternalClient;

  @Mock private CardInternalClient cardInternalClient;

  @Mock private BankMyDataMapper bankMyDataMapper;

  private BankMyDataServiceImpl bankMyDataService;

  private static final String UID = "test-firebase-uid";
  private static final Long ACCOUNT_ID = 1001L;

  @BeforeEach
  void setUp() {
    bankMyDataService =
        new BankMyDataServiceImpl(
            bankInternalClient, cardInternalClient, bankMyDataMapper, Runnable::run);
  }

  @Nested
  @DisplayName("계좌 목록 조회 - getAccounts")
  class GetAccounts {

    @Test
    @DisplayName("성공 - 계좌 목록 정상 반환")
    void success() {
      BankAccountListResponse listResponse = mock(BankAccountListResponse.class);
      BankAccountResponse accountResponse = mock(BankAccountResponse.class);
      given(listResponse.getContent()).willReturn(List.of(accountResponse));
      given(bankInternalClient.getAccounts(UID)).willReturn(ApiResponse.success(listResponse));

      AccountSummaryResponse mapped =
          AccountSummaryResponse.builder()
              .accountId(ACCOUNT_ID)
              .accountNumber("110-123-456789")
              .accountName("내 급여통장")
              .bankCode("088")
              .balance(3_500_000L)
              .build();
      given(bankMyDataMapper.toAccountSummaryList(List.of(accountResponse)))
          .willReturn(List.of(mapped));

      List<AccountSummaryResponse> result = bankMyDataService.getAccounts(UID);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).accountId()).isEqualTo(ACCOUNT_ID);
    }

    @Test
    @DisplayName("성공 - content가 null이면 빈 리스트 반환")
    void success_nullContent() {
      BankAccountListResponse listResponse = mock(BankAccountListResponse.class);
      given(listResponse.getContent()).willReturn(null);
      given(bankInternalClient.getAccounts(UID)).willReturn(ApiResponse.success(listResponse));

      List<AccountSummaryResponse> result = bankMyDataService.getAccounts(UID);

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("실패 - FeignException.NotFound → ACCOUNT_001")
    void error_notFound() {
      given(bankInternalClient.getAccounts(UID)).willThrow(mock(FeignException.NotFound.class));

      assertThatThrownBy(() -> bankMyDataService.getAccounts(UID))
          .isInstanceOf(BankMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((BankMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.ACCOUNT_001));
    }

    @Test
    @DisplayName("실패 - FeignException → BANK_INTERNAL_API_ERROR")
    void error_feignException() {
      given(bankInternalClient.getAccounts(UID)).willThrow(mock(FeignException.class));

      assertThatThrownBy(() -> bankMyDataService.getAccounts(UID))
          .isInstanceOf(BankMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((BankMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.BANK_INTERNAL_API_ERROR));
    }

    @Test
    @DisplayName("실패 - 응답 null → BANK_INTERNAL_API_ERROR")
    void error_nullResponse() {
      given(bankInternalClient.getAccounts(UID)).willReturn(null);

      assertThatThrownBy(() -> bankMyDataService.getAccounts(UID))
          .isInstanceOf(BankMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((BankMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.BANK_INTERNAL_API_ERROR));
    }
  }

  @Nested
  @DisplayName("계좌 상세 조회 - getAccountDetail")
  class GetAccountDetail {

    @Test
    @DisplayName("성공 - 계좌 상세 정상 반환")
    void success() {
      BankAccountDetailResponse detail = mock(BankAccountDetailResponse.class);
      given(bankInternalClient.getAccountDetail(ACCOUNT_ID))
          .willReturn(ApiResponse.success(detail));
      AccountDetailResponse expected = mock(AccountDetailResponse.class);
      given(bankMyDataMapper.toAccountDetail(detail)).willReturn(expected);

      AccountDetailResponse result = bankMyDataService.getAccountDetail(ACCOUNT_ID);

      assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("실패 - 응답 null → ACCOUNT_001")
    void error_nullResponse() {
      given(bankInternalClient.getAccountDetail(ACCOUNT_ID)).willReturn(null);

      assertThatThrownBy(() -> bankMyDataService.getAccountDetail(ACCOUNT_ID))
          .isInstanceOf(BankMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((BankMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.ACCOUNT_001));
    }

    @Test
    @DisplayName("실패 - FeignException.NotFound → ACCOUNT_001")
    void error_notFound() {
      given(bankInternalClient.getAccountDetail(ACCOUNT_ID))
          .willThrow(mock(FeignException.NotFound.class));

      assertThatThrownBy(() -> bankMyDataService.getAccountDetail(ACCOUNT_ID))
          .isInstanceOf(BankMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((BankMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.ACCOUNT_001));
    }

    @Test
    @DisplayName("실패 - FeignException → BANK_INTERNAL_API_ERROR")
    void error_feignException() {
      given(bankInternalClient.getAccountDetail(ACCOUNT_ID)).willThrow(mock(FeignException.class));

      assertThatThrownBy(() -> bankMyDataService.getAccountDetail(ACCOUNT_ID))
          .isInstanceOf(BankMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((BankMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.BANK_INTERNAL_API_ERROR));
    }
  }

  @Nested
  @DisplayName("잔액 조회 - getBalance")
  class GetBalance {

    @Test
    @DisplayName("성공 - 잔액 정상 반환")
    void success() {
      BankBalanceResponse balanceResponse = mock(BankBalanceResponse.class);
      given(bankInternalClient.getBalance(ACCOUNT_ID))
          .willReturn(ApiResponse.success(balanceResponse));
      BalanceResponse expected = mock(BalanceResponse.class);
      given(bankMyDataMapper.toBalanceResponse(balanceResponse)).willReturn(expected);

      BalanceResponse result = bankMyDataService.getBalance(ACCOUNT_ID);

      assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("실패 - 응답 null → ACCOUNT_001")
    void error_nullResponse() {
      given(bankInternalClient.getBalance(ACCOUNT_ID)).willReturn(null);

      assertThatThrownBy(() -> bankMyDataService.getBalance(ACCOUNT_ID))
          .isInstanceOf(BankMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((BankMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.ACCOUNT_001));
    }

    @Test
    @DisplayName("실패 - FeignException.NotFound → ACCOUNT_001")
    void error_notFound() {
      given(bankInternalClient.getBalance(ACCOUNT_ID))
          .willThrow(mock(FeignException.NotFound.class));

      assertThatThrownBy(() -> bankMyDataService.getBalance(ACCOUNT_ID))
          .isInstanceOf(BankMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((BankMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.ACCOUNT_001));
    }

    @Test
    @DisplayName("실패 - FeignException → BANK_INTERNAL_API_ERROR")
    void error_feignException() {
      given(bankInternalClient.getBalance(ACCOUNT_ID)).willThrow(mock(FeignException.class));

      assertThatThrownBy(() -> bankMyDataService.getBalance(ACCOUNT_ID))
          .isInstanceOf(BankMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((BankMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.BANK_INTERNAL_API_ERROR));
    }
  }

  @Nested
  @DisplayName("거래내역 조회 - getTransactions")
  class GetTransactions {

    private TransactionSearchRequest request =
        TransactionSearchRequest.builder()
            .fromDate("2026-05-01")
            .toDate("2026-05-31")
            .page(0)
            .size(20)
            .build();

    @Test
    @DisplayName("성공 - 카드 없을 때 merchantName/merchantCategory null로 반환")
    void success_noCards() {
      BankTransactionResponse tx = mock(BankTransactionResponse.class);
      given(tx.getTransactionId()).willReturn(9001L);
      given(tx.getTransactionAt()).willReturn("2026-05-01T09:00:00");
      given(tx.getTransactionType()).willReturn("DEPOSIT");
      given(tx.getAmount()).willReturn(3_000_000L);
      given(tx.getBalanceAfter()).willReturn(3_500_000L);
      given(tx.getDescription()).willReturn("급여");

      BankTransactionListResponse txListResponse = mock(BankTransactionListResponse.class);
      given(txListResponse.getContent()).willReturn(List.of(tx));
      given(bankInternalClient.getTransactions(ACCOUNT_ID, "2026-05-01", "2026-05-31", 0, 20))
          .willReturn(ApiResponse.success(txListResponse));

      CardListResponse cardListResponse = mock(CardListResponse.class);
      given(cardListResponse.getContent()).willReturn(Collections.emptyList());
      given(cardInternalClient.getCardsByAccountId(ACCOUNT_ID))
          .willReturn(ApiResponse.success(cardListResponse));

      List<TransactionResponse> result = bankMyDataService.getTransactions(ACCOUNT_ID, request);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).transactionId()).isEqualTo(9001L);
      assertThat(result.get(0).merchantName()).isNull();
      assertThat(result.get(0).merchantCategory()).isNull();
      assertThat(result.get(0).maskedCardNumber()).isNull();
    }

    @Test
    @DisplayName("성공 - APPROVED 카드 승인내역이 거래에 매핑됨")
    void success_withCardApproval() {
      BankTransactionResponse tx = mock(BankTransactionResponse.class);
      given(tx.getTransactionId()).willReturn(9002L);
      given(tx.getTransactionAt()).willReturn("2026-05-02T12:30:00");
      given(tx.getTransactionType()).willReturn("WITHDRAW");
      given(tx.getAmount()).willReturn(5_500L);
      given(tx.getBalanceAfter()).willReturn(3_494_500L);
      given(tx.getDescription()).willReturn("카드결제");

      BankTransactionListResponse txListResponse = mock(BankTransactionListResponse.class);
      given(txListResponse.getContent()).willReturn(List.of(tx));
      given(bankInternalClient.getTransactions(ACCOUNT_ID, "2026-05-01", "2026-05-31", 0, 20))
          .willReturn(ApiResponse.success(txListResponse));

      CardItemResponse card = mock(CardItemResponse.class);
      given(card.getCardId()).willReturn(1L);
      given(card.getCardNumber()).willReturn("1234-****-****-5678");
      CardListResponse cardListResponse = mock(CardListResponse.class);
      given(cardListResponse.getContent()).willReturn(List.of(card));
      given(cardInternalClient.getCardsByAccountId(ACCOUNT_ID))
          .willReturn(ApiResponse.success(cardListResponse));

      CardApprovalItemResponse approval = mock(CardApprovalItemResponse.class);
      given(approval.getAccountTransactionId()).willReturn(9002L);
      given(approval.getApprovalStatus()).willReturn("APPROVED");
      given(approval.getMerchantName()).willReturn("스타벅스 강남점");
      given(approval.getMerchantCategory()).willReturn("CAFE");
      CardApprovalListResponse approvalListResponse = mock(CardApprovalListResponse.class);
      given(approvalListResponse.getContent()).willReturn(List.of(approval));
      given(
              cardInternalClient.getApprovals(
                  anyLong(), anyString(), anyString(), anyInt(), anyInt()))
          .willReturn(ApiResponse.success(approvalListResponse));

      List<TransactionResponse> result = bankMyDataService.getTransactions(ACCOUNT_ID, request);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).merchantName()).isEqualTo("스타벅스 강남점");
      assertThat(result.get(0).merchantCategory()).isEqualTo("CAFE");
      assertThat(result.get(0).maskedCardNumber()).isEqualTo("1234-****-****-5678");
    }

    @Test
    @DisplayName("성공 - APPROVED가 아닌 승인내역은 매핑 제외")
    void success_nonApprovedSkipped() {
      BankTransactionResponse tx = mock(BankTransactionResponse.class);
      given(tx.getTransactionId()).willReturn(9003L);
      given(tx.getTransactionType()).willReturn("WITHDRAW");
      given(tx.getAmount()).willReturn(10_000L);
      given(tx.getBalanceAfter()).willReturn(990_000L);

      BankTransactionListResponse txListResponse = mock(BankTransactionListResponse.class);
      given(txListResponse.getContent()).willReturn(List.of(tx));
      given(bankInternalClient.getTransactions(ACCOUNT_ID, "2026-05-01", "2026-05-31", 0, 20))
          .willReturn(ApiResponse.success(txListResponse));

      CardItemResponse card = mock(CardItemResponse.class);
      given(card.getCardId()).willReturn(1L);
      CardListResponse cardListResponse = mock(CardListResponse.class);
      given(cardListResponse.getContent()).willReturn(List.of(card));
      given(cardInternalClient.getCardsByAccountId(ACCOUNT_ID))
          .willReturn(ApiResponse.success(cardListResponse));

      CardApprovalItemResponse approval = mock(CardApprovalItemResponse.class);
      given(approval.getAccountTransactionId()).willReturn(9003L);
      given(approval.getApprovalStatus()).willReturn("DECLINED");
      CardApprovalListResponse approvalListResponse = mock(CardApprovalListResponse.class);
      given(approvalListResponse.getContent()).willReturn(List.of(approval));
      given(
              cardInternalClient.getApprovals(
                  anyLong(), anyString(), anyString(), anyInt(), anyInt()))
          .willReturn(ApiResponse.success(approvalListResponse));

      List<TransactionResponse> result = bankMyDataService.getTransactions(ACCOUNT_ID, request);

      assertThat(result.get(0).merchantName()).isNull();
    }

    @Test
    @DisplayName("실패 - 거래내역 FeignException.NotFound → ACCOUNT_001 재포장")
    void error_bankTransactionNotFound() {
      given(bankInternalClient.getTransactions(ACCOUNT_ID, "2026-05-01", "2026-05-31", 0, 20))
          .willThrow(mock(FeignException.NotFound.class));

      CardListResponse cardListResponse = mock(CardListResponse.class);
      given(cardListResponse.getContent()).willReturn(Collections.emptyList());
      given(cardInternalClient.getCardsByAccountId(ACCOUNT_ID))
          .willReturn(ApiResponse.success(cardListResponse));

      assertThatThrownBy(() -> bankMyDataService.getTransactions(ACCOUNT_ID, request))
          .isInstanceOf(BankMyDataException.class)
          .satisfies(
              ex ->
                  assertThat(((BankMyDataException) ex).getErrorCode())
                      .isEqualTo(ErrorCode.ACCOUNT_001));
    }
  }

  @Nested
  @DisplayName("거래 카테고리 합계 조회 - getTransactionCategories")
  class GetTransactionCategories {

    @Test
    @DisplayName("성공 - 카테고리별 금액 합산")
    void success_withApprovals() {
      CardItemResponse card = mock(CardItemResponse.class);
      given(card.getCardId()).willReturn(1L);
      given(card.getCardNumber()).willReturn("1234-****-****-5678");
      CardListResponse cardListResponse = mock(CardListResponse.class);
      given(cardListResponse.getContent()).willReturn(List.of(card));
      given(cardInternalClient.getCardsByAccountId(ACCOUNT_ID))
          .willReturn(ApiResponse.success(cardListResponse));

      CardApprovalItemResponse approval1 = mock(CardApprovalItemResponse.class);
      given(approval1.getAccountTransactionId()).willReturn(9001L);
      given(approval1.getApprovalStatus()).willReturn("APPROVED");
      given(approval1.getMerchantCategory()).willReturn("CAFE");
      given(approval1.getApprovalAmount()).willReturn(BigDecimal.valueOf(5_500));

      CardApprovalItemResponse approval2 = mock(CardApprovalItemResponse.class);
      given(approval2.getAccountTransactionId()).willReturn(9002L);
      given(approval2.getApprovalStatus()).willReturn("APPROVED");
      given(approval2.getMerchantCategory()).willReturn("CAFE");
      given(approval2.getApprovalAmount()).willReturn(BigDecimal.valueOf(4_500));

      CardApprovalListResponse approvalListResponse = mock(CardApprovalListResponse.class);
      given(approvalListResponse.getContent()).willReturn(List.of(approval1, approval2));
      given(cardInternalClient.getApprovals(anyLong(), isNull(), isNull(), anyInt(), anyInt()))
          .willReturn(ApiResponse.success(approvalListResponse));

      List<CategoryResponse> result = bankMyDataService.getTransactionCategories(ACCOUNT_ID);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).category()).isEqualTo("CAFE");
      assertThat(result.get(0).amount()).isEqualTo(10_000L);
    }

    @Test
    @DisplayName("성공 - 카드 없을 때 빈 리스트 반환")
    void success_noCards() {
      CardListResponse cardListResponse = mock(CardListResponse.class);
      given(cardListResponse.getContent()).willReturn(Collections.emptyList());
      given(cardInternalClient.getCardsByAccountId(ACCOUNT_ID))
          .willReturn(ApiResponse.success(cardListResponse));

      List<CategoryResponse> result = bankMyDataService.getTransactionCategories(ACCOUNT_ID);

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("성공 - merchantCategory가 null인 승인내역은 집계 제외")
    void success_nullCategorySkipped() {
      CardItemResponse card = mock(CardItemResponse.class);
      given(card.getCardId()).willReturn(1L);
      given(card.getCardNumber()).willReturn("1234-****-****-5678");
      CardListResponse cardListResponse = mock(CardListResponse.class);
      given(cardListResponse.getContent()).willReturn(List.of(card));
      given(cardInternalClient.getCardsByAccountId(ACCOUNT_ID))
          .willReturn(ApiResponse.success(cardListResponse));

      CardApprovalItemResponse approval = mock(CardApprovalItemResponse.class);
      given(approval.getAccountTransactionId()).willReturn(9001L);
      given(approval.getApprovalStatus()).willReturn("APPROVED");
      given(approval.getMerchantCategory()).willReturn(null);
      CardApprovalListResponse approvalListResponse = mock(CardApprovalListResponse.class);
      given(approvalListResponse.getContent()).willReturn(List.of(approval));
      given(cardInternalClient.getApprovals(anyLong(), isNull(), isNull(), anyInt(), anyInt()))
          .willReturn(ApiResponse.success(approvalListResponse));

      List<CategoryResponse> result = bankMyDataService.getTransactionCategories(ACCOUNT_ID);

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("성공 - 카드 조회 Feign 실패 시 빈 리스트 반환")
    void success_cardFeignException() {
      given(cardInternalClient.getCardsByAccountId(ACCOUNT_ID))
          .willThrow(mock(FeignException.class));

      List<CategoryResponse> result = bankMyDataService.getTransactionCategories(ACCOUNT_ID);

      assertThat(result).isEmpty();
    }
  }
}
