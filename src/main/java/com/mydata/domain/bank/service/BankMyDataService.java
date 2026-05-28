package com.mydata.domain.bank.service;

import com.mydata.domain.bank.dto.request.TransactionSearchRequest;
import com.mydata.domain.bank.dto.response.*;
import java.util.List;

public interface BankMyDataService {

  List<AccountSummaryResponse> getAccounts();

  AccountDetailResponse getAccountDetail(Long accountId);

  BalanceResponse getBalance(Long accountId);

  List<TransactionResponse> getTransactions(Long accountId, TransactionSearchRequest request);

  List<CategoryResponse> getTransactionCategories(Long accountId);
}
