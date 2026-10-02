package com.streetconnect.service;

import com.streetconnect.dto.ChangePasswordRequest;
import com.streetconnect.dto.ProfileUpdateRequest;
import com.streetconnect.dto.UserDto;
import com.streetconnect.entity.Street;
import com.streetconnect.entity.User;
import com.streetconnect.exception.BadRequestException;
import com.streetconnect.exception.ResourceNotFoundException;
import com.streetconnect.repository.UserRepository;
import com.streetconnect.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final StreetService streetService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       StreetService streetService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.streetService = streetService;
        this.passwordEncoder = passwordEncoder;
    }

    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        if (authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            return userRepository.findById(principal.getId()).orElse(null);
        }

        return null;
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByStreet(Long streetId) {
        return userRepository.findByStreetId(streetId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateProfile(User currentUser, ProfileUpdateRequest request) {
        currentUser.setFullName(request.getFullName().trim());

        if (request.getStreetId() != null) {
            Street street = streetService.getStreetEntityById(request.getStreetId());
            currentUser.setStreet(street);
        }

        if (request.getProfileImage() != null && !request.getProfileImage().trim().isEmpty()) {
            currentUser.setProfileImage(request.getProfileImage());
        }

        User saved = userRepository.save(currentUser);
        return convertToDto(saved);
    }

    @Transactional
    public void changePassword(User currentUser, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        if (passwordEncoder.matches(request.getNewPassword(), currentUser.getPassword())) {
            throw new BadRequestException("New password must be different from current password");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }

    public UserDto convertToDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setRole(user.getRole());
        dto.setProfileImage(user.getProfileImage());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getStreet() != null) {
            dto.setStreet(streetService.convertToDto(user.getStreet()));
        }

        return dto;
    }
}
