package com.moonbaar.domain.visit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonbaar.common.config.SecurityTestConfig;
import com.moonbaar.domain.visit.dto.VisitRequest;
import com.moonbaar.domain.visit.dto.VisitResponse;
import com.moonbaar.domain.visit.exception.RecentlyVisitedException;
import com.moonbaar.domain.visit.exception.InvalidLocationException;
import com.moonbaar.domain.visit.service.VisitService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VisitController.class)
@Import(SecurityTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VisitService visitService;

    private final Long MOCK_USER_ID = 1L;
    private final Long EVENT_ID = 1L;

    @Test
    @DisplayName("행사 방문 인증에 성공한다")
    void visitEvent_Success() throws Exception {
        // given
        VisitRequest request = new VisitRequest(
                new BigDecimal("37.5725"),
                new BigDecimal("126.9760")
        );

        VisitResponse response = new VisitResponse(
                1L,
                EVENT_ID,
                "서울시극단 [코믹]",
                LocalDateTime.now()
        );

        when(visitService.visitEvent(anyLong(), eq(EVENT_ID), any(VisitRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/events/{eventId}/visit", EVENT_ID)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visitId").value(1L))
                .andExpect(jsonPath("$.eventId").value(EVENT_ID))
                .andExpect(jsonPath("$.eventTitle").value("서울시극단 [코믹]"));
    }

    @Test
    @DisplayName("위치 정보가 없으면 방문 인증에 실패한다")
    void visitEvent_NoLocation_ReturnsBadRequest() throws Exception {
        // given
        VisitRequest request = new VisitRequest(null, null);

        // when & then
        mockMvc.perform(post("/events/{eventId}/visit", EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("최근에 방문한 행사는 방문 인증에 실패한다")
    void visitEvent_RecentlyVisited_ReturnsConflict() throws Exception {
        // given
        VisitRequest request = new VisitRequest(
                new BigDecimal("37.5725"),
                new BigDecimal("126.9760")
        );

        when(visitService.visitEvent(anyLong(), eq(EVENT_ID), any(VisitRequest.class)))
                .thenThrow(new RecentlyVisitedException());

        // when & then
        mockMvc.perform(post("/events/{eventId}/visit", EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("행사 위치와 너무 멀면 방문 인증에 실패한다")
    void visitEvent_TooFarFromEvent_ReturnsForbidden() throws Exception {
        // given
        VisitRequest request = new VisitRequest(
                new BigDecimal("37.6625"),
                new BigDecimal("126.9760")
        );

        when(visitService.visitEvent(anyLong(), eq(EVENT_ID), any(VisitRequest.class)))
                .thenThrow(new InvalidLocationException());

        // when & then
        mockMvc.perform(post("/events/{eventId}/visit", EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
