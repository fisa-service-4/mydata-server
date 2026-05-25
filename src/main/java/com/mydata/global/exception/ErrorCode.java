package com.mydata.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // VALID
  VALID_001(HttpStatus.BAD_REQUEST, "VALID_001", "입력값 오류"),

  // MYDATA
  MYDATA_003(HttpStatus.INTERNAL_SERVER_ERROR, "MYDATA_003", "마이데이터 연동 실패"),

  // ACCOUNT
  ACCOUNT_001(HttpStatus.NOT_FOUND, "ACCOUNT_001", "계좌 없음"),

  // INTERNAL
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_001", "서버 내부 오류"),

  // BANK
  BANK_INTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "BANK_500", "은행 API 호출 중 오류가 발생했습니다."),

  // STOCK ACCOUNT
  STOCK_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK_ACCOUNT_001", "증권 계좌를 찾을 수 없습니다."),

  STOCK_HOLDING_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK_HOLDING_001", "보유 종목이 없습니다."),

  STOCK_PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK_PORTFOLIO_001", "포트폴리오 정보가 없습니다."),

  STOCK_RETURN_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK_RETURN_001", "수익률 정보가 없습니다."),

  STOCK_ASSET_SUMMARY_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK_ASSET_001", "자산 요약 정보가 없습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
