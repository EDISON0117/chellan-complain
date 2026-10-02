package com.streetconnect.service;

import com.streetconnect.entity.Complaint;
import com.streetconnect.entity.NotificationType;
import com.streetconnect.entity.Support;
import com.streetconnect.entity.User;
import com.streetconnect.exception.ResourceNotFoundException;
import com.streetconnect.repository.ComplaintRepository;
import com.streetconnect.repository.SupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SupportService {

    private final SupportRepository supportRepository;
    private final ComplaintRepository complaintRepository;
    private final NotificationService notificationService;

    @Autowired
    public SupportService(SupportRepository supportRepository,
                          ComplaintRepository complaintRepository,
                          NotificationService notificationService) {
        this.supportRepository = supportRepository;
        this.complaintRepository = complaintRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Map<String, Object> toggleSupport(Long complaintId, User currentUser) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        Optional<Support> existingSupport = supportRepository.findByComplaintIdAndUserId(complaintId, currentUser.getId());

        boolean supported;
        if (existingSupport.isPresent()) {
            supportRepository.delete(existingSupport.get());
            supported = false;
        } else {
            Support support = new Support(complaint, currentUser);
            supportRepository.save(support);
            supported = true;

            // Notify complaint owner if it's someone else
            if (!complaint.getUser().getId().equals(currentUser.getId())) {
                notificationService.createNotification(
                        complaint.getUser(),
                        "New Support on Your Issue",
                        currentUser.getFullName() + " supported your complaint: \"" + complaint.getTitle() + "\"",
                        "/complaint-detail.html?id=" + complaint.getId(),
                        NotificationType.SUPPORT
                );
            }
        }

        long count = supportRepository.countByComplaintId(complaintId);

        Map<String, Object> result = new HashMap<>();
        result.put("supported", supported);
        result.put("supportsCount", count);
        return result;
    }

    @Transactional(readOnly = true)
    public boolean isSupportedByUser(Long complaintId, Long userId) {
        if (userId == null) return false;
        return supportRepository.existsByComplaintIdAndUserId(complaintId, userId);
    }

    @Transactional(readOnly = true)
    public long getSupportCount(Long complaintId) {
        return supportRepository.countByComplaintId(complaintId);
    }
}
