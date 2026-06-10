package com.mydata.domain.bank.client.internal;

import com.mydata.domain.bank.client.dto.response.*;
import com.mydata.global.config.FeignConfig;
import com.mydata.global.logging.TraceIdConstants;
import com.mydata.global.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "bankInternalClient",
    url = "${external.baas.url}",
    configuration = FeignConfig.class)
public interface BankInternalClient {

  @GetMapping("/baas/v1/bank/accounts")
  ApiResponse<BankAccountListResponse> getAccounts(
      @RequestHeader(TraceIdConstants.FIREBASE_UID_HEADER) String firebaseUid);

  @GetMapping("/baas/v1/bank/accounts/{accountId}")
  ApiResponse<BankAccountDetailResponse> getAccountDetail(@PathVariable Long accountId);

  @GetMapping("/baas/v1/bank/accounts/{accountId}/balance")
  ApiResponse<BankBalanceResponse> getBalance(@PathVariable Long accountId);

  @GetMapping("/baas/v1/bank/accounts/{accountId}/transactions")
  ApiResponse<BankTransactionListResponse> getTransactions(
      @PathVariable Long accountId,
      @RequestParam(required = false) String fromDate,
      @RequestParam(required = false) String toDate,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size);
}
