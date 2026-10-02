package com.streetconnect.service;

import com.streetconnect.dto.*;
import com.streetconnect.entity.Role;
import com.streetconnect.entity.Street;
import com.streetconnect.entity.User;
import com.streetconnect.exception.BadRequestException;
import com.streetconnect.exception.DuplicateResourceException;
import com.streetconnect.exception.ResourceNotFoundException;
import com.streetconnect.repository.StreetRepository;
import com.streetconnect.repository.UserRepository;
import com.streetconnect.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StreetRepository streetRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @Autowired
    public AuthService(UserRepository userRepository,
                       StreetRepository streetRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       UserService userService) {
        this.userRepository = userRepository;
        this.streetRepository = streetRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }

        // Validate unique email
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new DuplicateResourceException("An account with this email address already exists");
        }

        // Validate unique mobile
        if (userRepository.existsByMobileNumber(request.getMobileNumber().trim())) {
            throw new DuplicateResourceException("An account with this mobile number already exists");
        }

        // Handle street association
        Street street = null;
        if (request.getStreetId() != null) {
            street = streetRepository.findById(request.getStreetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Selected street not found"));
        } else if (request.getStreetName() != null && !request.getStreetName().trim().isEmpty()) {
            String name = request.getStreetName().trim();
            String area = request.getArea() != null ? request.getArea().trim() : "City Center";
            String city = request.getCity() != null ? request.getCity().trim() : "Chennai";
            String pincode = request.getPincode() != null ? request.getPincode().trim() : "600001";

            street = streetRepository.findByNameAndAreaAndCity(name, area, city)
                    .orElseGet(() -> {
                        Street newStreet = new Street();
                        newStreet.setName(name);
                        newStreet.setArea(area);
                        newStreet.setCity(city);
                        newStreet.setPincode(pincode);
                        return streetRepository.save(newStreet);
                    });
        }

        // Create new user
        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setMobileNumber(request.getMobileNumber().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_RESIDENT);
        user.setStreet(street);
        user.setProfileImage(request.getProfileImage());
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        // Auto authenticate after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication, false);
        return new AuthResponse(jwt, userService.convertToDto(savedUser));
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentifier().trim(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication, request.getRememberMe());

        User user = userRepository.findByEmailOrMobileNumber(request.getIdentifier().trim(), request.getIdentifier().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new AuthResponse(jwt, userService.convertToDto(user));
    }

    public String forgotPassword(String identifier) {
        User user = userRepository.findByEmailOrMobileNumber(identifier.trim(), identifier.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No account registered with " + identifier));

        // In production, an SMS or email token is sent.
        // For development / demo, we return a helpful instructions message.
        return "Password reset link has been dispatched to your registered contact (" + user.getEmail() + ").";
    }
}
