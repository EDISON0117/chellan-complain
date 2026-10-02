package com.streetconnect.controller;

import com.streetconnect.dto.*;
import com.streetconnect.entity.Role;
import com.streetconnect.entity.User;
import com.streetconnect.exception.UnauthorizedException;
import com.streetconnect.service.AdminService;
import com.streetconnect.service.ComplaintService;
import com.streetconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AdminController {

    private final AdminService adminService;
    private final ComplaintService complaintService;
    private final UserService userService;

    @Autowired
    public AdminController(AdminService adminService,
                           ComplaintService complaintService,
                           UserService userService) {
        this.adminService = adminService;
        this.complaintService = complaintService;
        this.userService = userService;
    }

    // Report a complaint (accessible to any authenticated resident)
    @PostMapping("/complaints/{id}/report")
    public ResponseEntity<ApiResponse<ReportDto>> reportComplaint(
            @PathVariable Long id,
            @Valid @RequestBody ReportRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        ReportDto report = adminService.reportComplaint(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Thank you for reporting. Our moderation team will review this issue.", report));
    }

    // Admin Dashboard Stats
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        DashboardStatsDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved successfully", stats));
    }

    // Admin Users Management
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users retrieved successfully", users));
    }

    // Activate/Suspend User
    @PutMapping("/admin/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> payload) {
        Boolean active = payload.get("active");
        if (active == null) {
            active = true;
        }
        UserDto updated = adminService.updateUserStatus(id, active);
        String msg = active ? "User account activated" : "User account suspended";
        return ResponseEntity.ok(ApiResponse.success(msg, updated));
    }

    // Change User Role (e.g. promote to ROLE_MODERATOR)
    @PutMapping("/admin/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String roleStr = payload.get("role");
        Role role = Role.valueOf(roleStr);
        UserDto updated = adminService.updateUserRole(id, role);
        return ResponseEntity.ok(ApiResponse.success("User role updated to " + role, updated));
    }

    // View Reports
    @GetMapping("/admin/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReportDto>>> getAllReports() {
        List<ReportDto> reports = adminService.getAllReports();
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", reports));
    }

    // Update Report Action
    @PutMapping("/admin/reports/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReportDto>> updateReport(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String status = payload.getOrDefault("status", "REVIEWED");
        String actionNotes = payload.get("actionNotes");
        ReportDto updated = adminService.updateReportStatus(id, status, actionNotes);
        return ResponseEntity.ok(ApiResponse.success("Report action recorded", updated));
    }

    // Delete complaint by Admin
    @DeleteMapping("/admin/complaints/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteComplaintAdmin(@PathVariable Long id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        complaintService.deleteComplaint(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Complaint deleted by administration"));
    }
}
