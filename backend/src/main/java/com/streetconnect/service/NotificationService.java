package com.streetconnect.service;

import com.streetconnect.dto.NotificationDto;
import com.streetconnect.entity.Notification;
import com.streetconnect.entity.NotificationType;
import com.streetconnect.entity.User;
import com.streetconnect.exception.ResourceNotFoundException;
import com.streetconnect.exception.UnauthorizedException;
import com.streetconnect.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void createNotification(User user, String title, String message, String link, NotificationType type) {
        if (user == null) return;

        Notification notification = new Notification(user, title, message, link, type);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(User currentUser) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(User currentUser) {
        return notificationRepository.countByUserIdAndIsReadFalse(currentUser.getId());
    }

    @Transactional
    public NotificationDto markAsRead(Long notificationId, User currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You do not own this notification");
        }

        notification.setIsRead(true);
        Notification saved = notificationRepository.save(notification);
        return convertToDto(saved);
    }

    @Transactional
    public void markAllAsRead(User currentUser) {
        List<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        for (Notification n : unread) {
            n.setIsRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    public NotificationDto convertToDto(Notification notification) {
        if (notification == null) return null;

        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setLink(notification.getLink());
        dto.setType(notification.getType());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());

        return dto;
    }
}
