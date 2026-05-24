package com.mydata.domain.bank.service;

import com.mydata.domain.bank.dto.response.AccountDetailResponse;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.dto.response.BalanceResponse;
import java.util.List;

public interface BankMyDataService {

  List<AccountSummaryResponse> getAccounts(Long userId);

  AccountDetailResponse getAccountDetail(Long userId, Long accountId);

  BalanceResponse getBalance(Long userId, Long accountId);
}
