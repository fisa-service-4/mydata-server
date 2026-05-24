package com.mydata.domain.bank.dto.request;

import lombok.Builder;

@Builder
public record TransactionSearchRequest(
    String fromDate, String toDate, Integer page, Integer size) {}
