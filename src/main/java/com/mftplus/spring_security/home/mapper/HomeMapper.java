package com.mftplus.spring_security.home.mapper;

import com.mftplus.spring_security.home.dto.HomeDto;
import com.mftplus.spring_security.home.model.entity.Home;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface HomeMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", source = "user.fullName")
    HomeDto toDto(Home home);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Home toEntity(HomeDto dto);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(HomeDto dto, @MappingTarget Home home);
}