package com.mydata.global.client.stock;

import com.mydata.global.client.exception.CoreClientExceptionHandler;
import com.mydata.global.config.properties.StockClientProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class StockCoreClient {

  private final WebClient webClient;
  private final StockClientProperties properties;

  public String getAccounts() {

    return webClient
        .get()
        .uri(properties.baseUrl() + "/baas/v1/stock")
        .retrieve()
        .onStatus(status -> status.isError(), CoreClientExceptionHandler::handle)
        .bodyToMono(String.class)
        .block();
  }
}
