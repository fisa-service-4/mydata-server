package com.mydata.domain.bank.mapper;

import com.mydata.domain.bank.client.dto.response.BankAccountResponse;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankMyDataMapper {

  AccountSummaryResponse toAccountSummary(BankAccountResponse response);

  List<AccountSummaryResponse> toAccountSummaryList(List<BankAccountResponse> responses);
}
