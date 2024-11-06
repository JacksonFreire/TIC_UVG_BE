package com.uvg.digital.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uvg.digital.entity.Instructor;
import com.uvg.digital.repository.InstructorRepository;

@Service
public class InstructorService {
	
	@Autowired
    private InstructorRepository instructorRepository;

    // Obtener todos los instructores
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    // Obtener un instructor por ID
    public Instructor getInstructorById(Long id) {
        return instructorRepository.findById(id).orElse(null);
    }

}
