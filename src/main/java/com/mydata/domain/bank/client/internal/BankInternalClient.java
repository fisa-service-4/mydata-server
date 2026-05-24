package com.mydata.domain.bank.client.internal;

import com.mydata.domain.bank.client.dto.response.BankAccountResponse;
import com.mydata.global.response.ApiResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "bankInternalClient", url = "${external.baas.url}")
public interface BankInternalClient {

  @GetMapping("/baas/v1/bank/accounts")
  ApiResponse<List<BankAccountResponse>> getAccounts(@RequestHeader("X-User-Id") Long userId);
}
