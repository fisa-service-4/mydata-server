package com.mydata.domain.stock.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockReturnResponse {

  private Long totalPurchaseAmount;

  private Long totalEvaluationAmount;

  private Long totalProfitAmount;

  private Double totalProfitRate;
}
