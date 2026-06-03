package com.mydata.domain.stock.client.dto.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockAccountResponse {

  private Long accountId;

  private String accountNumber;

  private String accountName;

  private String bankCode;

  private BigDecimal cashBalance;
}
