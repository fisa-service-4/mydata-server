package com.mydata.domain.bank.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BankAccountDetailResponse {

  private Long accountId;

  private String accountNumber;

  private String accountName;

  private String bankName;

  private String accountStatus;

  private Long balance;
}
