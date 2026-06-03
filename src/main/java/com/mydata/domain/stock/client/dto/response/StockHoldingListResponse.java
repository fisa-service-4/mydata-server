package com.mydata.domain.stock.client.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockHoldingListResponse {

  private List<StockHoldingResponse> content;
}
