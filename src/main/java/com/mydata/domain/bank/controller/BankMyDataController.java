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
      @RequestHeader(TraceIdConstants.FIREBASE_UID_HEADER) String firebaseUid) {

    return ApiResponse.success(bankMyDataService.getAccounts(firebaseUid));
  }

  @Operation(summary = "계좌 상세 조회", description = "계좌 ID로 계좌 상세 정보를 조회합니다.")
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
      @PathVariable @Positive Long accountId) {

    return ApiResponse.success(bankMyDataService.getAccountDetail(accountId));
  }

  @Operation(summary = "잔액 조회", description = "계좌 ID로 현재 잔액 및 출금 가능 잔액을 조회합니다.")
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
  public ApiResponse<BalanceResponse> getBalance(@PathVariable @Positive Long accountId) {

    return ApiResponse.success(bankMyDataService.getBalance(accountId));
  }

  @Operation(
      summary = "거래내역 조회",
      description =
          """
          계좌 거래내역을 조회합니다.
          카드 결제로 발생한 거래인 경우 merchantName, merchantCategory, maskedCardNumber 필드가 포함됩니다.
          일반 은행 거래(이체·입출금 등)는 해당 필드가 null로 반환됩니다.
          """)
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
                                  "description": "급여",
                                  "merchantName": null,
                                  "merchantCategory": null,
                                  "maskedCardNumber": null
                                },
                                {
                                  "transactionId": 9002,
                                  "transactionDateTime": "2026-05-02T12:30:00",
                                  "transactionType": "WITHDRAW",
                                  "amount": 5500,
                                  "balanceAfter": 3494500,
                                  "description": "카드결제",
                                  "merchantName": "스타벅스 강남점",
                                  "merchantCategory": "CAFE",
                                  "maskedCardNumber": "1234-****-****-5678"
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
  @GetMapping("/{accountId}/transactions")
  public ApiResponse<List<TransactionResponse>> getTransactions(
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

    return ApiResponse.success(bankMyDataService.getTransactions(accountId, request));
  }

  @Operation(
      summary = "거래 카테고리별 합계 조회",
      description =
          """
          카드 결제 승인 내역을 기반으로 가맹점 카테고리별 지출 합계를 반환합니다.
          카드가 연결되지 않은 계좌이거나 카드 승인내역이 없는 경우 빈 배열을 반환합니다.
          """)
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
                                { "category": "CAFE", "amount": 23500 },
                                { "category": "FOOD_BEVERAGE", "amount": 85000 },
                                { "category": "TRANSPORTATION", "amount": 15000 }
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
      @PathVariable @Positive Long accountId) {

    return ApiResponse.success(bankMyDataService.getTransactionCategories(accountId));
  }
}
