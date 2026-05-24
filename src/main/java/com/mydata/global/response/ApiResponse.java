package com.mydata.global.response;

import lombok.Builder;

@Builder
public record ApiResponse<T>(boolean success, T data, Meta meta) {

  public static <T> ApiResponse<T> success(T data, String traceId) {
    return ApiResponse.<T>builder().success(true).data(data).meta(new Meta(traceId)).build();
  }

  public static ApiResponse<Void> success(String traceId) {
    return ApiResponse.<Void>builder().success(true).meta(new Meta(traceId)).build();
  }
}
