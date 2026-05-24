package com.mydata.domain.bank.service;

import com.mydata.domain.bank.client.dto.response.BankAccountDetailResponse;
import com.mydata.domain.bank.client.dto.response.BankAccountResponse;
import com.mydata.domain.bank.client.dto.response.BankBalanceResponse;
import com.mydata.domain.bank.client.internal.BankInternalClient;
import com.mydata.domain.bank.dto.response.AccountDetailResponse;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.dto.response.BalanceResponse;
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
      throw new BankMyDataException(ErrorCode.BANK_ACCOUNT_NOT_FOUND);
    }

    return bankMyDataMapper.toAccountDetail(response.data());
  }

  @Override
  public BalanceResponse getBalance(Long userId, Long accountId) {

    ApiResponse<BankBalanceResponse> response = bankInternalClient.getBalance(userId, accountId);

    if (response == null || response.data() == null) {
      throw new BankMyDataException(ErrorCode.BANK_ACCOUNT_NOT_FOUND);
    }

    return bankMyDataMapper.toBalanceResponse(response.data());
  }
}
