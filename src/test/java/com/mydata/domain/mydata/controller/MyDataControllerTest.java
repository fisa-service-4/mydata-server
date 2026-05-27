package com.mydata.domain.mydata.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mydata.domain.bank.dto.response.AccountSummaryResponse;
import com.mydata.domain.mydata.dto.response.ConnectResponse;
import com.mydata.domain.mydata.dto.response.ConnectionResponse;
import com.mydata.domain.mydata.dto.response.SyncResponse;
import com.mydata.domain.mydata.service.MyDataService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MyDataController.class)
class MyDataControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private MyDataService myDataService;

  @Nested
  @DisplayName("마이데이터 연동 POST /mydata/v1/connect")
  class Connect {

    @Test
    @DisplayName("성공 - 은행/증권 모두 연동")
    void success() throws Exception {
      given(myDataService.connect(anyLong(), any()))
          .willReturn(
              ConnectResponse.builder().connected(true).bankLinked(true).stockLinked(true).build());

      mockMvc
          .perform(
              post("/mydata/v1/connect")
                  .header("X-User-Id", 1L)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"provider\":\"test\"}"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.connected").value(true))
          .andExpect(jsonPath("$.data.bankLinked").value(true))
          .andExpect(jsonPath("$.data.stockLinked").value(true));
    }

    @Test
    @DisplayName("실패 - X-User-Id 헤더 누락 시 400")
    void missingHeader_returns400() throws Exception {
      mockMvc
          .perform(
              post("/mydata/v1/connect")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"provider\":\"test\"}"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.error.code").value("VALID_001"));
    }

    @Test
    @DisplayName("실패 - provider 누락 시 400")
    void missingProvider_returns400() throws Exception {
      mockMvc
          .perform(
              post("/mydata/v1/connect")
                  .header("X-User-Id", 1L)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"provider\":\"\"}"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.success").value(false));
    }
  }

  @Nested
  @DisplayName("연동 목록 조회 GET /mydata/v1/connections")
  class GetConnections {

    @Test
    @DisplayName("성공 - 은행/증권 계좌 목록 반환")
    void success() throws Exception {
      given(myDataService.getConnections(anyLong()))
          .willReturn(
              ConnectionResponse.builder()
                  .bankAccounts(
                      List.of(
                          AccountSummaryResponse.builder()
                              .accountId(1001L)
                              .accountNumber("110-123-456789")
                              .accountName("내 급여통장")
                              .bankCode("088")
                              .balance(3500000L)
                              .build()))
                  .stockAccounts(List.of())
                  .build());

      mockMvc
          .perform(get("/mydata/v1/connections").header("X-User-Id", 1L))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.bankAccounts[0].accountId").value(1001))
          .andExpect(jsonPath("$.data.bankAccounts[0].balance").value(3500000))
          .andExpect(jsonPath("$.data.stockAccounts").isEmpty());
    }

    @Test
    @DisplayName("성공 - 연동 계좌 없는 경우 빈 목록 반환")
    void success_emptyAccounts() throws Exception {
      given(myDataService.getConnections(anyLong()))
          .willReturn(
              ConnectionResponse.builder()
                  .bankAccounts(List.of())
                  .stockAccounts(List.of())
                  .build());

      mockMvc
          .perform(get("/mydata/v1/connections").header("X-User-Id", 2L))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.bankAccounts").isEmpty())
          .andExpect(jsonPath("$.data.stockAccounts").isEmpty());
    }

    @Test
    @DisplayName("실패 - X-User-Id 헤더 누락 시 400")
    void missingHeader_returns400() throws Exception {
      mockMvc
          .perform(get("/mydata/v1/connections"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.error.code").value("VALID_001"));
    }
  }

  @Nested
  @DisplayName("마이데이터 동기화 POST /mydata/v1/sync")
  class Sync {

    @Test
    @DisplayName("성공 - 동기화 완료 후 synced=true 반환")
    void success() throws Exception {
      given(myDataService.sync(anyLong()))
          .willReturn(
              SyncResponse.builder()
                  .synced(true)
                  .syncedAt(LocalDateTime.of(2026, 5, 26, 10, 0, 0))
                  .build());

      mockMvc
          .perform(post("/mydata/v1/sync").header("X-User-Id", 1L))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.synced").value(true))
          .andExpect(jsonPath("$.data.syncedAt").isNotEmpty());
    }

    @Test
    @DisplayName("실패 - X-User-Id 헤더 누락 시 400")
    void missingHeader_returns400() throws Exception {
      mockMvc
          .perform(post("/mydata/v1/sync"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.error.code").value("VALID_001"));
    }
  }
}
