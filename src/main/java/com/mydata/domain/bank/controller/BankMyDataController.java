package com.mydata.domain.bank.controller;

import com.mydata.domain.bank.dto.request.TransactionSearchRequest;
import com.mydata.domain.bank.dto.response.*;
import com.mydata.domain.bank.service.BankMyDataService;
import com.mydata.global.logging.TraceIdConstants;
import com.mydata.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bank MyData API", description = "은행 마이데이터 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mydata/v1/bank/accounts")
public class BankMyDataController {

  private final BankMyDataService bankMyDataService;

  @Operation(summary = "계좌 조회", description = "사용자의 전체 계좌 목록을 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "성공",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": true,
                              "data": [
                                {
                                  "accountId": 1001,
                                  "accountNumber": "110-123-456789",
                                  "accountName": "내 급여통장",
                                  "bankCode": "088",
                                  "balance": 3500000
                                }
                              ],
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "은행 API 호출 오류",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "BANK_500", "message": "은행 API 호출 중 오류가 발생했습니다." },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping
  public ApiResponse<List<AccountSummaryResponse>> getAccounts(
      @RequestHeader(TraceIdConstants.USER_ID_HEADER) @Positive Long userId) {

    return ApiResponse.success(bankMyDataService.getAccounts(userId));
  }

  @Operation(summary = "계좌 상세 조회")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "성공",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": true,
                              "data": {
                                "accountId": 1001,
                                "accountNumber": "110-123-456789",
                                "accountName": "내 급여통장",
                                "bankCode": "088",
                                "accountStatus": "ACTIVE",
                                "balance": 3500000
                              },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "계좌 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "ACCOUNT_001", "message": "계좌 없음" },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/{accountId}")
  public ApiResponse<AccountDetailResponse> getAccountDetail(
      @RequestHeader(TraceIdConstants.USER_ID_HEADER) @Positive Long userId,
      @PathVariable @Positive Long accountId) {

    return ApiResponse.success(bankMyDataService.getAccountDetail(userId, accountId));
  }

  @Operation(summary = "잔액 조회")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "성공",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": true,
                              "data": {
                                "accountId": 1001,
                                "balance": 3500000,
                                "availableBalance": 3200000
                              },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "계좌 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "ACCOUNT_001", "message": "계좌 없음" },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/{accountId}/balance")
  public ApiResponse<BalanceResponse> getBalance(
      @RequestHeader(TraceIdConstants.USER_ID_HEADER) @Positive Long userId,
      @PathVariable @Positive Long accountId) {

    return ApiResponse.success(bankMyDataService.getBalance(userId, accountId));
  }

  @Operation(summary = "거래내역 조회", description = "계좌 거래내역을 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "성공",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": true,
                              "data": [
                                {
                                  "transactionId": 9001,
                                  "transactionDateTime": "2026-05-01T09:00:00",
                                  "transactionType": "DEPOSIT",
                                  "amount": 3000000,
                                  "balanceAfter": 3500000,
                                  "description": "급여"
                                }
                              ],
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "날짜 형식 오류",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "VALID_001", "message": "fromDate: 날짜 형식은 YYYY-MM-DD여야 합니다" },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/{accountId}/transactions")
  public ApiResponse<List<TransactionResponse>> getTransactions(
      @RequestHeader(TraceIdConstants.USER_ID_HEADER) @Positive Long userId,
      @PathVariable @Positive Long accountId,
      @RequestParam(required = false)
          @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 YYYY-MM-DD여야 합니다")
          String fromDate,
      @RequestParam(required = false)
          @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 YYYY-MM-DD여야 합니다")
          String toDate,
      @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
      @RequestParam(defaultValue = "20") @Positive Integer size) {

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
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "성공",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": true,
                              "data": [
                                { "category": "급여", "amount": 3000000 },
                                { "category": "식비", "amount": 280000 }
                              ],
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "계좌 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "ACCOUNT_001", "message": "계좌 없음" },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/{accountId}/transactions/categories")
  public ApiResponse<List<CategoryResponse>> getTransactionCategories(
      @RequestHeader(TraceIdConstants.USER_ID_HEADER) @Positive Long userId,
      @PathVariable @Positive Long accountId) {

    return ApiResponse.success(bankMyDataService.getTransactionCategories(userId, accountId));
  }
}
