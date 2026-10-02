package com.streetconnect.controller;

import com.streetconnect.dto.ApiResponse;
import com.streetconnect.dto.ChangePasswordRequest;
import com.streetconnect.dto.ProfileUpdateRequest;
import com.streetconnect.dto.UserDto;
import com.streetconnect.entity.User;
import com.streetconnect.exception.UnauthorizedException;
import com.streetconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> getProfile() {
        User user = userService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", userService.convertToDto(user)));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        User user = userService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        UserDto updated = userService.updateProfile(user, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User user = userService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        userService.changePassword(user, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @GetMapping("/street/{streetId}")
    public ResponseEntity<ApiResponse<List<UserDto>>> getResidentsByStreet(@PathVariable Long streetId) {
        List<UserDto> residents = userService.getUsersByStreet(streetId);
        return ResponseEntity.ok(ApiResponse.success("Residents retrieved", residents));
    }
}
