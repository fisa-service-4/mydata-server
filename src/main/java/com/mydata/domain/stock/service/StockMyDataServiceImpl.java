package com.mydata.domain.stock.service;

import com.mydata.domain.stock.client.dto.response.*;
import com.mydata.domain.stock.client.internal.StockInternalClient;
import com.mydata.domain.stock.dto.response.*;
import com.mydata.domain.stock.exception.StockMyDataException;
import com.mydata.domain.stock.mapper.StockMyDataMapper;
import com.mydata.global.exception.ErrorCode;
import com.mydata.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockMyDataServiceImpl implements StockMyDataService {

  private final StockInternalClient stockInternalClient;

  private final StockMyDataMapper stockMyDataMapper;

  @Override
  public List<StockAccountSummaryResponse> getAccounts(Long userId) {

    ApiResponse<List<StockAccountResponse>> response = stockInternalClient.getAccounts(userId);

    if (response == null || response.data() == null) {
      throw new StockMyDataException(ErrorCode.STOCK_ACCOUNT_NOT_FOUND);
    }

    return stockMyDataMapper.toStockAccountSummaryList(response.data());
  }

  @Override
  public List<HoldingResponse> getHoldings(Long userId, Long accountId) {

    ApiResponse<List<StockHoldingResponse>> response =
        stockInternalClient.getHoldings(userId, accountId);

    if (response == null || response.data() == null) {
      throw new StockMyDataException(ErrorCode.STOCK_HOLDING_NOT_FOUND);
    }

    return stockMyDataMapper.toHoldingResponseList(response.data());
  }

  @Override
  public PortfolioResponse getPortfolio(Long userId, Long accountId) {

    ApiResponse<StockPortfolioResponse> response =
        stockInternalClient.getPortfolio(userId, accountId);

    if (response == null || response.data() == null) {
      throw new StockMyDataException(ErrorCode.STOCK_PORTFOLIO_NOT_FOUND);
    }

    return stockMyDataMapper.toPortfolioResponse(response.data());
  }

  @Override
  public ReturnResponse getReturns(Long userId, Long accountId) {

    ApiResponse<StockReturnResponse> response = stockInternalClient.getReturns(userId, accountId);

    if (response == null || response.data() == null) {
      throw new StockMyDataException(ErrorCode.STOCK_RETURN_NOT_FOUND);
    }

    return stockMyDataMapper.toReturnResponse(response.data());
  }

  @Override
  public AssetSummaryResponse getAssetSummary(Long userId) {

    ApiResponse<StockAssetSummaryResponse> response = stockInternalClient.getAssetSummary(userId);

    if (response == null || response.data() == null) {
      throw new StockMyDataException(ErrorCode.STOCK_ASSET_SUMMARY_NOT_FOUND);
    }

    return stockMyDataMapper.toAssetSummaryResponse(response.data());
  }
}
