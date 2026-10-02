package com.streetconnect.controller;

import com.streetconnect.dto.ApiResponse;
import com.streetconnect.entity.User;
import com.streetconnect.exception.UnauthorizedException;
import com.streetconnect.service.SupportService;
import com.streetconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints/{complaintId}/support")
public class SupportController {

    private final SupportService supportService;
    private final UserService userService;

    @Autowired
    public SupportController(SupportService supportService, UserService userService) {
        this.supportService = supportService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleSupport(@PathVariable Long complaintId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        Map<String, Object> result = supportService.toggleSupport(complaintId, currentUser);
        String message = Boolean.TRUE.equals(result.get("supported")) ? "Support registered!" : "Support removed.";
        return ResponseEntity.ok(ApiResponse.success(message, result));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> removeSupport(@PathVariable Long complaintId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        Map<String, Object> result = supportService.toggleSupport(complaintId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Support updated", result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSupportStatus(@PathVariable Long complaintId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        boolean supported = currentUser != null && supportService.isSupportedByUser(complaintId, currentUser.getId());
        long count = supportService.getSupportCount(complaintId);

        Map<String, Object> map = new HashMap<>();
        map.put("supported", supported);
        map.put("supportsCount", count);

        return ResponseEntity.ok(ApiResponse.success("Support status retrieved", map));
    }
}
