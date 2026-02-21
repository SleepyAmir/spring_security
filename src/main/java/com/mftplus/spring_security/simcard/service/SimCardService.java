package com.mftplus.spring_security.simcard.service;

import com.mftplus.spring_security.core.model.User;
import com.mftplus.spring_security.core.repository.UserRepository;
import com.mftplus.spring_security.simcard.dto.SimCardDto;
import com.mftplus.spring_security.simcard.model.SimCard;
import com.mftplus.spring_security.simcard.repository.SimCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimCardService {
    
    private final SimCardRepository simCardRepository;
    private final UserRepository userRepository;
    
    private static final int MAX_SIMCARDS_PER_USER = 10;
    
    @Transactional(readOnly = true)
    public List<SimCardDto> findAllByOwnerId(Long ownerId) {
        return simCardRepository.findByOwnerIdAndActiveTrue(ownerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<SimCardDto> findById(Long id) {
        return simCardRepository.findById(id)
                .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Optional<SimCardDto> findByIdAndOwnerId(Long id, Long ownerId) {
        return simCardRepository.findById(id)
                .filter(simCard -> simCard.getOwner() != null && simCard.getOwner().getId().equals(ownerId))
                .map(this::convertToDto);
    }
    
    @Transactional
    public SimCardDto save(SimCardDto simCardDto, Long ownerId) {
        if (simCardRepository.existsByPhoneNumber(simCardDto.getPhoneNumber())) {
            throw new RuntimeException("شماره سیم کارت تکراری است");
        }
        
        // Check max simcards limit
        long activeCount = simCardRepository.countActiveSimCardsByOwnerId(ownerId);
        if (activeCount >= MAX_SIMCARDS_PER_USER) {
            throw new RuntimeException("هر کاربر حداکثر می‌تواند " + MAX_SIMCARDS_PER_USER + " سیم کارت فعال داشته باشد");
        }
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));
        
        SimCard simCard = convertToEntity(simCardDto);
        simCard.setOwner(owner);
        SimCard saved = simCardRepository.save(simCard);
        return convertToDto(saved);
    }
    
    @Transactional
    public SimCardDto update(Long id, SimCardDto simCardDto, Long ownerId) {
        SimCard simCard = simCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("سیم کارت یافت نشد"));
        
        // Check ownership
        if (simCard.getOwner() == null || !simCard.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("شما اجازه ویرایش این سیم کارت را ندارید");
        }
        
        // Check phone number uniqueness if changed
        if (!simCard.getPhoneNumber().equals(simCardDto.getPhoneNumber()) &&
            simCardRepository.existsByPhoneNumber(simCardDto.getPhoneNumber())) {
            throw new RuntimeException("شماره سیم کارت تکراری است");
        }
        
        simCard.setPhoneNumber(simCardDto.getPhoneNumber());
        simCard.setSerialNumber(simCardDto.getSerialNumber());
        simCard.setOperator(simCardDto.getOperator());
        simCard.setActive(simCardDto.getActive() != null ? simCardDto.getActive() : true);
        
        SimCard updated = simCardRepository.save(simCard);
        return convertToDto(updated);
    }
    
    @Transactional
    public void deleteById(Long id, Long ownerId) {
        SimCard simCard = simCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("سیم کارت یافت نشد"));
        
        // Check ownership
        if (simCard.getOwner() == null || !simCard.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("شما اجازه حذف این سیم کارت را ندارید");
        }
        
        simCardRepository.deleteById(id);
    }
    
    private SimCardDto convertToDto(SimCard simCard) {
        SimCardDto dto = new SimCardDto();
        dto.setId(simCard.getId());
        dto.setPhoneNumber(simCard.getPhoneNumber());
        dto.setSerialNumber(simCard.getSerialNumber());
        dto.setOperator(simCard.getOperator());
        dto.setOwnerId(simCard.getOwner() != null ? simCard.getOwner().getId() : null);
        dto.setOwnerUsername(simCard.getOwner() != null ? simCard.getOwner().getUsername() : null);
        dto.setActive(simCard.getActive());
        return dto;
    }
    
    private SimCard convertToEntity(SimCardDto dto) {
        SimCard simCard = new SimCard();
        simCard.setPhoneNumber(dto.getPhoneNumber());
        simCard.setSerialNumber(dto.getSerialNumber());
        simCard.setOperator(dto.getOperator());
        simCard.setActive(dto.getActive() != null ? dto.getActive() : true);
        return simCard;
    }
}

