package com.moonbaar.domain.visit.controller;

import com.moonbaar.common.oauth.CustomUserDetails;
import com.moonbaar.domain.visit.dto.VisitListRequest;
import com.moonbaar.domain.visit.dto.VisitListResponse;
import com.moonbaar.domain.visit.service.UserVisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserVisitController {

    private final UserVisitService userVisitService;

    @GetMapping("/me/visits")
    public ResponseEntity<VisitListResponse> getUserVisits(
            @ModelAttribute @Valid VisitListRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        VisitListResponse response = userVisitService.getUserVisits(userDetails.getUser(), request);
        return ResponseEntity.ok(response);
    }
}
