package com.mydata.domain.mydata.controller;

import com.mydata.domain.mydata.dto.request.ConnectRequest;
import com.mydata.domain.mydata.dto.response.ConnectResponse;
import com.mydata.domain.mydata.dto.response.ConnectionResponse;
import com.mydata.domain.mydata.dto.response.SyncResponse;
import com.mydata.domain.mydata.service.MyDataService;
import com.mydata.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MyData API", description = "마이데이터 연동 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mydata/v1")
public class MyDataController {

  private final MyDataService myDataService;

  @Operation(summary = "마이데이터 연동", description = "금융기관과의 마이데이터 연동을 요청합니다.")
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
                                "connected": true,
                                "bankLinked": true,
                                "stockLinked": false
                              },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (헤더 누락 또는 유효하지 않은 값)",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "VALID_001", "message": "X-User-Id 헤더가 필요합니다" },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @PostMapping("/connect")
  public ApiResponse<ConnectResponse> connect(@Valid @RequestBody ConnectRequest request) {

    return ApiResponse.success(myDataService.connect(request));
  }

  @Operation(summary = "연동 목록 조회", description = "현재 연동된 금융 계좌 목록을 조회합니다.")
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
                                "bankAccounts": [
                                  {
                                    "accountId": 1001,
                                    "accountNumber": "110-123-456789",
                                    "accountName": "내 급여통장",
                                    "bankCode": "088",
                                    "balance": 3500000
                                  }
                                ],
                                "stockAccounts": []
                              },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (헤더 누락 또는 유효하지 않은 값)",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "VALID_001", "message": "X-User-Id 헤더가 필요합니다" },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @GetMapping("/connections")
  public ApiResponse<ConnectionResponse> getConnections() {

    return ApiResponse.success(myDataService.getConnections());
  }

  @Operation(summary = "마이데이터 동기화", description = "은행/증권 마이데이터를 최신 상태로 동기화합니다.")
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
                                "synced": true,
                                "syncedAt": "2026-05-26T10:00:00"
                              },
                              "meta": { "traceId": "uuid" }
                            }
                            """))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (헤더 누락 또는 유효하지 않은 값)",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                            {
                              "success": false,
                              "error": { "code": "VALID_001", "message": "X-User-Id 헤더가 필요합니다" },
                              "meta": { "traceId": "uuid" }
                            }
                            """)))
  })
  @PostMapping("/sync")
  public ApiResponse<SyncResponse> sync() {

    return ApiResponse.success(myDataService.sync());
  }
}
