package com.mftplus.spring_security.home.service;

import com.mftplus.spring_security.home.dto.HomeDto;
import com.mftplus.spring_security.home.model.enums.HomeStatus;
import com.mftplus.spring_security.home.model.enums.HomeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HomeService {

    void    save(HomeDto dto);
    void    update(Long id, HomeDto dto);
    void    deleteById(Long id);
    void    restoreById(Long id);

    HomeDto         findById(Long id);
    Page<HomeDto>   findAll(Pageable pageable);
    List<HomeDto>   findAllByPersonId(Long personId);
    Page<HomeDto>   findAllByPersonId(Long personId, Pageable pageable);
    Page<HomeDto>   findAllByStatus(HomeStatus status, Pageable pageable);
    Page<HomeDto>   findAllDeleted(Pageable pageable);
    Page<HomeDto>   findAllByFilter(String city, String state, HomeStatus status, HomeType type, Pageable pageable);

    HomeDto markAsSold(Long id);
    HomeDto markAsAvailable(Long id);
    HomeDto markAsReserved(Long id);
}