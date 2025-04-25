package com.moonbaar.domain.statistics.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.moonbaar.common.config.SecurityTestConfig;
import com.moonbaar.common.security.WithMockCustomUser;
import com.moonbaar.domain.statistics.dto.GroupStatistics;
import com.moonbaar.domain.statistics.dto.GroupStatistics.TopStatistic;
import com.moonbaar.domain.statistics.dto.StatisticsResponse;
import com.moonbaar.domain.statistics.dto.SummaryStatistics;
import com.moonbaar.domain.statistics.service.StatisticsService;
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

@WebMvcTest(StatisticsController.class)
@Import(SecurityTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockCustomUser
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatisticsService statisticsService;

    private final Long MOCK_USER_ID = 1L;

    @Test
    @DisplayName("사용자 방문 통계 정보 조회 성공")
    void getUserStatistics_ReturnSuccess() throws Exception {
        // given
        SummaryStatistics summary = SummaryStatistics.of(23, 8);

        TopStatistic topCategory = TopStatistic.of("전시/미술", 8L, 35);
        List<String> allCategories = List.of("전시/미술", "클래식", "연극", "콘서트", "교육/체험");
        GroupStatistics categories = GroupStatistics.of(topCategory, allCategories);

        TopStatistic topDistrict = TopStatistic.of("종로구", 5L, 22);
        List<String> allDistricts = List.of("종로구", "강남구", "서초구", "마포구");
        GroupStatistics districts = GroupStatistics.of(topDistrict, allDistricts);

        StatisticsResponse response = StatisticsResponse.of(summary, categories, districts);

        when(statisticsService.getUserStatistics(MOCK_USER_ID)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/users/me/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.totalVisits").value(23))
                .andExpect(jsonPath("$.summary.thisMonthVisits").value(8))
                .andExpect(jsonPath("$.categories.top.name").value("전시/미술"))
                .andExpect(jsonPath("$.categories.top.count").value(8))
                .andExpect(jsonPath("$.categories.top.percentage").value(35))
                .andExpect(jsonPath("$.categories.all").isArray())
                .andExpect(jsonPath("$.categories.all.length()").value(5))
                .andExpect(jsonPath("$.districts.top.name").value("종로구"))
                .andExpect(jsonPath("$.districts.top.percentage").value(22))
                .andExpect(jsonPath("$.districts.all").isArray())
                .andExpect(jsonPath("$.districts.all.length()").value(4));
    }

    @Test
    @DisplayName("방문 기록이 없는 사용자의 통계 정보 조회")
    void getUserStatistics_WithNoVisits_ReturnEmptyStatistics() throws Exception {
        // given
        SummaryStatistics summary = SummaryStatistics.of(0, 0);
        GroupStatistics emptyGroup = GroupStatistics.of(null, List.of());
        StatisticsResponse emptyResponse = StatisticsResponse.of(summary, emptyGroup, emptyGroup);

        when(statisticsService.getUserStatistics(MOCK_USER_ID)).thenReturn(emptyResponse);

        // when & then
        mockMvc.perform(get("/users/me/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.totalVisits").value(0))
                .andExpect(jsonPath("$.summary.thisMonthVisits").value(0))
                .andExpect(jsonPath("$.categories.top").isEmpty())
                .andExpect(jsonPath("$.categories.all").isArray())
                .andExpect(jsonPath("$.categories.all.length()").value(0))
                .andExpect(jsonPath("$.districts.top").isEmpty())
                .andExpect(jsonPath("$.districts.all").isArray())
                .andExpect(jsonPath("$.districts.all.length()").value(0));
    }
}
