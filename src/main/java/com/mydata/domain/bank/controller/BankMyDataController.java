package com.mydata.domain.bank.controller;

import com.mydata.domain.bank.dto.request.TransactionSearchRequest;
import com.mydata.domain.bank.dto.response.*;
import com.mydata.domain.bank.service.BankMyDataService;
import com.mydata.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bank MyData API", description = "은행 마이데이터 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/mydata/v1/accounts")
public class BankMyDataController {

  private final BankMyDataService bankMyDataService;

  @Operation(summary = "계좌 조회")
  @GetMapping
  public ApiResponse<List<AccountSummaryResponse>> getAccounts(
      @RequestHeader("X-User-Id") Long userId) {

    return ApiResponse.success(bankMyDataService.getAccounts(userId));
  }

  @Operation(summary = "계좌 상세 조회")
  @GetMapping("/{accountId}")
  public ApiResponse<AccountDetailResponse> getAccountDetail(
      @RequestHeader("X-User-Id") Long userId, @PathVariable Long accountId) {

    return ApiResponse.success(bankMyDataService.getAccountDetail(userId, accountId));
  }

  @Operation(summary = "잔액 조회")
  @GetMapping("/{accountId}/balance")
  public ApiResponse<BalanceResponse> getBalance(
      @RequestHeader("X-User-Id") Long userId, @PathVariable Long accountId) {

    return ApiResponse.success(bankMyDataService.getBalance(userId, accountId));
  }

  @Operation(summary = "거래내역 조회")
  @GetMapping("/{accountId}/transactions")
  public ApiResponse<List<TransactionResponse>> getTransactions(
      @RequestHeader("X-User-Id") Long userId,
      @PathVariable Long accountId,
      @RequestParam(required = false) String fromDate,
      @RequestParam(required = false) String toDate,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {

    TransactionSearchRequest request =
        TransactionSearchRequest.builder()
            .fromDate(fromDate)
            .toDate(toDate)
            .page(page)
            .size(size)
            .build();

    return ApiResponse.success(bankMyDataService.getTransactions(userId, accountId, request));
  }

  @Operation(summary = "거래 카테고리 조회")
  @GetMapping("/{accountId}/transactions/categories")
  public ApiResponse<List<CategoryResponse>> getTransactionCategories(
      @RequestHeader("X-User-Id") Long userId, @PathVariable Long accountId) {

    return ApiResponse.success(bankMyDataService.getTransactionCategories(userId, accountId));
  }
}
