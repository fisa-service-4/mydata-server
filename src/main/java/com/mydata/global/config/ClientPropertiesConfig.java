package com.mydata.global.config;

import com.mydata.global.config.properties.BankClientProperties;
import com.mydata.global.config.properties.StockClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({BankClientProperties.class, StockClientProperties.class})
public class ClientPropertiesConfig {}
