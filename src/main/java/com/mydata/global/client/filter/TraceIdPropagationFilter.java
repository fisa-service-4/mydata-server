package com.mydata.global.client.filter;

import com.mydata.global.logging.TraceIdConstants;
import org.slf4j.MDC;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

public final class TraceIdPropagationFilter {

  private TraceIdPropagationFilter() {}

  public static ExchangeFilterFunction traceIdFilter() {

    return (request, next) -> {
      String traceId = MDC.get(TraceIdConstants.TRACE_ID_MDC_KEY);

      ClientRequest clientRequest =
          ClientRequest.from(request).header(TraceIdConstants.TRACE_ID_HEADER, traceId).build();

      return next.exchange(clientRequest);
    };
  }
}
