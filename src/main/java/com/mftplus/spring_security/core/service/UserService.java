package com.mftplus.spring_security.core.service;

import com.mftplus.spring_security.core.dto.UserCreateDto;
import com.mftplus.spring_security.core.dto.UserDto;
import com.mftplus.spring_security.core.dto.UserUpdateDto;
import com.mftplus.spring_security.core.mapper.UserMapper;
import com.mftplus.spring_security.core.model.Person;
import com.mftplus.spring_security.core.model.Role;
import com.mftplus.spring_security.core.model.User;
import com.mftplus.spring_security.core.repository.PersonRepository;
import com.mftplus.spring_security.core.repository.RoleRepository;
import com.mftplus.spring_security.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PersonRepository personRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAllEnabled() {
        return userRepository.findByEnabledTrue().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto create(UserCreateDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("نام کاربری قبلاً استفاده شده است");
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("ایمیل قبلاً استفاده شده است");
        }
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(resolveRoles(dto.getRoleNames()));
        if (dto.getPersonId() != null) {
            Person person = personRepository.findById(dto.getPersonId())
                    .orElseThrow(() -> new IllegalArgumentException("شخص یافت نشد"));
            user.setPerson(person);
        }
        user.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Transactional
    public UserDto update(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("کاربر یافت نشد"));
        userMapper.updateEntityFromDto(dto, user);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getRoleNames() != null) {
            user.setRoles(resolveRoles(dto.getRoleNames()));
        }
        if (dto.getPersonId() != null) {
            Person person = personRepository.findById(dto.getPersonId())
                    .orElseThrow(() -> new IllegalArgumentException("شخص یافت نشد"));
            user.setPerson(person);
        }
        if (dto.getEnabled() != null) {
            user.setEnabled(dto.getEnabled());
        }
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("کاربر یافت نشد");
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findEntityById(Long id) {
        return userRepository.findById(id);
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) return new HashSet<>();
        Set<Role> roles = new HashSet<>();
        for (String name : roleNames) {
            Role.RoleType type = Role.RoleType.valueOf(name);
            roleRepository.findByName(type).ifPresent(roles::add);
        }
        return roles;
    }
}
