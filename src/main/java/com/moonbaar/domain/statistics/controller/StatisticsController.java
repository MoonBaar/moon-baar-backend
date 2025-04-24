package com.moonbaar.domain.statistics.controller;

import com.moonbaar.common.oauth.CustomUserDetails;
import com.moonbaar.domain.statistics.dto.StatisticsResponse;
import com.moonbaar.domain.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/me/statistics")
    public ResponseEntity<StatisticsResponse> getUserStatistics(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        StatisticsResponse response = statisticsService.getUserStatistics(userDetails.getUser().getId());
        return ResponseEntity.ok(response);
    }
}
