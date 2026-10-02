package com.streetconnect.service;

import com.streetconnect.dto.CommentCreateRequest;
import com.streetconnect.dto.CommentDto;
import com.streetconnect.entity.Comment;
import com.streetconnect.entity.Complaint;
import com.streetconnect.entity.NotificationType;
import com.streetconnect.entity.Role;
import com.streetconnect.entity.User;
import com.streetconnect.exception.ResourceNotFoundException;
import com.streetconnect.exception.UnauthorizedException;
import com.streetconnect.repository.CommentRepository;
import com.streetconnect.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ComplaintRepository complaintRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          ComplaintRepository complaintRepository,
                          UserService userService,
                          NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.complaintRepository = complaintRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByComplaint(Long complaintId, User currentUser) {
        return commentRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId)
                .stream()
                .map(c -> convertToDto(c, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(Long complaintId, CommentCreateRequest request, User currentUser) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        Comment comment = new Comment(complaint, currentUser, request.getContent().trim());
        Comment saved = commentRepository.save(comment);

        // Notify complaint owner
        if (!complaint.getUser().getId().equals(currentUser.getId())) {
            notificationService.createNotification(
                    complaint.getUser(),
                    "New Comment on Your Issue",
                    currentUser.getFullName() + " commented: \"" +
                            (request.getContent().length() > 60 ? request.getContent().substring(0, 57) + "..." : request.getContent()) + "\"",
                    "/complaint-detail.html?id=" + complaint.getId(),
                    NotificationType.COMMENT
            );
        }

        return convertToDto(saved, currentUser);
    }

    @Transactional
    public void deleteComment(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        boolean isOwner = comment.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
        boolean isMod = currentUser.getRole() == Role.ROLE_MODERATOR;

        if (!isOwner && !isAdmin && !isMod) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    public CommentDto convertToDto(Comment comment, User currentUser) {
        if (comment == null) return null;

        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setComplaintId(comment.getComplaint() != null ? comment.getComplaint().getId() : null);
        dto.setUser(userService.convertToDto(comment.getUser()));
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());

        boolean isOwner = currentUser != null && comment.getUser().getId().equals(currentUser.getId());
        dto.setOwner(isOwner);

        return dto;
    }
}
