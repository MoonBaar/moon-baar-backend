package com.moonbaar.domain.event.controller;

import com.moonbaar.domain.event.dto.EventDetailResponse;
import com.moonbaar.domain.event.dto.EventListResponse;
import com.moonbaar.domain.event.dto.EventSearchRequest;
import com.moonbaar.domain.event.exeption.InvalidEventParamsException;
import com.moonbaar.domain.event.service.EventService;
import com.moonbaar.domain.like.dto.LikeResponse;
import com.moonbaar.domain.like.service.LikeService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    // 임시 유저 아이디
    private Long MOCK_USER_ID = 1L;
    private final EventService eventService;
    private final LikeService likeService;

    @GetMapping
    public EventListResponse searchEvents(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Long district,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(defaultValue = "startDate") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            EventSearchRequest request = createSearchRequest(
                    query, category, district, startDate, endDate,
                    isFree, sort, order, page, size
            );

            return eventService.searchEvents(request);
        } catch (DateTimeParseException e) {
            throw new InvalidEventParamsException();
        }
    }

    @GetMapping("/{eventId}")
    public EventDetailResponse getEventDetail(@PathVariable Long eventId) {
        return eventService.getEventDetail(eventId);
    }

    private EventSearchRequest createSearchRequest(
            String query, Long category, Long district,
            String startDateStr, String endDateStr,
            Boolean isFree, String sort, String order,
            int page, int size
    ) {
        LocalDate startDate = parseLocalDate(startDateStr);
        LocalDate endDate = parseLocalDate(endDateStr);

        validateSearchParameters(page, size);

        return new EventSearchRequest(
                query, category, district, startDate, endDate,
                isFree, sort, order, page, size
        );
    }

    private LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr);
    }

    private void validateSearchParameters(int page, int size) {
        if (page < 1 || size < 1 || size > 100) {
            throw new InvalidEventParamsException();
        }
    }

    @PostMapping("/{eventId}/like")
    public ResponseEntity<LikeResponse> likeEvent(@PathVariable Long eventId) {
        LikeResponse response = likeService.likeEvent(MOCK_USER_ID, eventId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}/like")
    public ResponseEntity<LikeResponse> unlikeEvent(@PathVariable Long eventId) {
        LikeResponse response = likeService.unlikeEvent(MOCK_USER_ID, eventId);
        return ResponseEntity.ok(response);
    }
}
