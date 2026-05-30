package com.mydata.domain.bank.service;

import com.mydata.domain.bank.client.dto.response.*;
import com.mydata.domain.bank.client.internal.BankInternalClient;
import com.mydata.domain.bank.dto.request.TransactionSearchRequest;
import com.mydata.domain.bank.dto.response.*;
import com.mydata.domain.bank.exception.BankMyDataException;
import com.mydata.domain.bank.mapper.BankMyDataMapper;
import com.mydata.global.exception.ErrorCode;
import com.mydata.global.response.ApiResponse;
import feign.FeignException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankMyDataServiceImpl implements BankMyDataService {

  private final BankInternalClient bankInternalClient;
  private final BankMyDataMapper bankMyDataMapper;

  @Override
  public List<AccountSummaryResponse> getAccounts(String firebaseUid) {
    try {
      ApiResponse<com.mydata.domain.bank.client.dto.response.BankAccountListResponse> response =
          bankInternalClient.getAccounts(firebaseUid);

      if (response == null || response.data() == null || response.data().getContent() == null) {
        throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR);
      }

      return bankMyDataMapper.toAccountSummaryList(response.data().getContent());

    } catch (FeignException.NotFound e) {
      throw new BankMyDataException(ErrorCode.ACCOUNT_001, e);
    } catch (FeignException e) {
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR, e);
    }
  }

  @Override
  public AccountDetailResponse getAccountDetail(Long accountId) {
    try {
      ApiResponse<BankAccountDetailResponse> response =
          bankInternalClient.getAccountDetail(accountId);

      if (response == null || response.data() == null) {
        throw new BankMyDataException(ErrorCode.ACCOUNT_001);
      }

      return bankMyDataMapper.toAccountDetail(response.data());

    } catch (FeignException.NotFound e) {
      throw new BankMyDataException(ErrorCode.ACCOUNT_001, e);
    } catch (FeignException e) {
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR, e);
    }
  }

  @Override
  public BalanceResponse getBalance(Long accountId) {
    try {
      ApiResponse<BankBalanceResponse> response = bankInternalClient.getBalance(accountId);

      if (response == null || response.data() == null) {
        throw new BankMyDataException(ErrorCode.ACCOUNT_001);
      }

      return bankMyDataMapper.toBalanceResponse(response.data());

    } catch (FeignException.NotFound e) {
      throw new BankMyDataException(ErrorCode.ACCOUNT_001, e);
    } catch (FeignException e) {
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR, e);
    }
  }

  @Override
  public List<TransactionResponse> getTransactions(
      Long accountId, TransactionSearchRequest request) {
    try {
      ApiResponse<List<BankTransactionResponse>> response =
          bankInternalClient.getTransactions(
              accountId, request.fromDate(), request.toDate(), request.page(), request.size());

      if (response == null || response.data() == null) {
        throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR);
      }

      return bankMyDataMapper.toTransactionResponseList(response.data());

    } catch (FeignException.NotFound e) {
      throw new BankMyDataException(ErrorCode.ACCOUNT_001, e);
    } catch (FeignException e) {
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR, e);
    }
  }

  @Override
  public List<CategoryResponse> getTransactionCategories(Long accountId) {
    try {
      ApiResponse<List<BankTransactionCategoryResponse>> response =
          bankInternalClient.getTransactionCategories(accountId);

      if (response == null || response.data() == null) {
        throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR);
      }

      return bankMyDataMapper.toCategoryResponseList(response.data());

    } catch (FeignException.NotFound e) {
      throw new BankMyDataException(ErrorCode.ACCOUNT_001, e);
    } catch (FeignException e) {
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR, e);
    }
  }
}
