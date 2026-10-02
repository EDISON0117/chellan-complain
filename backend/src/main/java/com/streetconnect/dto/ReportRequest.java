package com.streetconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class ReportRequest {

    @NotBlank(message = "Reason for reporting cannot be empty")
    @Size(min = 5, max = 1000, message = "Reason must be between 5 and 1000 characters")
    private String reason;

    public ReportRequest() {}

    public ReportRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
