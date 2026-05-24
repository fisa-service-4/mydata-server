package com.mydata.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "client.stock")
public record StockClientProperties(String baseUrl) {}
