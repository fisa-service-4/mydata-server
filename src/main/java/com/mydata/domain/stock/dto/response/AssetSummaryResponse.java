package com.mydata.domain.stock.dto.response;

import lombok.Builder;

@Builder
public record AssetSummaryResponse(
    Long totalAssetAmount,
    Long totalPurchaseAmount,
    Long totalEvaluationAmount,
    Long totalProfitAmount,
    Double totalProfitRate) {}
