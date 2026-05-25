package com.mydata.domain.stock.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockAccountResponse {

  private Long accountId;

  private String accountNumber;

  private String accountName;

  private Long availableCash;
}
