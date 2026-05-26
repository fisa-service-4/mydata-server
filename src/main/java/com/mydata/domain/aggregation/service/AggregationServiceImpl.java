package com.mydata.domain.aggregation.service;

import com.mydata.domain.aggregation.dto.response.AssetSummaryResponse;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.service.BankMyDataService;
import com.mydata.domain.stock.service.StockMyDataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AggregationServiceImpl implements AggregationService {

  private final BankMyDataService bankMyDataService;

  private final StockMyDataService stockMyDataService;

  @Override
  public AssetSummaryResponse getAssetSummary(Long userId) {

    List<AccountSummaryResponse> accounts = bankMyDataService.getAccounts(userId);

    Long totalBankAssetAmount = accounts.stream().mapToLong(AccountSummaryResponse::balance).sum();

    com.mydata.domain.stock.dto.response.AssetSummaryResponse stockSummary =
        stockMyDataService.getAssetSummary(userId);

    Long totalStockAssetAmount = stockSummary.totalEvaluationAmount();

    Long totalAssetAmount = totalBankAssetAmount + totalStockAssetAmount;

    Double investmentRatio = 0.0;

    if (totalAssetAmount > 0) {

      investmentRatio =
          (totalStockAssetAmount.doubleValue() / totalAssetAmount.doubleValue()) * 100;
    }

    return AssetSummaryResponse.builder()
        .totalAssetAmount(totalAssetAmount)
        .totalBankAssetAmount(totalBankAssetAmount)
        .totalStockAssetAmount(totalStockAssetAmount)
        .investmentRatio(Math.round(investmentRatio * 100) / 100.0)
        .build();
  }
}
