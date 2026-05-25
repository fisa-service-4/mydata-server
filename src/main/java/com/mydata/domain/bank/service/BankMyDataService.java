package com.mydata.domain.bank.service;

import com.mydata.domain.bank.dto.request.TransactionSearchRequest;
import com.mydata.domain.bank.dto.response.*;
import java.util.List;

public interface BankMyDataService {

  List<AccountSummaryResponse> getAccounts(Long userId);

  AccountDetailResponse getAccountDetail(Long userId, Long accountId);

  BalanceResponse getBalance(Long userId, Long accountId);

  List<TransactionResponse> getTransactions(
      Long userId, Long accountId, TransactionSearchRequest request);

  List<CategoryResponse> getTransactionCategories(Long userId, Long accountId);
}
