package com.moonbaar.domain.visit.controller;

import com.moonbaar.domain.visit.dto.FootprintListResponse;
import com.moonbaar.domain.visit.dto.FootprintRequest;
import com.moonbaar.domain.visit.service.FootprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class FootPrintController {

    // 임시 유저 아이디
    private Long MOCK_USER_ID = 1L;

    private final FootprintService footprintService;

    @GetMapping("/me/footprints")
    public ResponseEntity<FootprintListResponse> getUserFootprints(
            @ModelAttribute @Valid FootprintRequest request) {

        FootprintListResponse response = footprintService.findUserFootprints(MOCK_USER_ID, request);
        return ResponseEntity.ok(response);
    }
}
