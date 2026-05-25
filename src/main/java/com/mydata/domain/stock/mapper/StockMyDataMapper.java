package com.mydata.domain.stock.mapper;

import com.mydata.domain.stock.client.dto.response.*;
import com.mydata.domain.stock.dto.response.*;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockMyDataMapper {

  StockAccountSummaryResponse toStockAccountSummary(StockAccountResponse response);

  List<StockAccountSummaryResponse> toStockAccountSummaryList(List<StockAccountResponse> responses);

  HoldingResponse toHoldingResponse(StockHoldingResponse response);

  List<HoldingResponse> toHoldingResponseList(List<StockHoldingResponse> responses);

  PortfolioResponse toPortfolioResponse(StockPortfolioResponse response);

  ReturnResponse toReturnResponse(StockReturnResponse response);

  AssetSummaryResponse toAssetSummaryResponse(StockAssetSummaryResponse response);
}
