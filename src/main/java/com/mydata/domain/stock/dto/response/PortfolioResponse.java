package com.mydata.domain.stock.dto.response;

import lombok.Builder;

@Builder
public record PortfolioResponse(
    Long totalEvaluationAmount,
    Long totalPurchaseAmount,
    Long totalProfitAmount,
    Double totalProfitRate) {}
