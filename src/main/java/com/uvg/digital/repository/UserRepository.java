package com.uvg.digital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uvg.digital.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
    Optional<User> findByEmail(String email);
    
    Optional<User> findByVerificationToken(String token);
    
    Optional<User> findByUsername(String username);

}
