package com.mydata.domain.mydata.dto.response;

import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record ConnectionResponse(
    List<AccountSummaryResponse> bankAccounts, List<StockAccountSummaryResponse> stockAccounts) {}
