package com.mydata.global.exception;

import com.mydata.global.logging.TraceIdConstants;
import com.mydata.global.response.ErrorResponse;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;

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

    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.of(
                ErrorCode.VALID_001.getCode(), ErrorCode.VALID_001.getMessage(), traceId));
  }

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

  @ExceptionHandler(WebClientRequestException.class)
  public ResponseEntity<ErrorResponse> handleWebClientException(
      WebClientRequestException e, HttpServletRequest request) {

    String traceId = request.getHeader(TraceIdConstants.TRACE_ID_HEADER);

    return ResponseEntity.internalServerError()
        .body(
            ErrorResponse.of(
                ErrorCode.MYDATA_003.getCode(), ErrorCode.MYDATA_003.getMessage(), traceId));
  }

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<ErrorResponse> handleFeignException(
      FeignException e, HttpServletRequest request) {

    String traceId = request.getHeader(TraceIdConstants.TRACE_ID_HEADER);

    return ResponseEntity.internalServerError()
        .body(
            ErrorResponse.of(
                ErrorCode.MYDATA_003.getCode(), ErrorCode.MYDATA_003.getMessage(), traceId));
  }
}
