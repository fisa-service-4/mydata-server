package com.mydata.domain.bank.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BankBalanceResponse {

  private Long accountId;

  private Long balance;

  private Long availableBalance;
}
