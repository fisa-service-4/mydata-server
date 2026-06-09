package com.mydata.domain.bank.service;

import com.mydata.domain.bank.client.dto.response.BankAccountDetailResponse;
import com.mydata.domain.bank.client.dto.response.BankAccountListResponse;
import com.mydata.domain.bank.client.dto.response.BankBalanceResponse;
import com.mydata.domain.bank.client.dto.response.BankTransactionResponse;
import com.mydata.domain.bank.client.internal.BankInternalClient;
import com.mydata.domain.bank.dto.request.TransactionSearchRequest;
import com.mydata.domain.bank.dto.response.AccountDetailResponse;
import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.bank.dto.response.BalanceResponse;
import com.mydata.domain.bank.dto.response.CategoryResponse;
import com.mydata.domain.bank.dto.response.TransactionResponse;
import com.mydata.domain.bank.exception.BankMyDataException;
import com.mydata.domain.bank.mapper.BankMyDataMapper;
import com.mydata.domain.card.client.dto.response.CardApprovalItemResponse;
import com.mydata.domain.card.client.dto.response.CardApprovalListResponse;
import com.mydata.domain.card.client.dto.response.CardItemResponse;
import com.mydata.domain.card.client.dto.response.CardListResponse;
import com.mydata.domain.card.client.internal.CardInternalClient;
import com.mydata.global.exception.ErrorCode;
import com.mydata.global.response.ApiResponse;
import feign.FeignException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankMyDataServiceImpl implements BankMyDataService {

  private final BankInternalClient bankInternalClient;
  private final CardInternalClient cardInternalClient;
  private final BankMyDataMapper bankMyDataMapper;

  @Override
  public List<AccountSummaryResponse> getAccounts(String firebaseUid) {
    try {
      ApiResponse<BankAccountListResponse> response = bankInternalClient.getAccounts(firebaseUid);

      if (response == null || response.data() == null) {
        throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR);
      }

      if (response.data().getContent() == null) {
        return Collections.emptyList();
      }

      return bankMyDataMapper.toAccountSummaryList(response.data().getContent());

    } catch (FeignException.NotFound e) {
      throw new BankMyDataException(ErrorCode.ACCOUNT_001, e);
    } catch (FeignException e) {
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR, e);
    }
  }

  @Override
  public AccountDetailResponse getAccountDetail(Long accountId) {
    try {
      ApiResponse<BankAccountDetailResponse> response =
          bankInternalClient.getAccountDetail(accountId);

      if (response == null || response.data() == null) {
        throw new BankMyDataException(ErrorCode.ACCOUNT_001);
      }

      return bankMyDataMapper.toAccountDetail(response.data());

    } catch (FeignException.NotFound e) {
      throw new BankMyDataException(ErrorCode.ACCOUNT_001, e);
    } catch (FeignException e) {
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR, e);
    }
  }

  @Override
  public BalanceResponse getBalance(Long accountId) {
    try {
      ApiResponse<BankBalanceResponse> response = bankInternalClient.getBalance(accountId);

      if (response == null || response.data() == null) {
        throw new BankMyDataException(ErrorCode.ACCOUNT_001);
      }

      return bankMyDataMapper.toBalanceResponse(response.data());

    } catch (FeignException.NotFound e) {
      throw new BankMyDataException(ErrorCode.ACCOUNT_001, e);
    } catch (FeignException e) {
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR, e);
    }
  }

  @Override
  public List<TransactionResponse> getTransactions(
      Long accountId, TransactionSearchRequest request) {

    CompletableFuture<List<BankTransactionResponse>> bankFuture =
        CompletableFuture.supplyAsync(() -> fetchBankTransactions(accountId, request));

    CompletableFuture<CardApprovalData> cardFuture =
        CompletableFuture.supplyAsync(
            () -> buildCardApprovalMap(accountId, request.fromDate(), request.toDate()));

    try {
      List<BankTransactionResponse> bankTxs = bankFuture.get();
      CardApprovalData cardData = cardFuture.get();

      return bankTxs.stream()
          .map(
              tx -> {
                CardApprovalItemResponse approval =
                    cardData.approvalByTxId().get(tx.getTransactionId());
                return TransactionResponse.builder()
                    .transactionId(tx.getTransactionId())
                    .transactionDateTime(tx.getTransactionDateTime())
                    .transactionType(tx.getTransactionType())
                    .amount(tx.getAmount())
                    .balanceAfter(tx.getBalanceAfter())
                    .description(tx.getDescription())
                    .merchantName(approval != null ? approval.getMerchantName() : null)
                    .merchantCategory(approval != null ? approval.getMerchantCategory() : null)
                    .maskedCardNumber(cardData.cardNumberByTxId().get(tx.getTransactionId()))
                    .build();
              })
          .toList();

    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof BankMyDataException ex) throw ex;
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR, e);
    }
  }

  @Override
  public List<CategoryResponse> getTransactionCategories(Long accountId) {
    CardApprovalData cardData = buildCardApprovalMap(accountId, null, null);

    Map<String, Long> categoryTotals = new LinkedHashMap<>();
    for (CardApprovalItemResponse approval : cardData.approvalByTxId().values()) {
      if (approval.getMerchantCategory() == null) {
        continue;
      }
      Long amount = approval.getApprovalAmount() != null ? approval.getApprovalAmount() : 0L;
      categoryTotals.merge(approval.getMerchantCategory(), amount, Long::sum);
    }

    return categoryTotals.entrySet().stream()
        .map(e -> CategoryResponse.builder().category(e.getKey()).amount(e.getValue()).build())
        .toList();
  }

  private List<BankTransactionResponse> fetchBankTransactions(
      Long accountId, TransactionSearchRequest request) {
    try {
      ApiResponse<List<BankTransactionResponse>> response =
          bankInternalClient.getTransactions(
              accountId, request.fromDate(), request.toDate(), request.page(), request.size());

      if (response == null || response.data() == null) {
        throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR);
      }

      return response.data();

    } catch (FeignException.NotFound e) {
      throw new BankMyDataException(ErrorCode.ACCOUNT_001, e);
    } catch (FeignException e) {
      throw new BankMyDataException(ErrorCode.BANK_INTERNAL_API_ERROR, e);
    }
  }

  private CardApprovalData buildCardApprovalMap(Long accountId, String fromDate, String toDate) {

    Map<Long, CardApprovalItemResponse> approvalByTxId = new ConcurrentHashMap<>();
    Map<Long, String> cardNumberByTxId = new ConcurrentHashMap<>();

    List<CardItemResponse> cards;
    try {
      ApiResponse<CardListResponse> cardResponse =
          cardInternalClient.getCardsByAccountId(accountId);
      if (cardResponse == null
          || cardResponse.data() == null
          || cardResponse.data().getContent() == null) {
        return new CardApprovalData(approvalByTxId, cardNumberByTxId);
      }
      cards = cardResponse.data().getContent();
    } catch (FeignException e) {
      log.warn("카드 목록 조회 실패, 카드 정보 없이 처리: {}", e.getMessage());
      return new CardApprovalData(approvalByTxId, cardNumberByTxId);
    }

    List<CompletableFuture<Void>> futures =
        cards.stream()
            .map(
                card ->
                    CompletableFuture.runAsync(
                        () -> {
                          try {
                            ApiResponse<CardApprovalListResponse> approvalResponse =
                                cardInternalClient.getApprovals(
                                    card.getCardId(), fromDate, toDate, 0, 1000);

                            if (approvalResponse == null
                                || approvalResponse.data() == null
                                || approvalResponse.data().getContent() == null) {
                              return;
                            }

                            for (CardApprovalItemResponse approval :
                                approvalResponse.data().getContent()) {
                              if (approval.getAccountTransactionId() == null
                                  || !"APPROVED".equals(approval.getApprovalStatus())) {
                                continue;
                              }
                              approvalByTxId.put(approval.getAccountTransactionId(), approval);
                              cardNumberByTxId.put(
                                  approval.getAccountTransactionId(), card.getCardNumber());
                            }
                          } catch (FeignException e) {
                            log.warn(
                                "카드 승인내역 조회 실패 cardId={}: {}", card.getCardId(), e.getMessage());
                          }
                        }))
            .toList();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    return new CardApprovalData(approvalByTxId, cardNumberByTxId);
  }

  private record CardApprovalData(
      Map<Long, CardApprovalItemResponse> approvalByTxId, Map<Long, String> cardNumberByTxId) {}
}
