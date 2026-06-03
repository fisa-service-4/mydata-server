package com.mydata.domain.aggregation.service;

import com.mydata.domain.aggregation.dto.response.AssetDistributionResponse;
import com.mydata.domain.aggregation.dto.response.DashboardResponse;
import com.mydata.domain.aggregation.dto.response.TotalAssetSummaryResponse;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.exception.BankMyDataException;
import com.mydata.domain.bank.service.BankMyDataService;
import com.mydata.domain.stock.dto.response.AssetSummaryResponse;
import com.mydata.domain.stock.dto.response.HoldingResponse;
import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
import com.mydata.domain.stock.exception.StockMyDataException;
import com.mydata.domain.stock.service.StockMyDataService;
import com.mydata.global.exception.ErrorCode;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AggregationServiceImpl implements AggregationService {

  private final BankMyDataService bankMyDataService;
  private final StockMyDataService stockMyDataService;

  @Override
  public TotalAssetSummaryResponse getAssetSummary(String firebaseUid) {
    long totalBankAssetAmount = sumBankBalance(firebaseUid);
    long totalStockAssetAmount = fetchStockEvaluationAmount(firebaseUid);
    long totalAssetAmount = totalBankAssetAmount + totalStockAssetAmount;

    return TotalAssetSummaryResponse.builder()
        .totalAssetAmount(totalAssetAmount)
        .totalBankAssetAmount(totalBankAssetAmount)
        .totalStockAssetAmount(totalStockAssetAmount)
        .investmentRatio(computeRatio(totalStockAssetAmount, totalAssetAmount))
        .build();
  }

  @Override
  public AssetDistributionResponse getAssetDistribution(String firebaseUid) {
    long totalBankAssetAmount = sumBankBalance(firebaseUid);
    long totalStockAssetAmount = fetchStockEvaluationAmount(firebaseUid);
    long totalAssetAmount = totalBankAssetAmount + totalStockAssetAmount;

    return AssetDistributionResponse.builder()
        .totalAssetAmount(totalAssetAmount)
        .totalBankAssetAmount(totalBankAssetAmount)
        .totalStockAssetAmount(totalStockAssetAmount)
        .bankRatio(computeRatio(totalBankAssetAmount, totalAssetAmount))
        .stockRatio(computeRatio(totalStockAssetAmount, totalAssetAmount))
        .build();
  }

  @Override
  public DashboardResponse getDashboard(String firebaseUid) {
    // 은행 계좌 조회 (잔액 합산 + 계좌 수 동시 처리)
    List<AccountSummaryResponse> bankAccounts = fetchBankAccountsOrEmpty(firebaseUid);
    long totalBankAssetAmount =
        bankAccounts.stream().mapToLong(AccountSummaryResponse::balance).sum();
    int bankAccountCount = bankAccounts.size();

    // 증권 보유 종목에서 직접 자산 계산
    StockAssets stockAssets = calcStockAssetsFromHoldings(firebaseUid);

    long totalAssetAmount = totalBankAssetAmount + stockAssets.evaluationAmount();

    return DashboardResponse.builder()
        .totalAssetAmount(totalAssetAmount)
        .totalBankAssetAmount(totalBankAssetAmount)
        .totalStockAssetAmount(stockAssets.evaluationAmount())
        .investmentRatio(computeRatio(stockAssets.evaluationAmount(), totalAssetAmount))
        .bankAccountCount(bankAccountCount)
        .holdingCount(stockAssets.holdingCount())
        .totalProfitRate(stockAssets.profitRate())
        .build();
  }

  // --- private helpers ---

  /** 은행 계좌 목록 조회. 계좌가 없거나 조회 실패 시 빈 리스트 반환. */
  private List<AccountSummaryResponse> fetchBankAccountsOrEmpty(String firebaseUid) {
    try {
      return bankMyDataService.getAccounts(firebaseUid);
    } catch (BankMyDataException e) {
      if (ErrorCode.ACCOUNT_001.equals(e.getErrorCode())) {
        return Collections.emptyList();
      }
      throw e;
    }
  }

  /** 은행 전체 계좌 잔액 합산 */
  private long sumBankBalance(String firebaseUid) {
    return fetchBankAccountsOrEmpty(firebaseUid).stream()
        .mapToLong(AccountSummaryResponse::balance)
        .sum();
  }

  /** 증권 평가 금액 조회 (미보유 시 0 반환) */
  private long fetchStockEvaluationAmount(String firebaseUid) {
    return fetchStockSummaryOrDefault(firebaseUid).totalEvaluationAmount();
  }

  /** 증권 자산 요약 조회. 증권 계좌/자산이 없는 경우 0으로 채운 기본값을 반환하여 집계 API가 실패하지 않도록 처리. */
  private AssetSummaryResponse fetchStockSummaryOrDefault(String firebaseUid) {
    try {
      return stockMyDataService.getAssetSummary(firebaseUid);
    } catch (StockMyDataException e) {
      if (ErrorCode.STOCK_ASSET_SUMMARY_NOT_FOUND.equals(e.getErrorCode())) {
        return AssetSummaryResponse.builder()
            .totalAssetAmount(0L)
            .totalPurchaseAmount(0L)
            .totalEvaluationAmount(0L)
            .totalProfitAmount(0L)
            .totalProfitRate(0.0)
            .build();
      }
      throw e;
    }
  }

  private record StockAssets(long evaluationAmount, int holdingCount, Double profitRate) {}

  /** 보유 종목에서 직접 증권 평가금액, 보유 종목 수, 수익률을 계산. */
  private StockAssets calcStockAssetsFromHoldings(String firebaseUid) {
    try {
      List<StockAccountSummaryResponse> stockAccounts = stockMyDataService.getAccounts(firebaseUid);
      long totalEvaluation = 0;
      long totalPurchase = 0;
      int count = 0;
      for (StockAccountSummaryResponse account : stockAccounts) {
        List<HoldingResponse> holdings = stockMyDataService.getHoldings(account.accountId());
        for (HoldingResponse h : holdings) {
          totalEvaluation += h.evaluationAmount() != null ? h.evaluationAmount() : 0L;
          long purchase =
              (h.averagePrice() != null && h.quantity() != null)
                  ? h.averagePrice() * h.quantity()
                  : 0L;
          totalPurchase += purchase;
          count++;
        }
      }
      Double profitRate =
          totalPurchase > 0
              ? Math.round(((double) (totalEvaluation - totalPurchase) / totalPurchase) * 100 * 100)
                  / 100.0
              : 0.0;
      return new StockAssets(totalEvaluation, count, profitRate);
    } catch (StockMyDataException e) {
      if (ErrorCode.STOCK_ACCOUNT_NOT_FOUND.equals(e.getErrorCode())
          || ErrorCode.STOCK_HOLDING_NOT_FOUND.equals(e.getErrorCode())) {
        return new StockAssets(0L, 0, 0.0);
      }
      throw e;
    }
  }

  /**
   * part / total * 100 을 소수점 둘째 자리로 반올림.
   *
   * @param part 분자 (부분 금액)
   * @param total 분모 (전체 금액)
   * @return 비율 (%), total &lt;= 0 이면 0.0 반환
   */
  private static double computeRatio(long part, long total) {
    if (total <= 0) {
      return 0.0;
    }
    return Math.round(((double) part / total) * 100 * 100) / 100.0;
  }
}
