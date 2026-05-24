package com.mydata.domain.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountSummaryResponse {

  private Long accountId;

  private String accountNumber;

  private String accountName;

  private String bankName;

  private Long balance;
}
