package com.moonbaar.domain.visit.controller;

import com.moonbaar.domain.visit.dto.VisitRequest;
import com.moonbaar.domain.visit.dto.VisitResponse;
import com.moonbaar.domain.visit.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class VisitController {

    // 임시 유저 아이디
    private Long MOCK_USER_ID = 1L;

    private final VisitService visitService;

    @PostMapping("/{eventId}/visit")
    public ResponseEntity<VisitResponse> visitEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody VisitRequest request) {

        VisitResponse response = visitService.visitEvent(MOCK_USER_ID, eventId, request);
        return ResponseEntity.ok(response);
    }
}
