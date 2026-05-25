package com.mydata.domain.stock.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockAssetSummaryResponse {

  private Long totalAssetAmount;

  private Long totalPurchaseAmount;

  private Long totalEvaluationAmount;

  private Long totalProfitAmount;

  private Double totalProfitRate;
}
