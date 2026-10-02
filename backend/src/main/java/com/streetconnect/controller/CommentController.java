package com.streetconnect.controller;

import com.streetconnect.dto.ApiResponse;
import com.streetconnect.dto.CommentCreateRequest;
import com.streetconnect.dto.CommentDto;
import com.streetconnect.entity.User;
import com.streetconnect.exception.UnauthorizedException;
import com.streetconnect.service.CommentService;
import com.streetconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/complaints/{complaintId}/comments")
    public ResponseEntity<ApiResponse<List<CommentDto>>> getComments(@PathVariable Long complaintId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        List<CommentDto> comments = commentService.getCommentsByComplaint(complaintId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
    }

    @PostMapping("/complaints/{complaintId}/comments")
    public ResponseEntity<ApiResponse<CommentDto>> addComment(
            @PathVariable Long complaintId,
            @Valid @RequestBody CommentCreateRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        CommentDto created = commentService.addComment(complaintId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Comment added successfully", created));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        commentService.deleteComment(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully"));
    }
}
