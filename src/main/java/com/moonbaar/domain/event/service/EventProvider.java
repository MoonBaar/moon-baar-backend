package com.moonbaar.domain.event.service;

import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.event.exeption.EventNotFoundException;
import com.moonbaar.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventProvider {

    private final EventRepository eventRepository;

    public CulturalEvent getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
    }
}
