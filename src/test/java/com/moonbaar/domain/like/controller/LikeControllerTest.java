package com.moonbaar.domain.like.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.moonbaar.common.config.SecurityTestConfig;
import com.moonbaar.domain.like.dto.LikeResponse;
import com.moonbaar.domain.like.exception.AlreadyLikedEventException;
import com.moonbaar.domain.like.exception.LikeNotFoundException;
import com.moonbaar.domain.like.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LikeController.class)
@Import(SecurityTestConfig.class)
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LikeService likeService;

    private final Long MOCK_USER_ID = 1L;
    private final Long EVENT_ID = 1L;

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
