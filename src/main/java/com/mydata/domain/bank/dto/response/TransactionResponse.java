package com.mydata.domain.bank.dto.response;

import lombok.Builder;

@Builder
public record TransactionResponse(
    Long transactionId,
    String transactionDateTime,
    String transactionType,
    Long amount,
    Long balanceAfter,
    String description) {}
