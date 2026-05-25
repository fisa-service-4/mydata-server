package com.mydata.domain.stock.client.internal;

import com.mydata.global.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
    name = "stockInternalClient",
    url = "${external.baas.url}",
    configuration = FeignConfig.class)
public interface StockInternalClient {}
