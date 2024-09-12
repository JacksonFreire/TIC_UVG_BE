package com.uvg.digital.repository;

import com.uvg.digital.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    // Método personalizado para obtener el currículo de un curso por su ID
    List<Curriculum> findByCourseId(Long courseId);
}
