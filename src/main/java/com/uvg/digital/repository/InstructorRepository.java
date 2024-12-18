package com.uvg.digital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uvg.digital.entity.Instructor;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
	 // Buscar un instructor por su userId
    Optional<Instructor> findByUserId(Long userId);
}
