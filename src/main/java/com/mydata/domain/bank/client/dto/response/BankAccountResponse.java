package com.mydata.domain.bank.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponse {

  private Long accountId;

  private String accountNumber;

  private String accountName;

  private String bankName;

  private Long balance;
}
