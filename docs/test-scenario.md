# 테스트 시나리오

## 개요

마이데이터 서버 테스트 코드 작성 결과 문서.
총 **98개 테스트, 0 실패, 100% 성공**.

---

## 우선순위 1 — AggregationService (14개)

### AggregationServiceTest

#### GetAssetSummary (5개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 은행 계좌 2개, 증권 보유종목 있음 → 정상 집계 | totalAssetAmount, totalBankAssetAmount, totalStockAssetAmount, investmentRatio 값 검증 |
| `success_onlyBankAccounts` | 증권 계좌 없음 → 은행 자산만 집계 | stockAssetAmount=0, investmentRatio=0 검증 |
| `success_onlyStockAccounts` | 은행 계좌 없음 → 증권 자산만 집계 | bankAssetAmount=0 검증 |
| `error_bankException` | 은행 서비스 예외 발생 → AggregationException(BANK_INTERNAL_API_ERROR) 전파 | 예외 타입 및 에러 코드 검증 |
| `error_stockException` | 증권 서비스 예외 발생 → AggregationException(STOCK_INTERNAL_API_ERROR) 전파 | 예외 타입 및 에러 코드 검증 |

#### GetAssetDistribution (4개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 은행+증권 자산 있음 → 비율 계산 | bankRatio, stockRatio 백분율 값 검증 |
| `success_zeroTotalAsset` | 자산 합계 0 → 비율 0 반환 | bankRatio=0, stockRatio=0 검증 |
| `error_bankException` | 은행 서비스 예외 발생 → AggregationException 전파 | 예외 에러 코드 검증 |
| `error_stockException` | 증권 서비스 예외 발생 → AggregationException 전파 | 예외 에러 코드 검증 |

#### GetDashboard (5개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 은행 계좌 2개, 증권 보유종목 3개 → 전체 대시보드 집계 | bankAccountCount, holdingCount, investmentRatio, totalProfitRate 검증 |
| `success_noStockAsset` | 증권 자산 없음 → holdingCount=0, investmentRatio=0 | 기본값 처리 검증 |
| `success_emptyBankAccounts` | 은행 계좌 없음 → bankAccountCount=0 | 빈 리스트 처리 검증 |
| `error_bankException` | 은행 서비스 예외 발생 → AggregationException 전파 | 예외 에러 코드 검증 |
| `error_stockException` | 증권 서비스 예외 발생 → AggregationException 전파 | 예외 에러 코드 검증 |

---

## 우선순위 2 — BankMyDataService (21개)

### BankMyDataServiceTest

> `@BeforeEach`에서 `BankMyDataServiceImpl`을 `Runnable::run` executor로 직접 생성
> (비동기 작업을 동기 실행으로 변환하여 테스트 예측 가능성 확보)

#### GetAccounts (5개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 은행 계좌 1개 응답 → 매핑 후 반환 | result 크기 1, accountId 검증 |
| `success_nullContent` | content가 null인 응답 → 빈 리스트 반환 | isEmpty 검증 |
| `error_notFound` | FeignException.NotFound → BankMyDataException(ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_feignException` | FeignException → BankMyDataException(BANK_INTERNAL_API_ERROR) | 에러 코드 satisfies 패턴 |
| `error_nullResponse` | null 응답 → BankMyDataException(BANK_INTERNAL_API_ERROR) | 에러 코드 satisfies 패턴 |

#### GetAccountDetail (4개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 계좌 상세 응답 정상 → 매핑 결과 반환 | 매핑된 객체 동일성 검증 |
| `error_nullResponse` | null 응답 → BankMyDataException(ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_notFound` | FeignException.NotFound → BankMyDataException(ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_feignException` | FeignException → BankMyDataException(BANK_INTERNAL_API_ERROR) | 에러 코드 satisfies 패턴 |

#### GetBalance (4개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 잔액 응답 정상 → 매핑 결과 반환 | 매핑된 객체 동일성 검증 |
| `error_nullResponse` | null 응답 → BankMyDataException(ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_notFound` | FeignException.NotFound → BankMyDataException(ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_feignException` | FeignException → BankMyDataException(BANK_INTERNAL_API_ERROR) | 에러 코드 satisfies 패턴 |

#### GetTransactions (4개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success_withCardApproval` | 거래내역 + APPROVED 카드 승인 매핑 → merchantName/merchantCategory/maskedCardNumber 포함 반환 | transactionId, merchantName, merchantCategory, maskedCardNumber 검증 |
| `success_noCards` | 카드 없음 → 카드 필드 null인 거래내역 반환 | merchantName=null 검증 |
| `success_nonApprovedSkipped` | APPROVED가 아닌 승인내역(DECLINED 등) → 카드 필드 매핑 제외 | merchantName=null 검증 |
| `error_bankTransactionNotFound` | 은행 거래내역 FeignException.NotFound → BankMyDataException(ACCOUNT_001) | 에러 코드 satisfies 패턴 |

#### GetTransactionCategories (4개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success_withApprovals` | APPROVED 카드 승인 2건(같은 카테고리) → 카테고리별 금액 합산 | result 크기 1, category="CAFE", amount=10000 검증 |
| `success_noCards` | 카드 없음 → 빈 리스트 반환 | isEmpty 검증 |
| `success_nullCategorySkipped` | merchantCategory=null인 승인내역 → 집계 제외 | isEmpty 검증 |
| `success_cardFeignException` | 카드 목록 조회 Feign 실패 → 빈 리스트 반환 (graceful degradation) | isEmpty 검증 |

> **핵심 주의사항**: `CardItemResponse.getCardNumber()`를 반드시 stub해야 함.
> `ConcurrentHashMap`은 null value를 허용하지 않으므로, stub 누락 시 CompletionException(NullPointerException) 발생.

---

## 우선순위 3 — SyncService / MyDataService (12개)

### MyDataServiceTest

#### Connect (4개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success_bothLinked` | 은행+증권 모두 연동 → connected=true, bankLinked=true, stockLinked=true | ConnectResponse 필드 검증 |
| `success_onlyBankLinked` | 은행만 연동 → bankLinked=true, stockLinked=false | 부분 연동 처리 검증 |
| `success_onlyStockLinked` | 증권만 연동 → bankLinked=false, stockLinked=true | 부분 연동 처리 검증 |
| `error_alreadyConnected` | 이미 연동된 기관 → MyDataException(MYDATA_001) | 에러 코드 검증 |

#### GetConnections (4개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 은행+증권 계좌 모두 연동 → 목록 반환 | bankAccounts, stockAccounts 크기 검증 |
| `success_onlyBank` | 은행만 연동 → stockAccounts 빈 리스트 | 부분 연동 목록 검증 |
| `success_noConnections` | 연동 없음 → 둘 다 빈 리스트 | 미연동 상태 처리 검증 |
| `error_notConnected` | 연동 정보 없음 → MyDataException(MYDATA_002) | 에러 코드 검증 |

#### Sync (4개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 정상 동기화 → synced=true, syncedAt 반환 | SyncResponse 필드 검증 |
| `success_noLinkedAccounts` | 연동 계좌 없음 → synced=true (빈 동기화) | 예외 없이 처리 검증 |
| `error_syncFailed` | 동기화 실패 → MyDataException 전파 | 예외 타입 검증 |
| `error_notConnected` | 연동 정보 없음 → MyDataException(MYDATA_002) | 에러 코드 검증 |

---

## 우선순위 4 — StockMyDataService (21개)

### StockMyDataServiceTest

#### GetAccounts (5개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 증권 계좌 1개 응답 → 매핑 후 반환 | result 크기 1, accountId 검증 |
| `success_emptyContent` | content 빈 리스트 → 빈 리스트 반환 | isEmpty 검증 |
| `error_notFound` | FeignException.NotFound → StockMyDataException(ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_feignException` | FeignException → StockMyDataException(STOCK_INTERNAL_API_ERROR) | 에러 코드 satisfies 패턴 |
| `error_nullResponse` | null 응답 → StockMyDataException(STOCK_INTERNAL_API_ERROR) | 에러 코드 satisfies 패턴 |

#### GetHoldings (4개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 보유종목 1개 → 매핑 결과 반환 | stockCode, profitRate 검증 |
| `success_emptyContent` | 보유종목 없음 → 빈 리스트 반환 | isEmpty 검증 |
| `error_notFound` | FeignException.NotFound → StockMyDataException(ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_feignException` | FeignException → StockMyDataException(STOCK_INTERNAL_API_ERROR) | 에러 코드 satisfies 패턴 |

#### GetPortfolio (4개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 포트폴리오 정상 응답 → 매핑 결과 반환 | totalEvaluationAmount 등 검증 |
| `error_nullResponse` | null 응답 → StockMyDataException(ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_notFound` | FeignException.NotFound → StockMyDataException(ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_feignException` | FeignException → StockMyDataException(STOCK_INTERNAL_API_ERROR) | 에러 코드 satisfies 패턴 |

#### GetReturns (4개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 수익률 정상 응답 → 매핑 결과 반환 | totalProfitRate 검증 |
| `error_nullResponse` | null 응답 → StockMyDataException(ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_notFound` | FeignException.NotFound → StockMyDataException(ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_feignException` | FeignException → StockMyDataException(STOCK_INTERNAL_API_ERROR) | 에러 코드 satisfies 패턴 |

#### GetAssetSummary (4개)

| 테스트명 | 시나리오 | 검증 내용 |
|---|---|---|
| `success` | 전체 자산 요약 정상 응답 → 매핑 결과 반환 | totalAssetAmount 등 검증 |
| `error_nullResponse` | null 응답 → StockMyDataException(STOCK_ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_notFound` | FeignException.NotFound → StockMyDataException(STOCK_ACCOUNT_001) | 에러 코드 satisfies 패턴 |
| `error_feignException` | FeignException → StockMyDataException(STOCK_INTERNAL_API_ERROR) | 에러 코드 satisfies 패턴 |

---

## 컨트롤러 테스트 (29개)

### AggregationControllerTest (7개)

| 클래스 | 테스트명 | 시나리오 |
|---|---|---|
| GetAssetSummary | `success` | `X-Firebase-Uid` 헤더 포함 → 200 + data 검증 |
| GetAssetSummary | `missingHeader_returns400` | 헤더 누락 → 400 + VALID_001 |
| GetAssetDistribution | `success` | `X-Firebase-Uid` 헤더 포함 → 200 + bankRatio/stockRatio 검증 |
| GetAssetDistribution | `missingHeader_returns400` | 헤더 누락 → 400 + VALID_001 |
| GetDashboard | `success` | `X-Firebase-Uid` 헤더 포함 → 200 + 전체 필드 검증 |
| GetDashboard | `success_noStockAsset` | 증권 없는 사용자 → investmentRatio=0, holdingCount=0 |
| GetDashboard | `missingHeader_returns400` | 헤더 누락 → 400 + VALID_001 |

### BankMyDataControllerTest (7개)

| 클래스 | 테스트명 | 시나리오 |
|---|---|---|
| GetAccounts | `success` | `X-Firebase-Uid` 헤더 포함 → 200 + 계좌 목록 검증 |
| GetAccounts | `missingHeader_returns400` | 헤더 누락 → 400 + VALID_001 |
| GetAccountDetail | `success` | path variable만으로 → 200 + 상세 검증 |
| GetBalance | `success` | path variable만으로 → 200 + 잔액 검증 |
| GetTransactions | `success` | fromDate/toDate 파라미터 포함 → 200 + 거래내역 검증 |
| GetTransactions | `invalidDateFormat_returns400` | fromDate 형식 오류(yyyyMMdd) → 400 |
| GetTransactionCategories | `success` | path variable만으로 → 200 + 카테고리 목록 검증 |

> `getAccounts`만 `@RequestHeader(X-Firebase-Uid)` 적용. 나머지 엔드포인트는 `accountId` path variable로 특정하므로 헤더 불필요.

### StockMyDataControllerTest (7개)

| 클래스 | 테스트명 | 시나리오 |
|---|---|---|
| GetAccounts | `success` | `X-Firebase-Uid` 헤더 포함 → 200 + 증권 계좌 검증 |
| GetAccounts | `missingHeader_returns400` | 헤더 누락 → 400 + VALID_001 |
| GetHoldings | `success` | path variable만으로 → 200 + 보유종목 검증 |
| GetPortfolio | `success` | path variable만으로 → 200 + 포트폴리오 검증 |
| GetReturns | `success` | path variable만으로 → 200 + 수익률 검증 |
| GetAssetSummary | `success` | `X-Firebase-Uid` 헤더 포함 → 200 + 자산 요약 검증 |
| GetAssetSummary | `missingHeader_returns400` | 헤더 누락 → 400 + VALID_001 |

> `getAccounts`, `getAssetSummary`만 `@RequestHeader(X-Firebase-Uid)` 적용.

### MyDataControllerTest (8개)

| 클래스 | 테스트명 | 시나리오 |
|---|---|---|
| Connect | `success` | `X-Firebase-Uid` 헤더 + provider body → 200 + connected=true |
| Connect | `missingHeader_returns400` | 헤더 누락 → 400 + VALID_001 |
| Connect | `missingProvider_returns400` | provider="" (빈 값) → @Valid 검증 실패 → 400 |
| GetConnections | `success` | `X-Firebase-Uid` 헤더 포함 → 200 + 계좌 목록 검증 |
| GetConnections | `success_emptyAccounts` | 연동 없음 → 빈 배열 반환 |
| GetConnections | `missingHeader_returns400` | 헤더 누락 → 400 + VALID_001 |
| Sync | `success` | `X-Firebase-Uid` 헤더 포함 → 200 + synced=true |
| Sync | `missingHeader_returns400` | 헤더 누락 → 400 + VALID_001 |

---

## 공통 패턴 및 주의사항

### 에러 코드 검증 패턴

`BusinessException.getMessage()`는 Korean 메시지를 반환하므로 `.hasMessageContaining(errorCode.getCode())`는 항상 실패한다.
대신 아래 패턴을 사용:

```java
assertThatThrownBy(() -> service.method(...))
    .isInstanceOf(XxxException.class)
    .satisfies(ex ->
        assertThat(((XxxException) ex).getErrorCode())
            .isEqualTo(ErrorCode.XXX));
```

### FeignException Mock 방식

```java
// getStackTrace()가 null을 반환하는 mock은 NPE 유발 — 반드시 mock() 사용
given(client.method()).willThrow(mock(FeignException.NotFound.class));
```

### BankMyDataServiceImpl 생성자 주입

`Executor` 파라미터를 `Runnable::run`으로 주입하면 `CompletableFuture.runAsync()`가 동기 실행되어 비동기 흐름을 테스트할 수 있다:

```java
bankMyDataService = new BankMyDataServiceImpl(
    bankInternalClient, cardInternalClient, bankMyDataMapper, Runnable::run);
```
