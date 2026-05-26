package com.mydata.domain.aggregation.dto.response;

import lombok.Builder;

@Builder
public record AssetDistributionResponse(
    Long totalAssetAmount,
    Long totalBankAssetAmount,
    Long totalStockAssetAmount,
    Double bankRatio,
    Double stockRatio) {}
