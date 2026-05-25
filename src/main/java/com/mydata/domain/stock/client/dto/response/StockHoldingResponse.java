package com.mydata.domain.stock.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockHoldingResponse {

  private String stockCode;

  private String stockName;

  private Long quantity;

  private Long averagePrice;

  private Long currentPrice;

  private Long evaluationAmount;

  private Double profitRate;
}
