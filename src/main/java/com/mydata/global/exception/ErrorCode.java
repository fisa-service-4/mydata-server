package com.mydata.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // VALID
  VALID_001(HttpStatus.BAD_REQUEST, "VALID_001", "입력값 오류"),
  VALID_002(HttpStatus.BAD_REQUEST, "VALID_002", "필수값 누락"),

  // AUTH
  AUTH_001(HttpStatus.CONFLICT, "AUTH_001", "이미 가입된 이메일"),
  AUTH_002(HttpStatus.CONFLICT, "AUTH_002", "이미 가입된 전화번호"),
  AUTH_003(HttpStatus.UNAUTHORIZED, "AUTH_003", "이메일 또는 비밀번호 불일치"),
  AUTH_004(HttpStatus.UNAUTHORIZED, "AUTH_004", "만료된 토큰"),
  AUTH_005(HttpStatus.UNAUTHORIZED, "AUTH_005", "유효하지 않은 토큰"),

  // USER
  USER_001(HttpStatus.NOT_FOUND, "USER_001", "사용자 없음"),

  // MYDATA
  MYDATA_001(HttpStatus.CONFLICT, "MYDATA_001", "이미 연동된 기관"),
  MYDATA_002(HttpStatus.NOT_FOUND, "MYDATA_002", "연동 정보 없음"),
  MYDATA_003(HttpStatus.INTERNAL_SERVER_ERROR, "MYDATA_003", "마이데이터 연동 실패"),

  // ACCOUNT
  ACCOUNT_001(HttpStatus.NOT_FOUND, "ACCOUNT_001", "계좌 없음"),
  ACCOUNT_002(HttpStatus.FORBIDDEN, "ACCOUNT_002", "본인 계좌가 아닙니다"),
  ACCOUNT_003(HttpStatus.BAD_REQUEST, "ACCOUNT_003", "계좌 상태 오류"),

  // TRANSACTION
  TRANSACTION_001(HttpStatus.NOT_FOUND, "TRANSACTION_001", "거래내역 없음"),

  // STOCK
  STOCK_001(HttpStatus.NOT_FOUND, "STOCK_001", "종목 없음"),
  STOCK_002(HttpStatus.INTERNAL_SERVER_ERROR, "STOCK_002", "현재가 조회 실패"),

  // HOLDING
  HOLDING_001(HttpStatus.NOT_FOUND, "HOLDING_001", "보유 종목 없음"),

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
