package com.mydata.domain.bank.dto.response;

import lombok.Builder;

@Builder
public record BalanceResponse(Long accountId, Long balance, Long availableBalance) {}
