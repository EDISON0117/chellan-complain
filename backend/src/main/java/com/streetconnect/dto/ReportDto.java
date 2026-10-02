package com.streetconnect.dto;

import java.time.LocalDateTime;

public class ReportDto {
    private Long id;
    private Long complaintId;
    private String complaintTitle;
    private Long reportedByUserId;
    private String reportedByUserName;
    private String reason;
    private String status;
    private String actionNotes;
    private LocalDateTime createdAt;

    public ReportDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public String getComplaintTitle() {
        return complaintTitle;
    }

    public void setComplaintTitle(String complaintTitle) {
        this.complaintTitle = complaintTitle;
    }

    public Long getReportedByUserId() {
        return reportedByUserId;
    }

    public void setReportedByUserId(Long reportedByUserId) {
        this.reportedByUserId = reportedByUserId;
    }

    public String getReportedByUserName() {
        return reportedByUserName;
    }

    public void setReportedByUserName(String reportedByUserName) {
        this.reportedByUserName = reportedByUserName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActionNotes() {
        return actionNotes;
    }

    public void setActionNotes(String actionNotes) {
        this.actionNotes = actionNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
