package com.moonbaar.domain.visit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonbaar.common.config.SecurityTestConfig;
import com.moonbaar.domain.visit.dto.FootprintListResponse;
import com.moonbaar.domain.visit.dto.FootprintRequest;
import com.moonbaar.domain.visit.dto.FootprintResponse;
import com.moonbaar.domain.visit.service.FootprintService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FootprintController.class)
@Import(SecurityTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class FootprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FootprintService footprintService;

    private final Long MOCK_USER_ID = 1L;
    private final Long EVENT_ID = 1L;

    @Test
    @DisplayName("지도 범위 내 방문 위치 조회 성공")
    void getUserFootprints_ReturnsFootprints() throws Exception {
        // Given
        FootprintResponse footprint1 = new FootprintResponse(
                1L,
                "연극 '환상동화'",
                "세종문화회관 세종M씨어터",
                "https://example.com/image1.jpg",
                new BigDecimal("37.5725"),
                new BigDecimal("126.9760"),
                LocalDateTime.now().minusDays(5)
        );

        FootprintResponse footprint2 = new FootprintResponse(
                2L,
                "노원문화예술회관 국악예술단 정기공연",
                "노원문화예술회관 대공연장",
                "https://example.com/image2.jpg",
                new BigDecimal("37.6523"),
                new BigDecimal("127.0723"),
                LocalDateTime.now().minusDays(2)
        );

        FootprintListResponse response = new FootprintListResponse(List.of(footprint1, footprint2));

        when(footprintService.findUserFootprints(eq(MOCK_USER_ID), any(FootprintRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/users/me/footprints")
                        .param("minLat", "37.5000")
                        .param("maxLat", "37.7000")
                        .param("minLng", "126.9000")
                        .param("maxLng", "127.1000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events").isArray())
                .andExpect(jsonPath("$.events.length()").value(2))
                .andExpect(jsonPath("$.events[0].id").value(1))
                .andExpect(jsonPath("$.events[0].title").value("연극 '환상동화'"))
                .andExpect(jsonPath("$.events[0].place").value("세종문화회관 세종M씨어터"))
                .andExpect(jsonPath("$.events[0].latitude").value(37.5725))
                .andExpect(jsonPath("$.events[0].longitude").value(126.9760))
                .andExpect(jsonPath("$.events[1].id").value(2))
                .andExpect(jsonPath("$.events[1].title").value("노원문화예술회관 국악예술단 정기공연"));
    }

    @Test
    @DisplayName("필수 파라미터 누락 시 400 응답을 반환해야 한다")
    void getUserFootprints_WithMissingParameters_ReturnsBadRequest() throws Exception {
        // When & Then - minLat 파라미터 누락
        mockMvc.perform(get("/users/me/footprints")
                        .param("maxLat", "37.7000")
                        .param("minLng", "126.9000")
                        .param("maxLng", "127.1000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // When & Then - maxLat 파라미터 누락
        mockMvc.perform(get("/users/me/footprints")
                        .param("minLat", "37.5000")
                        .param("minLng", "126.9000")
                        .param("maxLng", "127.1000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // When & Then - minLng 파라미터 누락
        mockMvc.perform(get("/users/me/footprints")
                        .param("minLat", "37.5000")
                        .param("maxLat", "37.7000")
                        .param("maxLng", "127.1000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // When & Then - maxLng 파라미터 누락
        mockMvc.perform(get("/users/me/footprints")
                        .param("minLat", "37.5000")
                        .param("maxLat", "37.7000")
                        .param("minLng", "126.9000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("빈 결과 반환 처리 테스트")
    void getUserFootprints_WithNoResults_ReturnsEmptyList() throws Exception {
        // Given
        FootprintListResponse emptyResponse = new FootprintListResponse(List.of());

        when(footprintService.findUserFootprints(eq(MOCK_USER_ID), any(FootprintRequest.class)))
                .thenReturn(emptyResponse);

        // When & Then
        mockMvc.perform(get("/users/me/footprints")
                        .param("minLat", "37.5000")
                        .param("maxLat", "37.7000")
                        .param("minLng", "126.9000")
                        .param("maxLng", "127.1000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events").isArray())
                .andExpect(jsonPath("$.events.length()").value(0));
    }
}
