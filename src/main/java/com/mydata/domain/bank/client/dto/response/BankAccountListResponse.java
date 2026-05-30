package com.mydata.domain.bank.client.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BankAccountListResponse {
  private List<BankAccountResponse> content;
}
