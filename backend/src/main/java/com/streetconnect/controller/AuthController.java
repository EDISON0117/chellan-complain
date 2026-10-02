package com.streetconnect.controller;

import com.streetconnect.dto.ApiResponse;
import com.streetconnect.dto.AuthRequest;
import com.streetconnect.dto.AuthResponse;
import com.streetconnect.dto.RegisterRequest;
import com.streetconnect.dto.UserDto;
import com.streetconnect.entity.User;
import com.streetconnect.service.AuthService;
import com.streetconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful! Welcome to StreetConnect.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful!", response));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody Map<String, String> payload) {
        String identifier = payload.get("identifier");
        if (identifier == null || identifier.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Identifier (email or mobile number) is required"));
        }
        String msg = authService.forgotPassword(identifier);
        return ResponseEntity.ok(ApiResponse.success(msg, msg));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        User user = userService.getCurrentAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        return ResponseEntity.ok(ApiResponse.success("Current session retrieved", userService.convertToDto(user)));
    }
}
