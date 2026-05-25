package com.mydata.domain.stock.dto.response;

import lombok.Builder;

@Builder
public record ReturnResponse(
    Long totalPurchaseAmount,
    Long totalEvaluationAmount,
    Long totalProfitAmount,
    Double totalProfitRate) {}
