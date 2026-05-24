package com.mydata.global.exception;

import com.mydata.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(
      BusinessException e, HttpServletRequest request) {

    ErrorCode errorCode = e.getErrorCode();

    String traceId = request.getHeader("X-Trace-Id");

    return ResponseEntity.status(errorCode.getStatus())
        .body(ErrorResponse.of(errorCode.getCode(), errorCode.getMessage(), traceId));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException e, HttpServletRequest request) {

    String traceId = request.getHeader("X-Trace-Id");

    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.of(
                ErrorCode.VALID_001.getCode(), ErrorCode.VALID_001.getMessage(), traceId));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {

    log.error("Unhandled Exception", e);

    String traceId = request.getHeader("X-Trace-Id");

    return ResponseEntity.internalServerError()
        .body(
            ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                traceId));
  }
}
