package com.streetconnect.service;

import com.streetconnect.dto.AnnouncementDto;
import com.streetconnect.entity.Announcement;
import com.streetconnect.entity.Role;
import com.streetconnect.entity.Street;
import com.streetconnect.entity.User;
import com.streetconnect.exception.ResourceNotFoundException;
import com.streetconnect.exception.UnauthorizedException;
import com.streetconnect.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final StreetService streetService;

    @Autowired
    public AnnouncementService(AnnouncementRepository announcementRepository, StreetService streetService) {
        this.announcementRepository = announcementRepository;
        this.streetService = streetService;
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDto> getAnnouncementsForStreet(Long streetId) {
        return announcementRepository.findActiveByStreetOrGlobal(streetId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDto> getAllAnnouncements() {
        return announcementRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AnnouncementDto createAnnouncement(AnnouncementDto dto, User currentUser) {
        if (currentUser.getRole() != Role.ROLE_MODERATOR && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Only street moderators and admins can publish announcements");
        }

        Street street = null;
        if (dto.getStreetId() != null) {
            street = streetService.getStreetEntityById(dto.getStreetId());
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle().trim());
        announcement.setContent(dto.getContent().trim());
        announcement.setStreet(street);
        announcement.setCreatedByUser(currentUser);
        announcement.setPriority(dto.getPriority() != null ? dto.getPriority() : "NORMAL");
        announcement.setIsActive(true);

        Announcement saved = announcementRepository.save(announcement);
        return convertToDto(saved);
    }

    @Transactional
    public void deleteAnnouncement(Long id, User currentUser) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found with id: " + id));

        if (currentUser.getRole() != Role.ROLE_ADMIN && !announcement.getCreatedByUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this announcement");
        }

        announcementRepository.delete(announcement);
    }

    public AnnouncementDto convertToDto(Announcement a) {
        if (a == null) return null;

        AnnouncementDto dto = new AnnouncementDto();
        dto.setId(a.getId());
        if (a.getStreet() != null) {
            dto.setStreetId(a.getStreet().getId());
            dto.setStreetName(a.getStreet().getName());
        }
        if (a.getCreatedByUser() != null) {
            dto.setCreatedByName(a.getCreatedByUser().getFullName());
            dto.setCreatedByRole(a.getCreatedByUser().getRole().name());
        }
        dto.setTitle(a.getTitle());
        dto.setContent(a.getContent());
        dto.setPriority(a.getPriority());
        dto.setIsActive(a.getIsActive());
        dto.setCreatedAt(a.getCreatedAt());

        return dto;
    }
}
