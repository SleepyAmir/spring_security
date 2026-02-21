package com.mftplus.spring_security.core.repository;

import com.mftplus.spring_security.core.model.Role;
import com.mftplus.spring_security.core.model.Role.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
    boolean existsByName(RoleType name);
}

