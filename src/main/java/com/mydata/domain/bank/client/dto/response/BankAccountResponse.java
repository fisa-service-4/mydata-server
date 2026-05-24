package com.mydata.domain.bank.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BankAccountResponse {

  private Long accountId;

  private String accountNumber;

  private String accountName;

  private String bankCode;

  private Long balance;
}
