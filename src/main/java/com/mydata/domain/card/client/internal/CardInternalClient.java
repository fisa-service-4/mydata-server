package com.mydata.domain.card.client.internal;

import com.mydata.domain.card.client.dto.response.CardApprovalListResponse;
import com.mydata.domain.card.client.dto.response.CardListResponse;
import com.mydata.global.config.FeignConfig;
import com.mydata.global.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "cardInternalClient",
    url = "${external.baas.url}",
    configuration = FeignConfig.class)
public interface CardInternalClient {

  @GetMapping("/baas/v1/card/accounts/{accountId}/cards")
  ApiResponse<CardListResponse> getCardsByAccountId(@PathVariable Long accountId);

  @GetMapping("/baas/v1/card/cards/{cardId}/approvals")
  ApiResponse<CardApprovalListResponse> getApprovals(
      @PathVariable Long cardId,
      @RequestParam(required = false) String fromDate,
      @RequestParam(required = false) String toDate,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "1000") Integer size);
}
