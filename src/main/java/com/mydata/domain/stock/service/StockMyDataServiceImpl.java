package com.mydata.domain.stock.service;

import com.mydata.domain.stock.client.internal.StockInternalClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockMyDataServiceImpl implements StockMyDataService {

  private final StockInternalClient stockInternalClient;
}
