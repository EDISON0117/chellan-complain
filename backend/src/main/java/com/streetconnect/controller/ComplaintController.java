package com.streetconnect.controller;

import com.streetconnect.dto.*;
import com.streetconnect.entity.ComplaintStatus;
import com.streetconnect.entity.Priority;
import com.streetconnect.entity.User;
import com.streetconnect.exception.UnauthorizedException;
import com.streetconnect.service.ComplaintService;
import com.streetconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserService userService;

    @Autowired
    public ComplaintController(ComplaintService complaintService, UserService userService) {
        this.complaintService = complaintService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ComplaintResponseDto>>> getComplaints(
            @RequestParam(required = false) Long streetId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "latest") String sortBy
    ) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        List<ComplaintResponseDto> complaints = complaintService.searchAndFilterComplaints(
                streetId, categoryId, status, priority, search, sortBy, currentUser
        );
        return ResponseEntity.ok(ApiResponse.success("Complaints retrieved successfully", complaints));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ComplaintResponseDto>>> getMyComplaints() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        List<ComplaintResponseDto> complaints = complaintService.getUserComplaints(currentUser);
        return ResponseEntity.ok(ApiResponse.success("User complaints retrieved successfully", complaints));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> getComplaintById(@PathVariable Long id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        ComplaintResponseDto complaint = complaintService.getComplaintById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Complaint retrieved successfully", complaint));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> createComplaint(@Valid @RequestBody ComplaintCreateRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        ComplaintResponseDto created = complaintService.createComplaint(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Complaint filed successfully!", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintUpdateRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        ComplaintResponseDto updated = complaintService.updateComplaint(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Complaint updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComplaint(@PathVariable Long id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        complaintService.deleteComplaint(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Complaint deleted successfully"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> updateComplaintStatus(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintStatusUpdateRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        ComplaintResponseDto updated = complaintService.updateComplaintStatus(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Complaint status updated successfully", updated));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<ComplaintStatusHistoryDto>>> getComplaintHistory(@PathVariable Long id) {
        List<ComplaintStatusHistoryDto> history = complaintService.getComplaintStatusHistory(id);
        return ResponseEntity.ok(ApiResponse.success("Complaint status history retrieved", history));
    }
}
