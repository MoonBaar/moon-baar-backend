package com.moonbaar.domain.statistics.controller;

import com.moonbaar.domain.statistics.dto.StatisticsResponse;
import com.moonbaar.domain.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class StatisticsController {

    Long MOCK_USER_ID = 1L;

    private final StatisticsService statisticsService;

    @GetMapping("/me/statistics")
    public ResponseEntity<StatisticsResponse> getUserStatistics() {
        StatisticsResponse response = statisticsService.getUserStatistics(MOCK_USER_ID);
        return ResponseEntity.ok(response);
    }
}
