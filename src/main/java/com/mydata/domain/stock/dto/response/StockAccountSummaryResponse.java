package com.mydata.domain.stock.dto.response;

import lombok.Builder;

@Builder
public record StockAccountSummaryResponse(
    Long accountId, String accountNumber, String accountName, Long availableCash) {}
