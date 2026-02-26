package com.mftplus.spring_security.home.mapper;

import com.mftplus.spring_security.home.dto.HomeDto;
import com.mftplus.spring_security.home.model.entity.Home;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface HomeMapper {

    // ⚠️ mapping اضافه شد
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", ignore = true) // پر می‌شود در service
    HomeDto toDto(Home home);

    // ⚠️ mapping اضافه شد
    @Mapping(target = "user", ignore = true) // Set می‌شود در service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Home toEntity(HomeDto dto);

    // ⚠️ mapping اضافه شد
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(HomeDto dto, @MappingTarget Home home);
}