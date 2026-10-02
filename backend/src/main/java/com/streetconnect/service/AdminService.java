package com.streetconnect.service;

import com.streetconnect.dto.DashboardStatsDto;
import com.streetconnect.dto.ReportDto;
import com.streetconnect.dto.ReportRequest;
import com.streetconnect.dto.UserDto;
import com.streetconnect.entity.*;
import com.streetconnect.exception.ResourceNotFoundException;
import com.streetconnect.repository.ComplaintRepository;
import com.streetconnect.repository.ReportRepository;
import com.streetconnect.repository.StreetRepository;
import com.streetconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final StreetRepository streetRepository;
    private final ReportRepository reportRepository;
    private final UserService userService;

    @Autowired
    public AdminService(ComplaintRepository complaintRepository,
                        UserRepository userRepository,
                        StreetRepository streetRepository,
                        ReportRepository reportRepository,
                        UserService userService) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.streetRepository = streetRepository;
        this.reportRepository = reportRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        long total = complaintRepository.count();
        long pending = complaintRepository.countByStatus(ComplaintStatus.PENDING);
        long underReview = complaintRepository.countByStatus(ComplaintStatus.UNDER_REVIEW);
        long inProgress = complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS);
        long resolved = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);
        long rejected = complaintRepository.countByStatus(ComplaintStatus.REJECTED);

        stats.setTotalComplaints(total);
        stats.setPendingComplaints(pending);
        stats.setUnderReviewComplaints(underReview);
        stats.setInProgressComplaints(inProgress);
        stats.setResolvedComplaints(resolved);
        stats.setRejectedComplaints(rejected);

        stats.setTotalStreets(streetRepository.count());
        stats.setTotalResidents(userRepository.countByRole(Role.ROLE_RESIDENT));

        double resolutionRate = total > 0 ? ((double) resolved / (double) total) * 100.0 : 0.0;
        stats.setResolutionRate(Math.round(resolutionRate * 10.0) / 10.0);

        // Category stats
        Map<String, Long> categoryMap = new HashMap<>();
        List<Object[]> catResults = complaintRepository.countByCategory();
        for (Object[] row : catResults) {
            categoryMap.put((String) row[0], (Long) row[1]);
        }
        stats.setComplaintsByCategory(categoryMap);

        // Status stats
        Map<String, Long> statusMap = new HashMap<>();
        List<Object[]> statusResults = complaintRepository.countByStatusGroup();
        for (Object[] row : statusResults) {
            statusMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.setComplaintsByStatus(statusMap);

        return stats;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userService::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUserStatus(Long userId, boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setIsActive(isActive);
        return userService.convertToDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setRole(role);
        return userService.convertToDto(userRepository.save(user));
    }

    @Transactional
    public ReportDto reportComplaint(Long complaintId, ReportRequest request, User currentUser) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        Report report = new Report(complaint, currentUser, request.getReason().trim());
        Report saved = reportRepository.save(report);
        return convertReportToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertReportToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportDto updateReportStatus(Long reportId, String status, String actionNotes) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
        report.setStatus(status);
        if (actionNotes != null) {
            report.setActionNotes(actionNotes.trim());
        }
        return convertReportToDto(reportRepository.save(report));
    }

    private ReportDto convertReportToDto(Report r) {
        if (r == null) return null;

        ReportDto dto = new ReportDto();
        dto.setId(r.getId());
        if (r.getComplaint() != null) {
            dto.setComplaintId(r.getComplaint().getId());
            dto.setComplaintTitle(r.getComplaint().getTitle());
        }
        if (r.getReportedByUser() != null) {
            dto.setReportedByUserId(r.getReportedByUser().getId());
            dto.setReportedByUserName(r.getReportedByUser().getFullName());
        }
        dto.setReason(r.getReason());
        dto.setStatus(r.getStatus());
        dto.setActionNotes(r.getActionNotes());
        dto.setCreatedAt(r.getCreatedAt());

        return dto;
    }
}
