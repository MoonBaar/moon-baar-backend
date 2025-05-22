package com.moonbaar.domain.visit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.moonbaar.common.config.SecurityTestConfig;
import com.moonbaar.common.security.WithMockCustomUser;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.visit.dto.VisitItemResponse;
import com.moonbaar.domain.visit.dto.VisitListRequest;
import com.moonbaar.domain.visit.dto.VisitListResponse;
import com.moonbaar.domain.visit.service.UserVisitService;
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

@WebMvcTest(UserVisitController.class)
@Import(SecurityTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserVisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserVisitService userVisitService;

    private final Long MOCK_USER_ID = 1L;

    @Test
    @DisplayName("방문 목록 조회 성공")
    @WithMockCustomUser
    void getUserVisits_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        VisitItemResponse visit1 = new VisitItemResponse(
                1L, "서울시극단 [코믹]", "세종M씨어터",
                "https://example.com/image1.jpg", now.minusDays(3));

        VisitItemResponse visit2 = new VisitItemResponse(
                2L, "노원문화예술회관 국악예술단 정기공연", "노원문화예술회관 대공연장",
                "https://example.com/image2.jpg", now.minusDays(7));

        VisitListResponse response = new VisitListResponse(2L, 1, 1, List.of(visit1, visit2));

        when(userVisitService.getUserVisits(any(User.class), any(VisitListRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/users/me/visits")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.events").isArray())
                .andExpect(jsonPath("$.events.length()").value(2))
                .andExpect(jsonPath("$.events[0].id").value(1))
                .andExpect(jsonPath("$.events[0].title").value("서울시극단 [코믹]"))
                .andExpect(jsonPath("$.events[1].id").value(2))
                .andExpect(jsonPath("$.events[1].title").value("노원문화예술회관 국악예술단 정기공연"));
    }

    @Test
    @DisplayName("방문 목록 조회 - 기간 필터 적용")
    @WithMockCustomUser
    void getUserVisits_WithPeriodFilter_Success() throws Exception {
        // given
        VisitListResponse response = new VisitListResponse(1L, 1, 1, List.of());



        when(userVisitService.getUserVisits(any(User.class), any(VisitListRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/users/me/visits")
                        .param("period", "thisMonth")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1));
    }

    @Test
    @DisplayName("잘못된 기간 파라미터로 요청시 400 응답 반환")
    @WithMockCustomUser
    void getUserVisits_WithInvalidPeriod_ReturnsBadRequest() throws Exception {
        // given

        // when & then
        mockMvc.perform(get("/users/me/visits")
                        .param("period", "invalidPeriod")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
