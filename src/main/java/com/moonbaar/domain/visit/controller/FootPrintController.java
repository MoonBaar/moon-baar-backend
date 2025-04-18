package com.moonbaar.domain.visit.controller;

import com.moonbaar.domain.visit.dto.FootprintListResponse;
import com.moonbaar.domain.visit.dto.FootprintRequest;
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

    @GetMapping("/me/footprints")
    public ResponseEntity<FootprintListResponse> getUserFootPrints(
            @ModelAttribute @Valid FootprintRequest request) {

        FootprintListResponse response;
        return response;
    }
}
