package com.mftplus.spring_security.home.mapper;

import com.mftplus.spring_security.home.dto.HomeDto;
import com.mftplus.spring_security.home.model.entity.Home;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface HomeMapper {


    HomeDto toDto(Home home);

    Home toEntity(HomeDto dto);

    void updateFromDto(HomeDto dto, @MappingTarget Home home);
}