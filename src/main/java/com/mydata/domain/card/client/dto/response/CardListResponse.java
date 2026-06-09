package com.mydata.domain.card.client.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CardListResponse {

  private List<CardItemResponse> content;
}
