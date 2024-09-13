package com.uvg.digital.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.uvg.digital.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
	
	Page<Event> findByIsVisibleTrueOrderByStartDateAsc(Pageable pageable);
    
}
