package com.uvg.digital.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uvg.digital.entity.Instructor;
import com.uvg.digital.model.ItemInstructorDTO;
import com.uvg.digital.repository.InstructorRepository;

@Service
public class InstructorService {

    @Autowired
    private InstructorRepository instructorRepository;

    // Obtener todos los instructores y convertirlos a DTOs
    public List<ItemInstructorDTO> getAllInstructorDTOs() {
        return instructorRepository.findAll().stream()
                .map(this::convertToDTO) // Conversión de cada Instructor a ItemInstructorDTO
                .collect(Collectors.toList());
    }

    // Obtener un instructor por ID y devolver su DTO
    public ItemInstructorDTO getInstructorDTOById(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instructor no encontrado"));
        return convertToDTO(instructor); // Conversión del Instructor a DTO
    }
    
    private ItemInstructorDTO convertToDTO(Instructor instructor) {
        return new ItemInstructorDTO(
                instructor.getId(),
                instructor.getUser().getFirstName() + " " + instructor.getUser().getLastName() // Nombre completo desde User
        );
    }
    
}
