package com.streetconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentCreateRequest {

    @NotBlank(message = "Comment content cannot be empty")
    @Size(min = 1, max = 2000, message = "Comment must be between 1 and 2000 characters")
    private String content;

    public CommentCreateRequest() {}

    public CommentCreateRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
