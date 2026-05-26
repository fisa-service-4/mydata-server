package com.mydata.domain.mydata.dto.response;

import lombok.Builder;

@Builder
public record ConnectResponse(boolean connected, boolean bankLinked, boolean stockLinked) {}
