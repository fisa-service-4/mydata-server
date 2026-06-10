package com.mydata.domain.bank.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BankTransactionResponse {

  private Long transactionId;

  private String transactionAt;

  private String transactionType;

  private Long amount;

  private Long balanceAfter;

  private String description;
}
