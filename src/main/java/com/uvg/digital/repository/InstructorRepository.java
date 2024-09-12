package com.uvg.digital.repository;

import com.uvg.digital.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    // Métodos personalizados si son necesarios
}
