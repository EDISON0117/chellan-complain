package com.streetconnect.dto;

import com.streetconnect.entity.ComplaintStatus;
import jakarta.validation.constraints.NotNull;

public class ComplaintStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ComplaintStatus status;

    private String officialRemark;

    private String resolutionNotes;

    public ComplaintStatusUpdateRequest() {}

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getOfficialRemark() {
        return officialRemark;
    }

    public void setOfficialRemark(String officialRemark) {
        this.officialRemark = officialRemark;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
}
