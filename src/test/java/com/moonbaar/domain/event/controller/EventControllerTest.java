package com.moonbaar.domain.event.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.moonbaar.common.config.SecurityTestConfig;
import com.moonbaar.common.security.WithMockCustomUser;
import com.moonbaar.domain.event.dto.EventDetailResponse;
import com.moonbaar.domain.event.exeption.EventErrorCode;
import com.moonbaar.domain.event.exeption.EventNotFoundException;
import com.moonbaar.domain.event.service.EventService;
import com.moonbaar.domain.user.entity.User;
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

@WebMvcTest(EventController.class)
@Import(SecurityTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    private final Long MOCK_USER_ID = 1L;
    private final Long EVENT_ID = 1L;

    private EventDetailResponse createMockResponse(boolean isVisited, boolean isLiked, long visitCount, long likeCount) {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);

        return new EventDetailResponse(
                EVENT_ID,
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
                "https://www.seoulevent.or.kr",
                new BigDecimal("37.5725"),
                new BigDecimal("126.9760"),
                isVisited,
                isLiked,
                visitCount,
                likeCount
        );
    }

    @Test
    @DisplayName("비로그인 사용자도 행사 상세 정보를 조회할 수 있다")
    void getEventDetail_NonAuthenticatedUser() throws Exception {
        // given
        when(eventService.getEventDetail(EVENT_ID)).thenReturn(createMockResponse(false, false, 1, 100));

        // when & then
        mockMvc.perform(get("/events/{eventId}", EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EVENT_ID.intValue()))
                .andExpect(jsonPath("$.title").value("서울시극단 [코믹]"))
                .andExpect(jsonPath("$.isVisited").value(false))
                .andExpect(jsonPath("$.isLiked").value(false))
                .andExpect(jsonPath("$.visitCount").value(1))
                .andExpect(jsonPath("$.likeCount").value(100));
    }

    @Test
    @DisplayName("로그인한 사용자는 좋아요/방문 상태가 포함된 행사 상세 정보를 조회할 수 있다")
    @WithMockCustomUser
    void getEventDetail_AuthenticatedUser() throws Exception {
        // given
        when(eventService.getEventDetailForUser(any(User.class), eq(EVENT_ID)))
                .thenReturn(createMockResponse(true, false, 1, 100));

        // when & then
        mockMvc.perform(get("/events/{eventId}", EVENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EVENT_ID.intValue()))
                .andExpect(jsonPath("$.title").value("서울시극단 [코믹]"))
                .andExpect(jsonPath("$.isVisited").value(true))
                .andExpect(jsonPath("$.isLiked").value(false))
                .andExpect(jsonPath("$.visitCount").value(1))
                .andExpect(jsonPath("$.likeCount").value(100));
    }

    @Test
    @DisplayName("존재하지 않는 행사 ID로 조회 시 404 응답을 반환한다")
    void getEventDetail_WithNonExistingId_ReturnsNotFound() throws Exception {
        // given
        Long nonExistingId = 999L;
        when(eventService.getEventDetail(nonExistingId)).thenThrow(new EventNotFoundException());

        // when & then
        mockMvc.perform(get("/events/{eventId}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(EventErrorCode.EVENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(EventErrorCode.EVENT_NOT_FOUND.getMessage()));
    }
}
