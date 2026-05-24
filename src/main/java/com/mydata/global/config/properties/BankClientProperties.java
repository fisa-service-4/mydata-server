package com.mydata.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "client.bank")
public record BankClientProperties(String baseUrl) {}
