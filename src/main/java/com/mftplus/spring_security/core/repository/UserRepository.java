package com.mftplus.spring_security.core.repository;

import com.mftplus.spring_security.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByEnabledTrue();
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleType")
    List<User> findByRole(@Param("roleType") com.mftplus.spring_security.core.model.Role.RoleType roleType);
}

