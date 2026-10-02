package com.streetconnect.dto;

import com.streetconnect.entity.ComplaintStatus;
import com.streetconnect.entity.Priority;
import java.time.LocalDateTime;

public class ComplaintResponseDto {
    private Long id;
    private String title;
    private String description;
    private CategoryDto category;
    private StreetDto street;
    private UserDto user;
    private String landmark;
    private String imageUrl;
    private Priority priority;
    private ComplaintStatus status;
    private String officialRemark;
    private String resolutionNotes;
    private String contactNumber;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long commentsCount;
    private long supportsCount;
    private boolean isSupportedByCurrentUser;

    public ComplaintResponseDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public StreetDto getStreet() {
        return street;
    }

    public void setStreet(StreetDto street) {
        this.street = street;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public long getSupportsCount() {
        return supportsCount;
    }

    public void setSupportsCount(long supportsCount) {
        this.supportsCount = supportsCount;
    }

    public boolean isSupportedByCurrentUser() {
        return isSupportedByCurrentUser;
    }

    public void setSupportedByCurrentUser(boolean supportedByCurrentUser) {
        isSupportedByCurrentUser = supportedByCurrentUser;
    }
}
