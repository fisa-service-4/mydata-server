package com.mydata.domain.aggregation.service;

import com.mydata.domain.aggregation.dto.response.AssetDistributionResponse;
import com.mydata.domain.aggregation.dto.response.AssetSummaryResponse;
import com.mydata.domain.aggregation.dto.response.DashboardResponse;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.service.BankMyDataService;
import com.mydata.domain.stock.dto.response.HoldingResponse;
import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
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

  @Override
  public AssetDistributionResponse getAssetDistribution(Long userId) {

    List<AccountSummaryResponse> accounts = bankMyDataService.getAccounts(userId);

    Long totalBankAssetAmount = accounts.stream().mapToLong(AccountSummaryResponse::balance).sum();

    com.mydata.domain.stock.dto.response.AssetSummaryResponse stockSummary =
        stockMyDataService.getAssetSummary(userId);

    Long totalStockAssetAmount = stockSummary.totalEvaluationAmount();

    Long totalAssetAmount = totalBankAssetAmount + totalStockAssetAmount;

    Double bankRatio = 0.0;
    Double stockRatio = 0.0;

    if (totalAssetAmount > 0) {

      bankRatio = (totalBankAssetAmount.doubleValue() / totalAssetAmount.doubleValue()) * 100;

      stockRatio = (totalStockAssetAmount.doubleValue() / totalAssetAmount.doubleValue()) * 100;
    }

    return AssetDistributionResponse.builder()
        .totalAssetAmount(totalAssetAmount)
        .totalBankAssetAmount(totalBankAssetAmount)
        .totalStockAssetAmount(totalStockAssetAmount)
        .bankRatio(Math.round(bankRatio * 100) / 100.0)
        .stockRatio(Math.round(stockRatio * 100) / 100.0)
        .build();
  }

  @Override
  public DashboardResponse getDashboard(Long userId) {

    // 은행 계좌 조회
    List<AccountSummaryResponse> accounts = bankMyDataService.getAccounts(userId);

    Long totalBankAssetAmount = accounts.stream().mapToLong(AccountSummaryResponse::balance).sum();

    Integer bankAccountCount = accounts.size();

    // 주식 자산 조회
    com.mydata.domain.stock.dto.response.AssetSummaryResponse stockSummary =
        stockMyDataService.getAssetSummary(userId);

    Long totalStockAssetAmount = stockSummary.totalEvaluationAmount();

    Double totalProfitRate = stockSummary.totalProfitRate();

    // 보유 종목 수 계산
    Integer holdingCount = 0;

    List<StockAccountSummaryResponse> stockAccounts = stockMyDataService.getAccounts(userId);

    for (StockAccountSummaryResponse account : stockAccounts) {

      List<HoldingResponse> holdings = stockMyDataService.getHoldings(userId, account.accountId());

      holdingCount += holdings.size();
    }

    // 총 자산 계산
    Long totalAssetAmount = totalBankAssetAmount + totalStockAssetAmount;

    // 투자 비율 계산
    Double investmentRatio = 0.0;

    if (totalAssetAmount > 0) {

      investmentRatio =
          (totalStockAssetAmount.doubleValue() / totalAssetAmount.doubleValue()) * 100;
    }

    return DashboardResponse.builder()
        .totalAssetAmount(totalAssetAmount)
        .totalBankAssetAmount(totalBankAssetAmount)
        .totalStockAssetAmount(totalStockAssetAmount)
        .investmentRatio(Math.round(investmentRatio * 100) / 100.0)
        .bankAccountCount(bankAccountCount)
        .holdingCount(holdingCount)
        .totalProfitRate(totalProfitRate)
        .build();
  }
}
