package com.mydata.domain.mydata.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ConnectRequest(@NotBlank String provider) {}
