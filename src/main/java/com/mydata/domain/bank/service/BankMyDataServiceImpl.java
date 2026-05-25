package com.mydata.domain.bank.service;

import com.mydata.domain.bank.client.dto.response.*;
import com.mydata.domain.bank.client.internal.BankInternalClient;
import com.mydata.domain.bank.dto.request.TransactionSearchRequest;
import com.mydata.domain.bank.dto.response.*;
import com.mydata.domain.bank.exception.BankMyDataException;
import com.mydata.domain.bank.mapper.BankMyDataMapper;
import com.mydata.global.exception.ErrorCode;
import com.mydata.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankMyDataServiceImpl implements BankMyDataService {

  private final BankInternalClient bankInternalClient;
  private final BankMyDataMapper bankMyDataMapper;

  @Override
  public List<AccountSummaryResponse> getAccounts(Long userId) {

    ApiResponse<List<BankAccountResponse>> response = bankInternalClient.getAccounts(userId);

    if (response == null || response.data() == null) {
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR);
    }

    return bankMyDataMapper.toAccountSummaryList(response.data());
  }

  @Override
  public AccountDetailResponse getAccountDetail(Long userId, Long accountId) {

    ApiResponse<BankAccountDetailResponse> response =
        bankInternalClient.getAccountDetail(userId, accountId);

    if (response == null || response.data() == null) {
      throw new BankMyDataException(ErrorCode.ACCOUNT_001);
    }

    return bankMyDataMapper.toAccountDetail(response.data());
  }

  @Override
  public BalanceResponse getBalance(Long userId, Long accountId) {

    ApiResponse<BankBalanceResponse> response = bankInternalClient.getBalance(userId, accountId);

    if (response == null || response.data() == null) {
      throw new BankMyDataException(ErrorCode.ACCOUNT_001);
    }

    return bankMyDataMapper.toBalanceResponse(response.data());
  }

  @Override
  public List<TransactionResponse> getTransactions(
      Long userId, Long accountId, TransactionSearchRequest request) {

    ApiResponse<List<BankTransactionResponse>> response =
        bankInternalClient.getTransactions(
            userId,
            accountId,
            request.fromDate(),
            request.toDate(),
            request.page(),
            request.size());

    if (response == null || response.data() == null) {
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR);
    }

    return bankMyDataMapper.toTransactionResponseList(response.data());
  }

  @Override
  public List<CategoryResponse> getTransactionCategories(Long userId, Long accountId) {

    ApiResponse<List<BankTransactionCategoryResponse>> response =
        bankInternalClient.getTransactionCategories(userId, accountId);

    if (response == null || response.data() == null) {
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR);
    }

    return bankMyDataMapper.toCategoryResponseList(response.data());
  }
}
