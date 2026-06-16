# mydata-server

프리랜서 특화 AI 자산관리 플랫폼의 **마이데이터 수집 서버**.  
외부 금융기관(은행·카드·증권)의 데이터를 Transaction Server(BaaS)를 통해 수집하고, 정규화·집계하여 내부 서비스에 제공한다.

---

## 목차

- [서비스 역할](#서비스-역할)
- [기술 스택](#기술-스택)
- [아키텍처 개요](#아키텍처-개요)
- [도메인 구조](#도메인-구조)
- [핵심 구현 포인트](#핵심-구현-포인트)
- [API 목록](#api-목록)
- [설정 및 실행](#설정-및-실행)
- [테스트](#테스트)
- [코드 품질](#코드-품질)

---

## 서비스 역할

```
외부 서비스 (service-backend / AI 서버)
        │  HTTP  /mydata/v1/**
        ▼
  mydata-server  :8084          ← 이 서버
        │  OpenFeign  /baas/v1/**
        ▼
  transaction-server  :8083     (BaaS 게이트웨이)
        ├── /bank/**  →  Bank DB  (Oracle, 온프레미스)
        ├── /card/**  →  Card DB  (온프레미스)
        └── /stock/** →  Stock DB (온프레미스)
```

mydata-server는 transaction-server를 **단일 진입점**으로 삼아 세 금융 원장에 접근한다.  
금융 원장에 직접 접근하지 않으므로 온프레미스 망과의 결합도가 낮고, 응답 모델 변환 및 집계 로직을 이 서버에서 일괄 처리한다.

---

## 기술 스택

| 분류 | 기술 | 버전 |
|------|------|------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.5.14 |
| HTTP Client | Spring Cloud OpenFeign | Spring Cloud 2025.0.0 |
| DTO Mapping | MapStruct | 1.6.3 |
| API 문서 | SpringDoc OpenAPI (Swagger UI) | 2.8.9 |
| Validation | Spring Boot Starter Validation | - |
| Monitoring | Spring Boot Actuator | - |
| Build | Gradle | - |
| Code Style | Spotless (Google Java Format) | 6.25.0 |
| Lombok | Project Lombok | - |
| Test | JUnit 5 + Mockito (BDD) | - |

---

## 아키텍처 개요

### 레이어드 아키텍처

```
Controller  →  Service  →  InternalClient (Feign)
                 │
                 └── Mapper (MapStruct)  →  Response DTO
```

- **Controller**: 입력 유효성 검사, HTTP 헤더 추출, 응답 래핑
- **Service**: 비즈니스 로직, 병렬 조회, 예외 변환
- **InternalClient**: Feign 인터페이스로 transaction-server 호출
- **Mapper**: 클라이언트 응답 DTO → 공개 응답 DTO 변환 (MapStruct)

### 공통 응답 포맷

```json
{
  "success": true,
  "data": { ... },
  "meta": { "traceId": "uuid" }
}
```

모든 응답은 `ApiResponse<T>` 레코드로 래핑된다. 실패 시 `ErrorResponse`로 일관 반환한다.

### Trace ID 전파

`TraceIdFilter` (`OncePerRequestFilter`)가 모든 요청에서:
1. `X-Trace-Id` 요청 헤더를 수신하거나, 없으면 `UUID`를 신규 발급
2. `MDC`에 `traceId` 키로 등록 (로그 패턴에 `[%X{traceId}]` 자동 포함)
3. `X-Trace-Id` 응답 헤더로 반환
4. 요청 처리 완료 후 `MDC.clear()`로 정리

Feign 클라이언트는 `X-Firebase-Uid` / `X-Trace-Id` 헤더를 그대로 transaction-server로 전달한다.

---

## 도메인 구조

```
src/main/java/com/mydata/
├── MydataServerApplication.java
│
├── domain/
│   ├── bank/                    # 은행 계좌·거래내역·카테고리
│   │   ├── client/internal/     # BankInternalClient (Feign)
│   │   ├── controller/          # BankMyDataController
│   │   ├── service/             # BankMyDataServiceImpl
│   │   ├── mapper/              # BankMyDataMapper (MapStruct)
│   │   └── dto/
│   │
│   ├── card/                    # 카드 승인내역 (은행 거래와 조인)
│   │   └── client/internal/     # CardInternalClient (Feign)
│   │
│   ├── stock/                   # 증권 계좌·보유종목·수익률·포트폴리오
│   │   ├── client/internal/     # StockInternalClient (Feign)
│   │   ├── controller/          # StockMyDataController
│   │   ├── service/             # StockMyDataServiceImpl
│   │   └── mapper/              # StockMyDataMapper (MapStruct)
│   │
│   ├── mydata/                  # 연동 확인·연동 목록·동기화
│   │   └── controller/          # MyDataController
│   │
│   └── aggregation/             # 은행+증권 통합 자산 집계
│       └── controller/          # AggregationController
│
└── global/
    ├── config/
    │   ├── AsyncConfig.java     # 비동기 스레드풀 설정
    │   ├── FeignConfig.java     # Feign 로거 레벨 설정
    │   └── SwaggerConfig.java
    ├── exception/               # ErrorCode enum, GlobalExceptionHandler
    ├── filter/                  # TraceIdFilter
    ├── logging/                 # TraceIdConstants
    └── response/                # ApiResponse, ErrorResponse, Meta
```

---

## 핵심 구현 포인트

### 1. 거래내역 조회 병렬 처리 (CompletableFuture)

`BankMyDataServiceImpl.getTransactions()`는 은행 거래 조회와 카드 승인내역 조회를 **동시에** 실행한다.

```
getTransactions()
 ├── [비동기] bankFuture  : BankInternalClient.getTransactions()
 └── [비동기] cardFuture  : buildCardApprovalMap()
              └── 카드 목록 조회 후 카드별 승인내역을 CompletableFuture.allOf()로 병렬 조회
 └── 두 future 완료 후 bank 거래 + card 승인내역을 accountTransactionId 기준으로 조인
```

- 카드 승인내역 중 `approvalStatus = "APPROVED"`인 항목만 `merchantName`, `merchantCategory`, `maskedCardNumber`로 매핑
- 카드가 없거나 조회 실패 시 카드 정보 없이 은행 거래만 반환 (부분 실패 허용)

### 2. 비동기 스레드풀 (`AsyncConfig`)

```java
// core=10, max=30, queue=100, threadName="mydata-query-"
executor.setCorePoolSize(10);
executor.setMaxPoolSize(30);
executor.setQueueCapacity(100);
```

전용 `myDataQueryExecutor` 빈을 주입받아 CompletableFuture 작업에만 사용한다.

### 3. 통합 자산 집계 (`AggregationService`)

은행 계좌 잔액 합산과 증권 보유종목 평가금액을 단일 API에서 집계한다.

- 은행·증권 중 한쪽이 없어도 나머지 자산을 정상 반환 (graceful degradation)
- 투자 비중, 수익률은 소수점 둘째 자리까지 반올림

```
총 자산       = Σ(은행 계좌 잔액) + Σ(보유종목 평가금액)
투자 비중(%)  = 증권 자산 / 총 자산 × 100
총 수익률(%)  = (평가금액 - 매입금액) / 매입금액 × 100
```

### 4. 도메인별 예외 → 공통 ErrorCode 변환

각 도메인은 자체 Exception 클래스(`BankMyDataException`, `StockMyDataException`)를 사용하며,  
`GlobalExceptionHandler`가 `ErrorCode` enum의 `HttpStatus`를 참조해 HTTP 응답 코드를 결정한다.

```java
public enum ErrorCode {
  ACCOUNT_001(HttpStatus.NOT_FOUND,              "ACCOUNT_001",    "계좌 없음"),
  BANK_INTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "BANK_500", "은행 API 호출 중 오류"),
  STOCK_HOLDING_NOT_FOUND(HttpStatus.NOT_FOUND,  "STOCK_HOLDING_001", "보유 종목이 없습니다"),
  ...
}
```

Feign 404 → `ACCOUNT_001`, Feign 5xx → `BANK_INTERNAL_API_ERROR`로 재포장한다.

### 5. MapStruct DTO 분리

클라이언트 내부 DTO(`client/dto/response/`)와 공개 API DTO(`dto/response/`)를 완전히 분리한다.  
MapStruct 인터페이스가 컴파일 시점에 구현체를 생성하므로 런타임 리플렉션 비용이 없다.

```java
@Mapper(componentModel = "spring")
public interface BankMyDataMapper {
  AccountSummaryResponse toAccountSummary(BankAccountResponse response);
  List<AccountSummaryResponse> toAccountSummaryList(List<BankAccountResponse> responses);
  BalanceResponse toBalanceResponse(BankBalanceResponse response);
}
```

---

## API 목록

**Base URL:** `http://localhost:8084/mydata/v1`  
**Swagger UI:** `http://localhost:8084/swagger-ui.html`

### MyData

| Method | Path | 설명 |
|--------|------|------|
| POST | `/connect` | 금융기관 연동 확인 |
| GET | `/connections` | 연동된 계좌 목록 조회 |
| POST | `/sync` | 데이터 동기화 |

### Bank

| Method | Path | 설명 |
|--------|------|------|
| GET | `/bank/accounts` | 은행 계좌 목록 |
| GET | `/bank/accounts/{accountId}` | 계좌 상세 |
| GET | `/bank/accounts/{accountId}/balance` | 잔액 조회 |
| GET | `/bank/accounts/{accountId}/transactions` | 거래내역 (카드 승인내역 조인 포함) |
| GET | `/bank/accounts/{accountId}/transactions/categories` | 카테고리별 지출 합계 |

### Stock

| Method | Path | 설명 |
|--------|------|------|
| GET | `/stock/accounts` | 증권 계좌 목록 |
| GET | `/stock/accounts/{accountId}/holdings` | 보유 종목 |
| GET | `/stock/accounts/{accountId}/portfolio` | 포트폴리오 |
| GET | `/stock/accounts/{accountId}/returns` | 수익률 |
| GET | `/stock/assets/summary` | 증권 자산 요약 |

### Aggregation

| Method | Path | 설명 |
|--------|------|------|
| GET | `/assets/summary` | 통합 자산 요약 |
| GET | `/assets/distribution` | 은행/증권 자산 분포 |
| GET | `/assets/dashboard` | 통합 자산 대시보드 |

### 공통 헤더

| 헤더 | 설명 | 필수 |
|------|------|------|
| `X-Firebase-Uid` | 사용자 식별 (사용자 특정 API) | 조건부 |
| `X-Trace-Id` | 요청 추적 ID | 권장 |

---

## 설정 및 실행

### 환경별 프로파일

| 프로파일 | transaction-server URL | 용도 |
|----------|------------------------|------|
| (기본) | `http://localhost:8083` | 로컬 개발 |
| `app` | `http://10.10.4.107:8083` | 온프레미스 연동 |
| `test` | - | 테스트 전용 |

### 실행

```bash
# 기본 (로컬 transaction-server 필요)
./gradlew bootRun

# 온프레미스 연동
./gradlew bootRun --args='--spring.profiles.active=app'
```

### 빌드

```bash
./gradlew build
```

---

## 테스트

Mockito BDDMockito 기반의 단위 테스트를 도메인별로 작성한다.

```
test/
├── domain/bank/service/BankMyDataServiceTest       # getAccounts, getAccountDetail, getBalance,
│                                                   # getTransactions, getTransactionCategories
├── domain/bank/controller/BankMyDataControllerTest
├── domain/stock/service/StockMyDataServiceTest
├── domain/stock/controller/StockMyDataControllerTest
├── domain/mydata/service/MyDataServiceTest
├── domain/mydata/controller/MyDataControllerTest
├── domain/aggregation/service/AggregationServiceTest
└── domain/aggregation/controller/AggregationControllerTest
```

테스트 설계 원칙:
- `@Nested` + `@DisplayName`으로 시나리오를 계층화
- BDDMockito `given/willReturn/willThrow` 스타일로 가독성 확보
- 성공·부분 실패(카드 없음)·Feign 예외·null 응답 케이스를 개별 검증
- `Executor`를 `Runnable::run`으로 주입해 비동기 코드를 동기로 테스트

```bash
./gradlew test
```

---

## 코드 품질

### Spotless (Google Java Format)

```bash
# 포맷 검사
./gradlew spotlessCheck

# 포맷 자동 수정
./gradlew spotlessApply
```

### 로그 패턴

```
%d{yyyy-MM-dd HH:mm:ss} [%X{traceId}] [%thread] %-5level %logger - %msg%n
```

Feign 클라이언트 로거 레벨은 `HEADERS`로 설정되어 요청/응답 헤더를 DEBUG 레벨로 출력한다.

```yaml
logging:
  level:
    com.mydata.domain.bank.client.internal: DEBUG
    com.mydata.domain.stock.client.internal: DEBUG
```
