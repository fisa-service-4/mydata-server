package com.mydata.domain.bank.dto.response;

import lombok.Builder;

@Builder
public record AccountDetailResponse(
    Long accountId,
    String accountNumber,
    String accountName,
    String bankName,
    String accountStatus,
    Long balance) {}
