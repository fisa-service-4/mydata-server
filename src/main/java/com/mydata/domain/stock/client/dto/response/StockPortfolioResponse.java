package com.mydata.domain.stock.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockPortfolioResponse {

  private Long totalEvaluationAmount;

  private Long totalPurchaseAmount;

  private Long totalProfitAmount;

  private Double totalProfitRate;
}
