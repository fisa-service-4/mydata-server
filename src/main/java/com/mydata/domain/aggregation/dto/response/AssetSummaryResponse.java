package com.mydata.domain.aggregation.dto.response;

import lombok.Builder;

@Builder
public record AssetSummaryResponse(
    Long totalAssetAmount,
    Long totalBankAssetAmount,
    Long totalStockAssetAmount,
    Double investmentRatio) {}
