package com.mydata.domain.aggregation.dto.response;

import lombok.Builder;

@Builder
public record TotalAssetSummaryResponse(
    Long totalAssetAmount,
    Long totalBankAssetAmount,
    Long totalStockAssetAmount,
    Double investmentRatio) {}
