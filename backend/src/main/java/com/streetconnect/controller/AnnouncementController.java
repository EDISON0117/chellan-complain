package com.streetconnect.controller;

import com.streetconnect.dto.AnnouncementDto;
import com.streetconnect.dto.ApiResponse;
import com.streetconnect.entity.User;
import com.streetconnect.exception.UnauthorizedException;
import com.streetconnect.service.AnnouncementService;
import com.streetconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final UserService userService;

    @Autowired
    public AnnouncementController(AnnouncementService announcementService, UserService userService) {
        this.announcementService = announcementService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnnouncementDto>>> getAllAnnouncements() {
        List<AnnouncementDto> announcements = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(ApiResponse.success("Announcements retrieved successfully", announcements));
    }

    @GetMapping("/street/{streetId}")
    public ResponseEntity<ApiResponse<List<AnnouncementDto>>> getAnnouncementsForStreet(@PathVariable Long streetId) {
        List<AnnouncementDto> announcements = announcementService.getAnnouncementsForStreet(streetId);
        return ResponseEntity.ok(ApiResponse.success("Announcements for street retrieved", announcements));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AnnouncementDto>> createAnnouncement(@Valid @RequestBody AnnouncementDto dto) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        AnnouncementDto created = announcementService.createAnnouncement(dto, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Announcement published successfully", created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(@PathVariable Long id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        announcementService.deleteAnnouncement(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted successfully"));
    }
}
