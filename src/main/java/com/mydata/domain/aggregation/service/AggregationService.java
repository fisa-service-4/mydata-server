package com.mydata.domain.aggregation.service;

import com.mydata.domain.aggregation.dto.response.AssetDistributionResponse;
import com.mydata.domain.aggregation.dto.response.AssetSummaryResponse;

public interface AggregationService {

  AssetSummaryResponse getAssetSummary(Long userId);

  AssetDistributionResponse getAssetDistribution(Long userId);
}
