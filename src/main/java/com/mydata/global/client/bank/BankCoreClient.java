package com.mydata.global.client.bank;

import com.mydata.global.client.exception.CoreClientExceptionHandler;
import com.mydata.global.config.properties.BankClientProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class BankCoreClient {

  private final WebClient webClient;
  private final BankClientProperties properties;

  public String getAccounts() {

    return webClient
        .get()
        .uri(properties.baseUrl() + "/baas/v1/bank")
        .retrieve()
        .onStatus(status -> status.isError(), CoreClientExceptionHandler::handle)
        .bodyToMono(String.class)
        .block();
  }
}
