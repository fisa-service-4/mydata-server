package com.mydata.domain.bank.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mydata.domain.bank.dto.response.AccountDetailResponse;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.dto.response.BalanceResponse;
import com.mydata.domain.bank.dto.response.CategoryResponse;
import com.mydata.domain.bank.dto.response.TransactionResponse;
import com.mydata.domain.bank.service.BankMyDataService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BankMyDataController.class)
class BankMyDataControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private BankMyDataService bankMyDataService;

  @Nested
  @DisplayName("계좌 조회 GET /mydata/v1/bank/accounts")
  class GetAccounts {

    @Test
    @DisplayName("성공 - 계좌 목록 반환")
    void success() throws Exception {
      given(bankMyDataService.getAccounts(anyString()))
          .willReturn(
              List.of(
                  AccountSummaryResponse.builder()
                      .accountId(1001L)
                      .accountNumber("110-123-456789")
                      .accountName("내 급여통장")
                      .bankCode("088")
                      .balance(3500000L)
                      .build()));

      mockMvc
          .perform(get("/mydata/v1/bank/accounts").header("X-Firebase-Uid", "test-uid"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data[0].accountId").value(1001))
          .andExpect(jsonPath("$.data[0].bankCode").value("088"))
          .andExpect(jsonPath("$.data[0].balance").value(3500000));
    }

    @Test
    @DisplayName("실패 - X-Firebase-Uid 헤더 누락 시 400")
    void missingHeader_returns400() throws Exception {
      mockMvc
          .perform(get("/mydata/v1/bank/accounts"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.error.code").value("VALID_001"));
    }
  }

  @Nested
  @DisplayName("계좌 상세 조회 GET /mydata/v1/bank/accounts/{accountId}")
  class GetAccountDetail {

    @Test
    @DisplayName("성공 - 계좌 상세 정보 반환")
    void success() throws Exception {
      given(bankMyDataService.getAccountDetail(anyLong()))
          .willReturn(
              AccountDetailResponse.builder()
                  .accountId(1001L)
                  .accountNumber("110-123-456789")
                  .accountName("내 급여통장")
                  .bankCode("088")
                  .accountStatus("ACTIVE")
                  .balance(3500000L)
                  .build());

      mockMvc
          .perform(get("/mydata/v1/bank/accounts/1001"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.accountId").value(1001))
          .andExpect(jsonPath("$.data.accountStatus").value("ACTIVE"))
          .andExpect(jsonPath("$.data.balance").value(3500000));
    }
  }

  @Nested
  @DisplayName("잔액 조회 GET /mydata/v1/bank/accounts/{accountId}/balance")
  class GetBalance {

    @Test
    @DisplayName("성공 - 잔액 정보 반환")
    void success() throws Exception {
      given(bankMyDataService.getBalance(anyLong()))
          .willReturn(
              BalanceResponse.builder()
                  .accountId(1001L)
                  .balance(3500000L)
                  .availableBalance(3200000L)
                  .build());

      mockMvc
          .perform(get("/mydata/v1/bank/accounts/1001/balance"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.accountId").value(1001))
          .andExpect(jsonPath("$.data.balance").value(3500000))
          .andExpect(jsonPath("$.data.availableBalance").value(3200000));
    }
  }

  @Nested
  @DisplayName("거래내역 조회 GET /mydata/v1/bank/accounts/{accountId}/transactions")
  class GetTransactions {

    @Test
    @DisplayName("성공 - 거래내역 목록 반환")
    void success() throws Exception {
      given(bankMyDataService.getTransactions(anyLong(), any()))
          .willReturn(
              List.of(
                  TransactionResponse.builder()
                      .transactionId(9001L)
                      .transactionDateTime("2026-05-01T09:00:00")
                      .transactionType("DEPOSIT")
                      .amount(3000000L)
                      .balanceAfter(3500000L)
                      .description("급여")
                      .build()));

      mockMvc
          .perform(
              get("/mydata/v1/bank/accounts/1001/transactions")
                  .param("fromDate", "2026-05-01")
                  .param("toDate", "2026-05-31"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data[0].transactionId").value(9001))
          .andExpect(jsonPath("$.data[0].transactionType").value("DEPOSIT"))
          .andExpect(jsonPath("$.data[0].amount").value(3000000));
    }

    @Test
    @DisplayName("실패 - fromDate 날짜 형식 오류 시 400")
    void invalidDateFormat_returns400() throws Exception {
      mockMvc
          .perform(get("/mydata/v1/bank/accounts/1001/transactions").param("fromDate", "20260501"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.success").value(false));
    }
  }

  @Nested
  @DisplayName("거래 카테고리 조회 GET /mydata/v1/bank/accounts/{accountId}/transactions/categories")
  class GetTransactionCategories {

    @Test
    @DisplayName("성공 - 카테고리별 합계 반환")
    void success() throws Exception {
      given(bankMyDataService.getTransactionCategories(anyLong()))
          .willReturn(
              List.of(
                  CategoryResponse.builder().category("급여").amount(3000000L).build(),
                  CategoryResponse.builder().category("식비").amount(280000L).build()));

      mockMvc
          .perform(get("/mydata/v1/bank/accounts/1001/transactions/categories"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data[0].category").value("급여"))
          .andExpect(jsonPath("$.data[0].amount").value(3000000))
          .andExpect(jsonPath("$.data[1].category").value("식비"));
    }
  }
}
