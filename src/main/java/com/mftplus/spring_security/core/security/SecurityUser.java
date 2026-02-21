package com.mftplus.spring_security.core.security;

import com.mftplus.spring_security.core.model.Permission;
import com.mftplus.spring_security.core.model.Role;
import com.mftplus.spring_security.core.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Adapter that wraps {@link User} and implements {@link UserDetails}
 * for Spring Security authentication. Built in {@link com.mftplus.spring_security.core.service.UserDetailsServiceImpl}.
 */
public class SecurityUser implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final Set<GrantedAuthority> authorities;

    public SecurityUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = Boolean.TRUE.equals(user.getEnabled());
        this.accountNonExpired = Boolean.TRUE.equals(user.getAccountNonExpired());
        this.accountNonLocked = Boolean.TRUE.equals(user.getAccountNonLocked());
        this.credentialsNonExpired = Boolean.TRUE.equals(user.getCredentialsNonExpired());
        this.authorities = buildAuthorities(user);
    }

    private static Set<GrantedAuthority> buildAuthorities(User user) {
        Set<GrantedAuthority> set = new HashSet<>();
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                set.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
                if (role.getPermissions() != null) {
                    for (Permission p : role.getPermissions()) {
                        set.add(new SimpleGrantedAuthority(p.getName()));
                    }
                }
            }
        }
        return set;
    }

    public Long getId() {
        return id;
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
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
