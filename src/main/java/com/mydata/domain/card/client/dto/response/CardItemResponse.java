package com.mydata.domain.card.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CardItemResponse {

  private Long cardId;

  private String cardNumber;

  private Long linkedAccountId;

  private String cardStatus;
}
