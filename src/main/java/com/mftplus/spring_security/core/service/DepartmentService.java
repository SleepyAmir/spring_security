package com.mftplus.spring_security.core.service;

import com.mftplus.spring_security.core.model.Department;
import com.mftplus.spring_security.core.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    
    @Cacheable(value = "departments", key = "'all'")
    @Transactional(readOnly = true)
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }
    
    @Cacheable(value = "departments", key = "'active'")
    @Transactional(readOnly = true)
    public List<Department> findAllActive() {
        return departmentRepository.findByActiveTrue();
    }
    
    @Cacheable(value = "departments", key = "#id")
    @Transactional(readOnly = true)
    public Optional<Department> findById(Long id) {
        return departmentRepository.findById(id);
    }

    @CacheEvict(value = "departments", allEntries = true)
    @Transactional
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @CacheEvict(value = "departments", allEntries = true)
    @Transactional
    public Department update(Department department) {
        return departmentRepository.save(department);
    }

    @CacheEvict(value = "departments", allEntries = true)
    @Transactional
    public void deleteById(Long id) {
        departmentRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return departmentRepository.existsByName(name);
    }
}

