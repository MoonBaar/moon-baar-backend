package com.moonbaar.domain.event.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.moonbaar.common.config.SecurityTestConfig;
import com.moonbaar.common.exception.BusinessException;
import com.moonbaar.domain.event.dto.EventDetailResponse;
import com.moonbaar.domain.event.exeption.EventErrorCode;
import com.moonbaar.domain.event.service.EventService;
import com.moonbaar.domain.like.dto.LikeResponse;
import com.moonbaar.domain.like.exception.AlreadyLikedEventException;
import com.moonbaar.domain.like.exception.LikeNotFoundException;
import com.moonbaar.domain.like.service.LikeService;
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

    @MockitoBean
    private LikeService likeService;

    private final Long MOCK_USER_ID = 1L;
    private final Long EVENT_ID = 1L;

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

    @Test
    @DisplayName("행사 좋아요 성공")
    void likeEvent_Success() throws Exception {
        // given
        LikeResponse response = LikeResponse.of(EVENT_ID, true);
        when(likeService.likeEvent(MOCK_USER_ID, EVENT_ID)).thenReturn(response);

        // when & then
        mockMvc.perform(post("/events/{eventId}/like", EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(EVENT_ID))
                .andExpect(jsonPath("$.isLiked").value(true));
    }

    @Test
    @DisplayName("이미 좋아요한 행사를 다시 좋아요 시 409 응답")
    void likeEvent_AlreadyLiked() throws Exception {
        // given
        when(likeService.likeEvent(MOCK_USER_ID, EVENT_ID)).thenThrow(new AlreadyLikedEventException());

        // when & then
        mockMvc.perform(post("/events/{eventId}/like", EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlikeEvent_Success() throws Exception {
        // given
        LikeResponse response = new LikeResponse(EVENT_ID, false);
        when(likeService.unlikeEvent(MOCK_USER_ID, EVENT_ID)).thenReturn(response);

        // when & then
        mockMvc.perform(delete("/events/{eventId}/like", EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(EVENT_ID))
                .andExpect(jsonPath("$.isLiked").value(false));
    }

    @Test
    @DisplayName("좋아요하지 않은 행사 취소 시 404 응답")
    void unlikeEvent_NotLiked() throws Exception {
        // given
        when(likeService.unlikeEvent(MOCK_USER_ID, EVENT_ID)).thenThrow(new LikeNotFoundException());

        // when & then
        mockMvc.perform(delete("/events/{eventId}/like", EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
