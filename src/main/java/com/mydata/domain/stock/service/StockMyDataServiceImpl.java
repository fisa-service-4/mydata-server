package com.mydata.domain.stock.service;

import com.mydata.domain.stock.client.dto.response.*;
import com.mydata.domain.stock.client.internal.StockInternalClient;
import com.mydata.domain.stock.dto.response.*;
import com.mydata.domain.stock.exception.StockMyDataException;
import com.mydata.domain.stock.mapper.StockMyDataMapper;
import com.mydata.global.exception.ErrorCode;
import com.mydata.global.response.ApiResponse;
import feign.FeignException;
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
    try {
      ApiResponse<List<StockAccountResponse>> response = stockInternalClient.getAccounts(userId);

      if (response == null || response.data() == null) {
        throw new StockMyDataException(ErrorCode.STOCK_ACCOUNT_NOT_FOUND);
      }

      return stockMyDataMapper.toStockAccountSummaryList(response.data());

    } catch (FeignException.NotFound e) {
      throw new StockMyDataException(ErrorCode.STOCK_ACCOUNT_NOT_FOUND);
    } catch (FeignException e) {
      throw new StockMyDataException(ErrorCode.MYDATA_003);
    }
  }

  @Override
  public List<HoldingResponse> getHoldings(Long userId, Long accountId) {
    try {
      ApiResponse<List<StockHoldingResponse>> response =
          stockInternalClient.getHoldings(userId, accountId);

      if (response == null || response.data() == null) {
        throw new StockMyDataException(ErrorCode.STOCK_HOLDING_NOT_FOUND);
      }

      return stockMyDataMapper.toHoldingResponseList(response.data());

    } catch (FeignException.NotFound e) {
      throw new StockMyDataException(ErrorCode.STOCK_HOLDING_NOT_FOUND);
    } catch (FeignException e) {
      throw new StockMyDataException(ErrorCode.MYDATA_003);
    }
  }

  @Override
  public PortfolioResponse getPortfolio(Long userId, Long accountId) {
    try {
      ApiResponse<StockPortfolioResponse> response =
          stockInternalClient.getPortfolio(userId, accountId);

      if (response == null || response.data() == null) {
        throw new StockMyDataException(ErrorCode.STOCK_PORTFOLIO_NOT_FOUND);
      }

      return stockMyDataMapper.toPortfolioResponse(response.data());

    } catch (FeignException.NotFound e) {
      throw new StockMyDataException(ErrorCode.STOCK_PORTFOLIO_NOT_FOUND);
    } catch (FeignException e) {
      throw new StockMyDataException(ErrorCode.MYDATA_003);
    }
  }

  @Override
  public ReturnResponse getReturns(Long userId, Long accountId) {
    try {
      ApiResponse<StockReturnResponse> response = stockInternalClient.getReturns(userId, accountId);

      if (response == null || response.data() == null) {
        throw new StockMyDataException(ErrorCode.STOCK_RETURN_NOT_FOUND);
      }

      return stockMyDataMapper.toReturnResponse(response.data());

    } catch (FeignException.NotFound e) {
      throw new StockMyDataException(ErrorCode.STOCK_RETURN_NOT_FOUND);
    } catch (FeignException e) {
      throw new StockMyDataException(ErrorCode.MYDATA_003);
    }
  }

  @Override
  public AssetSummaryResponse getAssetSummary(Long userId) {
    try {
      ApiResponse<StockAssetSummaryResponse> response = stockInternalClient.getAssetSummary(userId);

      if (response == null || response.data() == null) {
        throw new StockMyDataException(ErrorCode.STOCK_ASSET_SUMMARY_NOT_FOUND);
      }

      return stockMyDataMapper.toAssetSummaryResponse(response.data());

    } catch (FeignException.NotFound e) {
      throw new StockMyDataException(ErrorCode.STOCK_ASSET_SUMMARY_NOT_FOUND);
    } catch (FeignException e) {
      throw new StockMyDataException(ErrorCode.MYDATA_003);
    }
  }
}
