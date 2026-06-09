package com.mydata.domain.card.client.dto.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CardApprovalItemResponse {

  private Long approvalId;

  private Long accountTransactionId;

  private String merchantName;

  private String merchantCategory;

  private BigDecimal approvalAmount;

  private String approvalStatus;
}
