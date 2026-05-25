package com.mydata.domain.stock.mapper;

import com.mydata.domain.stock.client.dto.response.StockAccountResponse;
import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockMyDataMapper {

  StockAccountSummaryResponse toStockAccountSummary(StockAccountResponse response);

  List<StockAccountSummaryResponse> toStockAccountSummaryList(List<StockAccountResponse> responses);
}
