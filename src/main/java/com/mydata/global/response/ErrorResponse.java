package com.mydata.global.response;

import lombok.Builder;

@Builder
public record ErrorResponse(boolean success, ErrorDetail error, Meta meta) {

  public static ErrorResponse of(String code, String message, String traceId) {
    return ErrorResponse.builder()
        .success(false)
        .error(new ErrorDetail(code, message))
        .meta(new Meta(traceId))
        .build();
  }
}
