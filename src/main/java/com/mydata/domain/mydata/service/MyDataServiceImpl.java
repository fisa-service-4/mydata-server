package com.mydata.domain.mydata.service;

import com.mydata.domain.aggregation.service.AggregationService;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.service.BankMyDataService;
import com.mydata.domain.mydata.dto.request.ConnectRequest;
import com.mydata.domain.mydata.dto.response.ConnectResponse;
import com.mydata.domain.mydata.dto.response.ConnectionResponse;
import com.mydata.domain.mydata.dto.response.SyncResponse;
import com.mydata.domain.stock.dto.response.StockAccountSummaryResponse;
import com.mydata.domain.stock.service.StockMyDataService;
import com.mydata.global.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyDataServiceImpl implements MyDataService {

  private final BankMyDataService bankMyDataService;
  private final StockMyDataService stockMyDataService;
  private final AggregationService aggregationService;

  @Override
  public ConnectResponse connect(ConnectRequest request) {

    boolean bankLinked = false;
    boolean stockLinked = false;

    try {
      bankMyDataService.getAccounts();
      bankLinked = true;
    } catch (BusinessException e) {
      log.debug("은행 계좌 연동 확인 실패: {}", e.getMessage());
    }

    try {
      stockMyDataService.getAccounts();
      stockLinked = true;
    } catch (BusinessException e) {
      log.debug("증권 계좌 연동 확인 실패: {}", e.getMessage());
    }

    return ConnectResponse.builder()
        .connected(bankLinked || stockLinked)
        .bankLinked(bankLinked)
        .stockLinked(stockLinked)
        .build();
  }

  @Override
  public ConnectionResponse getConnections() {

    List<AccountSummaryResponse> bankAccounts;
    List<StockAccountSummaryResponse> stockAccounts;

    try {
      bankAccounts = bankMyDataService.getAccounts();
    } catch (BusinessException e) {
      log.warn("은행 계좌 목록 조회 실패: {}", e.getMessage());
      bankAccounts = Collections.emptyList();
    }

    try {
      stockAccounts = stockMyDataService.getAccounts();
    } catch (BusinessException e) {
      log.warn("증권 계좌 목록 조회 실패: {}", e.getMessage());
      stockAccounts = Collections.emptyList();
    }

    return ConnectionResponse.builder()
        .bankAccounts(bankAccounts)
        .stockAccounts(stockAccounts)
        .build();
  }

  @Override
  public SyncResponse sync() {

    try {
      bankMyDataService.getAccounts();
    } catch (BusinessException e) {
      log.warn("동기화 중 은행 계좌 조회 실패: {}", e.getMessage());
    }

    try {
      stockMyDataService.getAccounts();
    } catch (BusinessException e) {
      log.warn("동기화 중 증권 계좌 조회 실패: {}", e.getMessage());
    }

    try {
      aggregationService.getAssetSummary();
    } catch (BusinessException e) {
      log.warn("동기화 중 자산 요약 조회 실패: {}", e.getMessage());
    }

    return SyncResponse.builder().synced(true).syncedAt(LocalDateTime.now()).build();
  }
}
