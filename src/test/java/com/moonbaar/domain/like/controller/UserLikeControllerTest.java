package com.moonbaar.domain.like.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.moonbaar.common.config.SecurityTestConfig;
import com.moonbaar.common.security.WithMockCustomUser;
import com.moonbaar.domain.like.dto.LikedEventListRequest;
import com.moonbaar.domain.like.dto.LikedEventListResponse;
import com.moonbaar.domain.like.dto.LikedEventResponse;
import com.moonbaar.domain.like.service.LikeService;
import com.moonbaar.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserLikeController.class)
@Import(SecurityTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockCustomUser
public class UserLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LikeService likeService;

    @Test
    @DisplayName("좋아요한 행사 목록 조회 성공")
    void getLikedEvents_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        LikedEventResponse event1 = new LikedEventResponse(
                1L, "서울시극단 [코믹]", "세종M씨어터",
                now.plusDays(1), now.plusDays(3), "https://example.com/image1.jpg");

        LikedEventResponse event2 = new LikedEventResponse(
                2L, "[노원문화원] 국악예술단 정기공연", "노원문화예술회관 대공연장",
                now.plusDays(2), now.plusDays(4), "https://example.com/image2.jpg");

        LikedEventListResponse response = new LikedEventListResponse(
                2L, 1, 1, List.of(event1, event2));

        // 기본값으로 생성되는 request를 검증할 수 있도록 ArgumentCaptor 사용
        ArgumentCaptor<LikedEventListRequest> requestCaptor = ArgumentCaptor.forClass(LikedEventListRequest.class);
        when(likeService.getLikedEvents(any(User.class), requestCaptor.capture()))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/users/me/likes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.events").isArray())
                .andExpect(jsonPath("$.events.length()").value(2))
                .andExpect(jsonPath("$.events[0].id").value(1))
                .andExpect(jsonPath("$.events[0].title").value("서울시극단 [코믹]"))
                .andExpect(jsonPath("$.events[1].id").value(2))
                .andExpect(jsonPath("$.events[1].title").value("[노원문화원] 국악예술단 정기공연"));

        // 서비스에 전달된 요청 객체 검증
        LikedEventListRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.page()).isEqualTo(1); // 기본값 검증
        assertThat(capturedRequest.size()).isEqualTo(20); // 기본값 검증
        assertThat(capturedRequest.order()).isEqualTo("desc"); // 기본값 검증
    }

    @Test
    @DisplayName("좋아요 목록 조회 시 페이징 파라미터 테스트")
    void getLikedEvents_WithPagingParams_Success() throws Exception {
        // given
        LikedEventListResponse response = new LikedEventListResponse(10L, 2, 2, List.of());

        when(likeService.getLikedEvents(any(User.class), any(LikedEventListRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/users/me/likes")
                        .param("page", "2")
                        .param("size", "5")
                        .param("order", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(10))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.currentPage").value(2));
    }

    @Test
    @DisplayName("잘못된 페이지네이션 파라미터로 요청시 400 응답 반환")
    void getLikedEvents_WithInvalidParams_ReturnsBadRequest() throws Exception {
        // when & then
        mockMvc.perform(get("/users/me/likes")
                        .param("page", "0")  // 유효하지 않은 페이지 (1 미만)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/users/me/likes")
                        .param("size", "0")  // 유효하지 않은 크기 (1 미만)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/users/me/likes")
                        .param("size", "101") // 유효하지 않은 크기 (100 초과)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
