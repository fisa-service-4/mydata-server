package com.mydata.domain.bank.dto.response;

import lombok.Builder;

@Builder
public record AccountSummaryResponse(
    Long accountId, String accountNumber, String accountName, String bankCode, Long balance) {}
