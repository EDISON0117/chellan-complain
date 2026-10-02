package com.streetconnect.dto;

import java.util.Map;

public class DashboardStatsDto {
    private long totalComplaints;
    private long pendingComplaints;
    private long underReviewComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    private long rejectedComplaints;
    private long totalStreets;
    private long totalResidents;
    private double resolutionRate;
    private Map<String, Long> complaintsByCategory;
    private Map<String, Long> complaintsByStatus;

    public DashboardStatsDto() {}

    public long getTotalComplaints() {
        return totalComplaints;
    }

    public void setTotalComplaints(long totalComplaints) {
        this.totalComplaints = totalComplaints;
    }

    public long getPendingComplaints() {
        return pendingComplaints;
    }

    public void setPendingComplaints(long pendingComplaints) {
        this.pendingComplaints = pendingComplaints;
    }

    public long getUnderReviewComplaints() {
        return underReviewComplaints;
    }

    public void setUnderReviewComplaints(long underReviewComplaints) {
        this.underReviewComplaints = underReviewComplaints;
    }

    public long getInProgressComplaints() {
        return inProgressComplaints;
    }

    public void setInProgressComplaints(long inProgressComplaints) {
        this.inProgressComplaints = inProgressComplaints;
    }

    public long getResolvedComplaints() {
        return resolvedComplaints;
    }

    public void setResolvedComplaints(long resolvedComplaints) {
        this.resolvedComplaints = resolvedComplaints;
    }

    public long getRejectedComplaints() {
        return rejectedComplaints;
    }

    public void setRejectedComplaints(long rejectedComplaints) {
        this.rejectedComplaints = rejectedComplaints;
    }

    public long getTotalStreets() {
        return totalStreets;
    }

    public void setTotalStreets(long totalStreets) {
        this.totalStreets = totalStreets;
    }

    public long getTotalResidents() {
        return totalResidents;
    }

    public void setTotalResidents(long totalResidents) {
        this.totalResidents = totalResidents;
    }

    public double getResolutionRate() {
        return resolutionRate;
    }

    public void setResolutionRate(double resolutionRate) {
        this.resolutionRate = resolutionRate;
    }

    public Map<String, Long> getComplaintsByCategory() {
        return complaintsByCategory;
    }

    public void setComplaintsByCategory(Map<String, Long> complaintsByCategory) {
        this.complaintsByCategory = complaintsByCategory;
    }

    public Map<String, Long> getComplaintsByStatus() {
        return complaintsByStatus;
    }

    public void setComplaintsByStatus(Map<String, Long> complaintsByStatus) {
        this.complaintsByStatus = complaintsByStatus;
    }
}
