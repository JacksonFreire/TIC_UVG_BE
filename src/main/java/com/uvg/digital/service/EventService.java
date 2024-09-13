package com.uvg.digital.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uvg.digital.entity.Event;
import com.uvg.digital.repository.EventRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Page<Event> getAllVisibleEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findByIsVisibleTrueOrderByStartDateAsc(pageable);
    }
    
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }
}
