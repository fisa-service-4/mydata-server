package com.mydata.domain.stock.dto.response;

import lombok.Builder;

@Builder
public record HoldingResponse(
    String stockCode,
    String stockName,
    Long quantity,
    Long averagePrice,
    Double currentPrice,
    Double evaluationAmount,
    Double profitRate) {}
