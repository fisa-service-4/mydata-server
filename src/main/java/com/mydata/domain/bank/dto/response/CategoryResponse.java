package com.mydata.domain.bank.dto.response;

import lombok.Builder;

@Builder
public record CategoryResponse(String category, Long amount) {}
