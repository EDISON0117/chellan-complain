package com.streetconnect.security;

import com.streetconnect.entity.Role;
import com.streetconnect.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String mobileNumber;
    private final String password;
    private final Role role;
    private final Long streetId;
    private final Boolean isActive;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String fullName, String email, String mobileNumber, String password,
                         Role role, Long streetId, Boolean isActive, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.role = role;
        this.streetId = streetId;
        this.isActive = isActive;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
        Long streetId = user.getStreet() != null ? user.getStreet().getId() : null;

        return new UserPrincipal(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getPassword(),
                user.getRole(),
                streetId,
                user.getIsActive(),
                Collections.singletonList(authority)
        );
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public Role getRole() {
        return role;
    }

    public Long getStreetId() {
        return streetId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive == null || isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive == null || isActive;
    }
}
