package com.mydata.domain.stock.client.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockAccountListResponse {

  private List<StockAccountResponse> content;
}
