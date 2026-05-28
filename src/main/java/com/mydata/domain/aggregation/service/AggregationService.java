package com.mydata.domain.aggregation.service;

import com.mydata.domain.aggregation.dto.response.AssetDistributionResponse;
import com.mydata.domain.aggregation.dto.response.DashboardResponse;
import com.mydata.domain.aggregation.dto.response.TotalAssetSummaryResponse;

public interface AggregationService {

  TotalAssetSummaryResponse getAssetSummary();

  AssetDistributionResponse getAssetDistribution();

  DashboardResponse getDashboard();
}
