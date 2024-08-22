package com.uvg.digital.controller;

import com.uvg.digital.model.JwtResponse;
import com.uvg.digital.model.LoginRequest;
import com.uvg.digital.model.PasswordResetRequest;
import com.uvg.digital.service.AuthenticationService;
import com.uvg.digital.service.PasswordResetService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        String token = authenticationService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new JwtResponse(token));
    }
    
    @PostMapping("/request-password-reset")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetRequest request) {
        passwordResetService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
    		 @RequestBody Map<String, String> payload) {
    	String newPassword = payload.get("newPassword");
    	passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password has been successfully reset.");
    }
}
