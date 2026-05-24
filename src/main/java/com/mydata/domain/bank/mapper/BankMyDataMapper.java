package com.mydata.domain.bank.mapper;

import com.mydata.domain.bank.client.dto.response.*;
import com.mydata.domain.bank.dto.response.*;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankMyDataMapper {

  AccountSummaryResponse toAccountSummary(BankAccountResponse response);

  List<AccountSummaryResponse> toAccountSummaryList(List<BankAccountResponse> responses);

  AccountDetailResponse toAccountDetail(BankAccountDetailResponse response);

  BalanceResponse toBalanceResponse(BankBalanceResponse response);

  TransactionResponse toTransactionResponse(BankTransactionResponse response);

  List<TransactionResponse> toTransactionResponseList(List<BankTransactionResponse> responses);

  CategoryResponse toCategoryResponse(BankTransactionCategoryResponse response);

  List<CategoryResponse> toCategoryResponseList(List<BankTransactionCategoryResponse> responses);
}
