package com.mydata.global.filter;

import com.mydata.global.logging.TraceIdConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class TraceIdFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String traceId = request.getHeader(TraceIdConstants.TRACE_ID_HEADER);

    if (traceId == null || traceId.isBlank()) {
      traceId = UUID.randomUUID().toString();
    }

    MDC.put(TraceIdConstants.TRACE_ID_MDC_KEY, traceId);

    response.setHeader(TraceIdConstants.TRACE_ID_HEADER, traceId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
