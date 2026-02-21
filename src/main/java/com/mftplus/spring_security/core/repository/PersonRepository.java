package com.mftplus.spring_security.core.repository;

import com.mftplus.spring_security.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByNationalCode(String nationalCode);
    boolean existsByNationalCode(String nationalCode);
    List<Person> findByActiveTrue();
    List<Person> findByDepartmentId(Long departmentId);
    
    @Query("SELECT p FROM Person p WHERE p.active = true AND " +
           "(:firstName IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:nationalCode IS NULL OR p.nationalCode LIKE CONCAT('%', :nationalCode, '%'))")
    List<Person> searchPersons(@Param("firstName") String firstName, 
                               @Param("lastName") String lastName, 
                               @Param("nationalCode") String nationalCode);
}

