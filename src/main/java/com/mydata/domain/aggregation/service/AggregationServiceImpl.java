package com.mydata.domain.aggregation.service;

import com.mydata.domain.aggregation.dto.response.AssetDistributionResponse;
import com.mydata.domain.aggregation.dto.response.DashboardResponse;
import com.mydata.domain.aggregation.dto.response.TotalAssetSummaryResponse;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.service.BankMyDataService;
import com.mydata.domain.stock.dto.response.AssetSummaryResponse;
import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
import com.mydata.domain.stock.exception.StockMyDataException;
import com.mydata.domain.stock.service.StockMyDataService;
import com.mydata.global.exception.ErrorCode;
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
    List<AccountSummaryResponse> bankAccounts = bankMyDataService.getAccounts(firebaseUid);
    long totalBankAssetAmount =
        bankAccounts.stream().mapToLong(AccountSummaryResponse::balance).sum();
    int bankAccountCount = bankAccounts.size();

    // 증권 자산 조회 (미보유 시 0으로 기본값 처리)
    var stockSummary = fetchStockSummaryOrDefault(firebaseUid);
    long totalStockAssetAmount = stockSummary.totalEvaluationAmount();
    Double totalProfitRate = stockSummary.totalProfitRate();

    // 보유 종목 수 집계 (BaaS holdingCount 스펙 미지원으로 인한 N+1 임시 구현)
    int holdingCount = countHoldings(firebaseUid);

    long totalAssetAmount = totalBankAssetAmount + totalStockAssetAmount;

    return DashboardResponse.builder()
        .totalAssetAmount(totalAssetAmount)
        .totalBankAssetAmount(totalBankAssetAmount)
        .totalStockAssetAmount(totalStockAssetAmount)
        .investmentRatio(computeRatio(totalStockAssetAmount, totalAssetAmount))
        .bankAccountCount(bankAccountCount)
        .holdingCount(holdingCount)
        .totalProfitRate(totalProfitRate)
        .build();
  }

  // --- private helpers ---

  /** 은행 전체 계좌 잔액 합산 */
  private long sumBankBalance(String firebaseUid) {
    return bankMyDataService.getAccounts(firebaseUid).stream()
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

  /** 전체 증권 계좌의 보유 종목 수 합산. 계좌/종목이 없는 경우 0 반환. */
  private int countHoldings(String firebaseUid) {
    try {
      List<StockAccountSummaryResponse> stockAccounts = stockMyDataService.getAccounts(firebaseUid);
      int count = 0;
      for (StockAccountSummaryResponse account : stockAccounts) {
        count += stockMyDataService.getHoldings(account.accountId()).size();
      }
      return count;
    } catch (StockMyDataException e) {
      if (ErrorCode.STOCK_ACCOUNT_NOT_FOUND.equals(e.getErrorCode())
          || ErrorCode.STOCK_HOLDING_NOT_FOUND.equals(e.getErrorCode())) {
        return 0;
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
