package com.mydata.domain.aggregation.dto.response;

import lombok.Builder;

@Builder
public record DashboardResponse(
    Long totalAssetAmount,
    Long totalBankAssetAmount,
    Long totalStockAssetAmount,
    Double investmentRatio,
    Integer bankAccountCount,
    Integer holdingCount,
    Double totalProfitRate) {}
