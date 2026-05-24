package com.mydata.domain.bank.service;

import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import java.util.List;

public interface BankMyDataService {

  List<AccountSummaryResponse> getAccounts(Long userId);
}
