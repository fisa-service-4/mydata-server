package com.mydata.domain.stock.client.internal;

import com.mydata.domain.stock.client.dto.response.StockAccountResponse;
import com.mydata.global.config.FeignConfig;
import com.mydata.global.response.ApiResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "stockInternalClient",
    url = "${external.baas.url}",
    configuration = FeignConfig.class)
public interface StockInternalClient {

  @GetMapping("/baas/v1/stock/accounts")
  ApiResponse<List<StockAccountResponse>> getAccounts(@RequestHeader("X-User-Id") Long userId);
}
