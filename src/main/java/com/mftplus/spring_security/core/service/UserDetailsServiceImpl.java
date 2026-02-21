package com.mftplus.spring_security.core.service;

import com.mftplus.spring_security.core.model.User;
import com.mftplus.spring_security.core.repository.UserRepository;
import com.mftplus.spring_security.core.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("کاربری با نام کاربری " + username + " یافت نشد"));

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new UsernameNotFoundException("حساب کاربری غیرفعال است");
        }

        return new SecurityUser(user);
    }
}
