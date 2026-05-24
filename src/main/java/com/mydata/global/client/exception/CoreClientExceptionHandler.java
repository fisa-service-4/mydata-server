package com.mydata.global.client.exception;

import com.mydata.global.exception.BusinessException;
import com.mydata.global.exception.ErrorCode;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public final class CoreClientExceptionHandler {

  private CoreClientExceptionHandler() {}

  public static Mono<Throwable> handle(ClientResponse response) {

    HttpStatusCode status = response.statusCode();

    if (status.is4xxClientError()) {

      return Mono.error(new BusinessException(ErrorCode.MYDATA_002));
    }

    if (status.is5xxServerError()) {

      return Mono.error(new BusinessException(ErrorCode.MYDATA_003));
    }

    return Mono.error(new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
  }
}
