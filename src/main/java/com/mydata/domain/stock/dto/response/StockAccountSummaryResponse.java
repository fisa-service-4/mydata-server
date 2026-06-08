package com.mydata.domain.stock.dto.response;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record StockAccountSummaryResponse(
    Long accountId,
    String accountNumber,
    String accountName,
    String bankCode,
    BigDecimal cashBalance) {}
