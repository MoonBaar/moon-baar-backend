package com.moonbaar.domain.event.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.moonbaar.common.config.SecurityTestConfig;
import com.moonbaar.common.exception.BusinessException;
import com.moonbaar.domain.event.dto.EventDetailResponse;
import com.moonbaar.domain.event.exeption.EventErrorCode;
import com.moonbaar.domain.event.service.EventService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EventController.class)
@Import(SecurityTestConfig.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    @DisplayName("행사 상세 정보를 조회한다")
    void getEventDetail() throws Exception {
        // given
        Long eventId = 1L;
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);

        EventDetailResponse response = new EventDetailResponse(
                eventId,
                "서울시극단 [코믹]",
                "연극",
                "종로구",
                "세종M씨어터",
                startDate,
                endDate,
                false,
                "30,000원 ~ 50,000원",
                "전체 관람가",
                "김문화, 이예술, 박공연",
                "유쾌한 코미디 연극입니다.",
                "서울시극단이 선보이는 유쾌한 코미디 연극입니다.",
                "https://example.com/image1.jpg",
                "세종문화회관",
                "https://www.seoulevent.or.kr",
                new BigDecimal("37.5725"),
                new BigDecimal("126.9760"),
                false,
                false
        );

        when(eventService.getEventDetail(eventId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/events/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId.intValue()))
                .andExpect(jsonPath("$.title").value("서울시극단 [코믹]"))
                .andExpect(jsonPath("$.category").value("연극"))
                .andExpect(jsonPath("$.district").value("종로구"))
                .andExpect(jsonPath("$.place").value("세종M씨어터"))
                .andExpect(jsonPath("$.isFree").value(false))
                .andExpect(jsonPath("$.useFee").value("30,000원 ~ 50,000원"))
                .andExpect(jsonPath("$.useTarget").value("전체 관람가"));
    }

    @Test
    @DisplayName("존재하지 않는 행사 ID로 조회 시 404 응답을 반환한다")
    void getEventDetail_WithNonExistingId_ReturnsNotFound() throws Exception {
        // given
        Long nonExistingId = 999L;
        when(eventService.getEventDetail(nonExistingId))
                .thenThrow(new BusinessException(EventErrorCode.EVENT_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/events/{eventId}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(EventErrorCode.EVENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(EventErrorCode.EVENT_NOT_FOUND.getMessage()));
    }
}
