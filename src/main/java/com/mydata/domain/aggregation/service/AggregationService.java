package com.mydata.domain.aggregation.service;

import com.mydata.domain.aggregation.dto.response.AssetDistributionResponse;
import com.mydata.domain.aggregation.dto.response.DashboardResponse;
import com.mydata.domain.aggregation.dto.response.TotalAssetSummaryResponse;

public interface AggregationService {

  TotalAssetSummaryResponse getAssetSummary(Long userId);

  AssetDistributionResponse getAssetDistribution(Long userId);

  DashboardResponse getDashboard(Long userId);
}
