package com.mftplus.spring_security.core.mapper;

import com.mftplus.spring_security.core.dto.UserCreateDto;
import com.mftplus.spring_security.core.dto.UserDto;
import com.mftplus.spring_security.core.dto.UserUpdateDto;
import com.mftplus.spring_security.core.model.Role;
import com.mftplus.spring_security.core.model.User;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "personId", source = "person.id")
    @Mapping(target = "personFullName", expression = "java(personFullName(user))")
    @Mapping(target = "roleNames", expression = "java(roleNames(user))")
    @Mapping(target = "password", ignore = true) // never expose password in DTO response
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "password")
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", defaultExpression = "java(true)")
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    User toEntity(UserCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true) // service sets if present
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    void updateEntityFromDto(UserUpdateDto dto, @MappingTarget User user);

    default String personFullName(User user) {
        if (user == null || user.getPerson() == null) return null;
        return user.getPerson().getFirstName() + " " + user.getPerson().getLastName();
    }

    default Set<String> roleNames(User user) {
        if (user == null || user.getRoles() == null) return Set.of();
        return user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());
    }
}
