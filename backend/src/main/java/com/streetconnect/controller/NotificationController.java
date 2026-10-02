package com.streetconnect.controller;

import com.streetconnect.dto.ApiResponse;
import com.streetconnect.dto.NotificationDto;
import com.streetconnect.entity.User;
import com.streetconnect.exception.UnauthorizedException;
import com.streetconnect.service.NotificationService;
import com.streetconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getNotifications() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        List<NotificationDto> list = notificationService.getUserNotifications(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", list));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.ok(ApiResponse.success("Unread count", Collections.singletonMap("count", 0L)));
        }
        long count = notificationService.getUnreadCount(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Unread count", Collections.singletonMap("count", count)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(@PathVariable Long id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        NotificationDto updated = notificationService.markAsRead(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", updated));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        notificationService.markAllAsRead(currentUser);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }
}
