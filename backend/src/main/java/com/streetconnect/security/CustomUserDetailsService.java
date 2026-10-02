package com.streetconnect.security;

import com.streetconnect.entity.User;
import com.streetconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Can be email or mobile number
        User user = userRepository.findByEmailOrMobileNumber(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email or mobile: " + identifier));

        return UserPrincipal.create(user);
    }
}
