package com.streetconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileUpdateRequest {

    @NotBlank(message = "Full name cannot be empty")
    @Size(min = 3, max = 100)
    private String fullName;

    private Long streetId;

    private String profileImage;

    public ProfileUpdateRequest() {}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getStreetId() {
        return streetId;
    }

    public void setStreetId(Long streetId) {
        this.streetId = streetId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
