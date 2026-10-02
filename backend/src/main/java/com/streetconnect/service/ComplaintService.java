package com.streetconnect.service;

import com.streetconnect.dto.*;
import com.streetconnect.entity.*;
import com.streetconnect.exception.BadRequestException;
import com.streetconnect.exception.ResourceNotFoundException;
import com.streetconnect.exception.UnauthorizedException;
import com.streetconnect.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintStatusHistoryRepository historyRepository;
    private final CommentRepository commentRepository;
    private final SupportRepository supportRepository;
    private final CategoryService categoryService;
    private final StreetService streetService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public ComplaintService(ComplaintRepository complaintRepository,
                            ComplaintStatusHistoryRepository historyRepository,
                            CommentRepository commentRepository,
                            SupportRepository supportRepository,
                            CategoryService categoryService,
                            StreetService streetService,
                            UserService userService,
                            NotificationService notificationService) {
        this.complaintRepository = complaintRepository;
        this.historyRepository = historyRepository;
        this.commentRepository = commentRepository;
        this.supportRepository = supportRepository;
        this.categoryService = categoryService;
        this.streetService = streetService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Transactional
    public ComplaintResponseDto createComplaint(ComplaintCreateRequest request, User currentUser) {
        Category category = categoryService.getCategoryEntityById(request.getCategoryId());

        Street street;
        if (request.getStreetId() != null) {
            street = streetService.getStreetEntityById(request.getStreetId());
        } else if (currentUser.getStreet() != null) {
            street = currentUser.getStreet();
        } else {
            throw new BadRequestException("Please select or join a street before creating a complaint");
        }

        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle().trim());
        complaint.setDescription(request.getDescription().trim());
        complaint.setCategory(category);
        complaint.setStreet(street);
        complaint.setUser(currentUser);
        complaint.setLandmark(request.getLandmark() != null ? request.getLandmark().trim() : null);
        complaint.setImageUrl(request.getImageUrl());
        complaint.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setContactNumber(request.getContactNumber() != null ? request.getContactNumber().trim() : currentUser.getMobileNumber());

        Complaint saved = complaintRepository.save(complaint);

        // Record initial status history
        ComplaintStatusHistory history = new ComplaintStatusHistory(
                saved,
                null,
                ComplaintStatus.PENDING,
                currentUser,
                "Complaint filed by resident."
        );
        historyRepository.save(history);

        return convertToDto(saved, currentUser);
    }

    @Transactional
    public ComplaintResponseDto updateComplaint(Long id, ComplaintUpdateRequest request, User currentUser) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        // Only owner or admin can edit content
        if (!complaint.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You are not authorized to update this complaint");
        }

        // Cannot update if resolved or rejected
        if (complaint.getStatus() == ComplaintStatus.RESOLVED || complaint.getStatus() == ComplaintStatus.REJECTED) {
            throw new BadRequestException("Cannot edit a complaint that is already " + complaint.getStatus());
        }

        Category category = categoryService.getCategoryEntityById(request.getCategoryId());
        complaint.setTitle(request.getTitle().trim());
        complaint.setDescription(request.getDescription().trim());
        complaint.setCategory(category);
        complaint.setLandmark(request.getLandmark() != null ? request.getLandmark().trim() : null);
        if (request.getImageUrl() != null) {
            complaint.setImageUrl(request.getImageUrl());
        }
        if (request.getPriority() != null) {
            complaint.setPriority(request.getPriority());
        }
        if (request.getContactNumber() != null) {
            complaint.setContactNumber(request.getContactNumber().trim());
        }

        Complaint saved = complaintRepository.save(complaint);
        return convertToDto(saved, currentUser);
    }

    @Transactional
    public void deleteComplaint(Long id, User currentUser) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        boolean isOwner = complaint.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You are not authorized to delete this complaint");
        }

        complaintRepository.delete(complaint);
    }

    @Transactional(readOnly = true)
    public ComplaintResponseDto getComplaintById(Long id, User currentUser) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
        return convertToDto(complaint, currentUser);
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponseDto> searchAndFilterComplaints(
            Long streetId,
            Long categoryId,
            ComplaintStatus status,
            Priority priority,
            String search,
            String sortBy,
            User currentUser
    ) {
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        List<Complaint> complaints = complaintRepository.searchAndFilter(streetId, categoryId, status, priority, searchTerm);

        List<ComplaintResponseDto> dtoList = complaints.stream()
                .map(c -> convertToDto(c, currentUser))
                .collect(Collectors.toList());

        // Dynamic sorting
        if ("most_supported".equalsIgnoreCase(sortBy)) {
            dtoList.sort(Comparator.comparingLong(ComplaintResponseDto::getSupportsCount).reversed());
        } else if ("most_commented".equalsIgnoreCase(sortBy)) {
            dtoList.sort(Comparator.comparingLong(ComplaintResponseDto::getCommentsCount).reversed());
        } else if ("highest_priority".equalsIgnoreCase(sortBy)) {
            dtoList.sort((a, b) -> {
                int pA = getPriorityWeight(a.getPriority());
                int pB = getPriorityWeight(b.getPriority());
                return Integer.compare(pB, pA);
            });
        }
        // Default is newest first (already sorted by createdAt DESC from query)

        return dtoList;
    }

    private int getPriorityWeight(Priority priority) {
        if (priority == null) return 0;
        return switch (priority) {
            case CRITICAL -> 4;
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponseDto> getUserComplaints(User currentUser) {
        return complaintRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(c -> convertToDto(c, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public ComplaintResponseDto updateComplaintStatus(Long id, ComplaintStatusUpdateRequest request, User currentUser) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        // Only Moderator or Admin can update status
        if (currentUser.getRole() != Role.ROLE_MODERATOR && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Only street moderators or admins can update complaint status");
        }

        ComplaintStatus previousStatus = complaint.getStatus();
        ComplaintStatus newStatus = request.getStatus();

        complaint.setStatus(newStatus);
        if (request.getOfficialRemark() != null && !request.getOfficialRemark().trim().isEmpty()) {
            complaint.setOfficialRemark(request.getOfficialRemark().trim());
        }
        if (request.getResolutionNotes() != null && !request.getResolutionNotes().trim().isEmpty()) {
            complaint.setResolutionNotes(request.getResolutionNotes().trim());
        }

        if (newStatus == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
        } else if (newStatus != ComplaintStatus.RESOLVED && previousStatus == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(null);
        }

        Complaint saved = complaintRepository.save(complaint);

        // Record status change in audit history
        String remarkText = request.getOfficialRemark() != null ? request.getOfficialRemark() : "Status updated to " + newStatus;
        ComplaintStatusHistory history = new ComplaintStatusHistory(
                saved,
                previousStatus,
                newStatus,
                currentUser,
                remarkText
        );
        historyRepository.save(history);

        // Notify complaint creator
        notificationService.createNotification(
                complaint.getUser(),
                "Complaint Status Updated: " + newStatus,
                "Your complaint \"" + complaint.getTitle() + "\" has been updated to " + newStatus +
                        (request.getOfficialRemark() != null ? " - Remark: " + request.getOfficialRemark() : ""),
                "/complaint-detail.html?id=" + complaint.getId(),
                NotificationType.STATUS_UPDATE
        );

        return convertToDto(saved, currentUser);
    }

    @Transactional(readOnly = true)
    public List<ComplaintStatusHistoryDto> getComplaintStatusHistory(Long complaintId) {
        return historyRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId)
                .stream()
                .map(this::convertHistoryToDto)
                .collect(Collectors.toList());
    }

    public ComplaintResponseDto convertToDto(Complaint complaint, User currentUser) {
        if (complaint == null) return null;

        ComplaintResponseDto dto = new ComplaintResponseDto();
        dto.setId(complaint.getId());
        dto.setTitle(complaint.getTitle());
        dto.setDescription(complaint.getDescription());
        dto.setCategory(categoryService.convertToDto(complaint.getCategory()));
        dto.setStreet(streetService.convertToDto(complaint.getStreet()));
        dto.setUser(userService.convertToDto(complaint.getUser()));
        dto.setLandmark(complaint.getLandmark());
        dto.setImageUrl(complaint.getImageUrl());
        dto.setPriority(complaint.getPriority());
        dto.setStatus(complaint.getStatus());
        dto.setOfficialRemark(complaint.getOfficialRemark());
        dto.setResolutionNotes(complaint.getResolutionNotes());
        dto.setContactNumber(complaint.getContactNumber());
        dto.setResolvedAt(complaint.getResolvedAt());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());

        long commentsCount = commentRepository.countByComplaintId(complaint.getId());
        long supportsCount = supportRepository.countByComplaintId(complaint.getId());
        dto.setCommentsCount(commentsCount);
        dto.setSupportsCount(supportsCount);

        boolean supported = currentUser != null && supportRepository.existsByComplaintIdAndUserId(complaint.getId(), currentUser.getId());
        dto.setSupportedByCurrentUser(supported);

        return dto;
    }

    private ComplaintStatusHistoryDto convertHistoryToDto(ComplaintStatusHistory history) {
        if (history == null) return null;

        ComplaintStatusHistoryDto dto = new ComplaintStatusHistoryDto();
        dto.setId(history.getId());
        dto.setComplaintId(history.getComplaint() != null ? history.getComplaint().getId() : null);
        dto.setPreviousStatus(history.getPreviousStatus());
        dto.setNewStatus(history.getNewStatus());
        if (history.getChangedByUser() != null) {
            dto.setChangedByName(history.getChangedByUser().getFullName());
            dto.setChangedByRole(history.getChangedByUser().getRole().name());
        }
        dto.setRemark(history.getRemark());
        dto.setCreatedAt(history.getCreatedAt());

        return dto;
    }
}
