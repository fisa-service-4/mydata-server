package com.mydata.domain.bank.controller;

import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.service.BankMyDataService;
import com.mydata.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
