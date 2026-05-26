package com.mydata.domain.mydata.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record SyncResponse(boolean synced, LocalDateTime syncedAt) {}
