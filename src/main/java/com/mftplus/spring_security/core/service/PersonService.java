package com.mftplus.spring_security.core.service;

import com.mftplus.spring_security.core.dto.PersonDto;
import com.mftplus.spring_security.core.model.Department;
import com.mftplus.spring_security.core.model.Person;
import com.mftplus.spring_security.core.repository.DepartmentRepository;
import com.mftplus.spring_security.core.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {
    
    private final PersonRepository personRepository;
    private final DepartmentRepository departmentRepository;
    
    @Transactional(readOnly = true)
    public List<PersonDto> findAll() {
        return personRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PersonDto> findAllActive() {
        return personRepository.findByActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<PersonDto> findById(Long id) {
        return personRepository.findById(id)
                .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public List<PersonDto> search(String firstName, String lastName, String nationalCode) {
        return personRepository.searchPersons(firstName, lastName, nationalCode).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PersonDto save(PersonDto personDto) {
        Department department = departmentRepository.findById(personDto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("دپارتمان یافت نشد"));
        
        if (personRepository.existsByNationalCode(personDto.getNationalCode())) {
            throw new RuntimeException("کد ملی تکراری است");
        }
        
        Person person = convertToEntity(personDto);
        person.setDepartment(department);
        Person saved = personRepository.save(person);
        return convertToDto(saved);
    }
    
    @Transactional
    public PersonDto update(Long id, PersonDto personDto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("شخص یافت نشد"));
        
        Department department = departmentRepository.findById(personDto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("دپارتمان یافت نشد"));
        
        // Check national code uniqueness if changed
        if (!person.getNationalCode().equals(personDto.getNationalCode()) &&
            personRepository.existsByNationalCode(personDto.getNationalCode())) {
            throw new RuntimeException("کد ملی تکراری است");
        }
        
        person.setFirstName(personDto.getFirstName());
        person.setLastName(personDto.getLastName());
        person.setNationalCode(personDto.getNationalCode());
        person.setEmail(personDto.getEmail());
        person.setMobile(personDto.getMobile());
        person.setDepartment(department);
        person.setActive(personDto.getActive() != null ? personDto.getActive() : true);
        
        Person updated = personRepository.save(person);
        return convertToDto(updated);
    }
    
    @Transactional
    public void deleteById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("شخص یافت نشد"));
        
        if (person.getUser() != null) {
            throw new RuntimeException("نمی‌توان شخصی که کاربر دارد را حذف کرد");
        }
        
        personRepository.deleteById(id);
    }
    
    private PersonDto convertToDto(Person person) {
        PersonDto dto = new PersonDto();
        dto.setId(person.getId());
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setNationalCode(person.getNationalCode());
        dto.setEmail(person.getEmail());
        dto.setMobile(person.getMobile());
        dto.setDepartmentId(person.getDepartment().getId());
        dto.setDepartmentName(person.getDepartment().getName());
        dto.setActive(person.getActive());
        return dto;
    }
    
    private Person convertToEntity(PersonDto dto) {
        Person person = new Person();
        person.setFirstName(dto.getFirstName());
        person.setLastName(dto.getLastName());
        person.setNationalCode(dto.getNationalCode());
        person.setEmail(dto.getEmail());
        person.setMobile(dto.getMobile());
        person.setActive(dto.getActive() != null ? dto.getActive() : true);
        return person;
    }
}

