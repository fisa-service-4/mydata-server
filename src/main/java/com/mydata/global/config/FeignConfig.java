package com.mydata.global.config;

import com.mydata.global.logging.TraceIdConstants;
import feign.Logger;
import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

  @Bean
  Logger.Level feignLoggerLevel() {
    return Logger.Level.HEADERS;
  }

  @Bean
  public RequestInterceptor traceIdInterceptor() {
    return requestTemplate -> {
      String traceId = MDC.get(TraceIdConstants.TRACE_ID_MDC_KEY);
      if (traceId != null && !traceId.isBlank()) {
        requestTemplate.header(TraceIdConstants.TRACE_ID_HEADER, traceId);
      }
    };
  }
}
