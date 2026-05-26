package com.mydata.global.exception;

import com.mydata.global.logging.TraceIdConstants;
import com.mydata.global.response.ErrorResponse;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(
      BusinessException e, HttpServletRequest request) {

    ErrorCode errorCode = e.getErrorCode();
    String traceId = request.getHeader(TraceIdConstants.TRACE_ID_HEADER);

    return ResponseEntity.status(errorCode.getStatus())
        .body(ErrorResponse.of(errorCode.getCode(), errorCode.getMessage(), traceId));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException e, HttpServletRequest request) {

    String traceId = request.getHeader(TraceIdConstants.TRACE_ID_HEADER);
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .findFirst()
            .orElse(ErrorCode.VALID_001.getMessage());

    return ResponseEntity.badRequest()
        .body(ErrorResponse.of(ErrorCode.VALID_001.getCode(), message, traceId));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException e, HttpServletRequest request) {

    String traceId = request.getHeader(TraceIdConstants.TRACE_ID_HEADER);
    String message =
        e.getConstraintViolations().stream()
            .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
            .findFirst()
            .orElse(ErrorCode.VALID_001.getMessage());

    return ResponseEntity.badRequest()
        .body(ErrorResponse.of(ErrorCode.VALID_001.getCode(), message, traceId));
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(
      MissingRequestHeaderException e, HttpServletRequest request) {

    String traceId = request.getHeader(TraceIdConstants.TRACE_ID_HEADER);
    String message = e.getHeaderName() + " 헤더가 필요합니다";

    return ResponseEntity.badRequest()
        .body(ErrorResponse.of(ErrorCode.VALID_001.getCode(), message, traceId));
  }

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<ErrorResponse> handleFeignException(
      FeignException e, HttpServletRequest request) {

    log.error("FeignException: status={}, message={}", e.status(), e.getMessage());
    String traceId = request.getHeader(TraceIdConstants.TRACE_ID_HEADER);

    return ResponseEntity.internalServerError()
        .body(
            ErrorResponse.of(
                ErrorCode.MYDATA_003.getCode(), ErrorCode.MYDATA_003.getMessage(), traceId));
  }

  @SuppressWarnings("java:S2221")
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {

    log.error("Unhandled Exception", e);
    String traceId = request.getHeader(TraceIdConstants.TRACE_ID_HEADER);

    return ResponseEntity.internalServerError()
        .body(
            ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                traceId));
  }
}
