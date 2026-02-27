package com.mftplus.spring_security.home.service;

import com.mftplus.spring_security.core.model.User; // ⚠️ تغییر از Person
import com.mftplus.spring_security.core.repository.UserRepository; // ⚠️ تغییر از PersonRepository
import com.mftplus.spring_security.home.dto.HomeDto;
import com.mftplus.spring_security.home.exception.*;
import com.mftplus.spring_security.home.mapper.HomeMapper;
import com.mftplus.spring_security.home.model.entity.Home;
import com.mftplus.spring_security.home.model.enums.HomeStatus;
import com.mftplus.spring_security.home.model.enums.HomeType;
import com.mftplus.spring_security.home.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final HomeRepository homeRepository;
    private final UserRepository userRepository;
    private final HomeMapper homeMapper;

    @Override
    @Transactional
    public void save(HomeDto dto) {
        validate(dto);
        Home home = homeMapper.toEntity(dto);
        home.setUser(findOwner(dto.getUserId()));
        homeRepository.save(home);
        log.info("Home saved id={}", home.getId());
    }

    @Override
    @Transactional
    public void update(Long id, HomeDto dto) {
        Home home = findEntity(id);
        if (home.getStatus() == HomeStatus.SOLD) throw new HomeAlreadySoldException(id);
        validate(dto);
        if (!home.getUser().getId().equals(dto.getUserId()))
            home.setUser(findOwner(dto.getUserId()));
        homeMapper.updateFromDto(dto, home);
        homeRepository.save(home);
        log.info("Home updated id={}", id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!homeRepository.existsById(id)) throw new HomeNotFoundException(id);
        homeRepository.deleteById(id);
        log.info("Home deleted id={}", id);
    }

    @Override
    @Transactional
    public void restoreById(Long id) {
        homeRepository.restoreById(id);
        log.info("Home restored id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public HomeDto findById(Long id) {
        return homeMapper.toDto(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HomeDto> findAll(Pageable pageable) {
        return homeRepository.findAll(pageable).map(homeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomeDto> findAllByUserId(Long userId) {
        return homeRepository.findAllByUserId(userId).stream()
                .map(homeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HomeDto> findAllByUserId(Long userId, Pageable pageable) {
        return homeRepository.findAllByUserId(userId, pageable)
                .map(homeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HomeDto> findAllByStatus(HomeStatus status, Pageable pageable) {
        return homeRepository.findAllByStatus(status, pageable)
                .map(homeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HomeDto> findAllDeleted(Pageable pageable) {
        return homeRepository.findAllDeleted(pageable)
                .map(homeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HomeDto> findAllByFilter(String city, String state, HomeStatus status, HomeType type, Pageable pageable) {
        return homeRepository.findAllByFilter(city, state, status, type, pageable)
                .map(homeMapper::toDto);
    }

    @Override
    @Transactional
    public HomeDto markAsSold(Long id) {
        Home home = findEntity(id);
        if (home.getStatus() == HomeStatus.SOLD) throw new HomeAlreadySoldException(id);
        if (home.getStatus() != HomeStatus.AVAILABLE && home.getStatus() != HomeStatus.RESERVED)
            throw new HomeNotAvailableException(id, home.getStatus());
        home.setStatus(HomeStatus.SOLD);
        return homeMapper.toDto(homeRepository.save(home));
    }

    @Override
    @Transactional
    public HomeDto markAsAvailable(Long id) {
        Home home = findEntity(id);
        if (home.getStatus() == HomeStatus.SOLD) throw new HomeAlreadySoldException(id);
        home.setStatus(HomeStatus.AVAILABLE);
        return homeMapper.toDto(homeRepository.save(home));
    }

    @Override
    @Transactional
    public HomeDto markAsReserved(Long id) {
        Home home = findEntity(id);
        if (home.getStatus() == HomeStatus.SOLD) throw new HomeAlreadySoldException(id);
        if (home.getStatus() != HomeStatus.AVAILABLE)
            throw new HomeNotAvailableException(id, home.getStatus());
        home.setStatus(HomeStatus.RESERVED);
        return homeMapper.toDto(homeRepository.save(home));
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    private Home findEntity(Long id) {
        return homeRepository.findById(id).orElseThrow(() -> new HomeNotFoundException(id));
    }

    private User findOwner(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new HomeOwnerNotFoundException(userId));
    }

    private void validate(HomeDto dto) {
        if (dto.getFloor() != null && dto.getTotalFloors() != null
                && dto.getFloor() > dto.getTotalFloors())
            throw new HomeValidationException("floor",
                    "Floor (" + dto.getFloor() + ") cannot exceed total floors (" + dto.getTotalFloors() + ")");

        if (dto.getYearBuilt() != null && dto.getYearBuilt() > LocalDate.now().getYear())
            throw new HomeValidationException("yearBuilt",
                    "Year built (" + dto.getYearBuilt() + ") cannot be in the future");

        if (Boolean.TRUE.equals(dto.getHasElevator())
                && dto.getTotalFloors() != null && dto.getTotalFloors() == 1)
            throw new HomeValidationException("hasElevator",
                    "A single-floor building cannot have an elevator");
    }
}