package com.mydata.global.config;

import com.mydata.global.config.properties.StockClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({StockClientProperties.class})
public class ClientPropertiesConfig {}
