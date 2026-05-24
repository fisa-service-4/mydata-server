package com.mydata.global.config;

import com.mydata.global.client.filter.TraceIdPropagationFilter;
import io.netty.channel.ChannelOption;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

  @Value("${webclient.timeout.connect}")
  private int connectTimeout;

  @Value("${webclient.timeout.read}")
  private int readTimeout;

  @Bean
  public WebClient webClient() {

    HttpClient httpClient =
        HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
            .responseTimeout(Duration.ofMillis(readTimeout));

    return WebClient.builder()
        .filter(TraceIdPropagationFilter.traceIdFilter())
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }
}
