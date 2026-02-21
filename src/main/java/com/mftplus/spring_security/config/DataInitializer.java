package com.mftplus.spring_security.config;

import com.mftplus.spring_security.core.model.*;
import com.mftplus.spring_security.core.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        initializeRolesAndPermissions();
        initializeAdminUser();
        initializeDepartments();
    }
    
    private void initializeRolesAndPermissions() {
        // Create Permissions
        Permission personRead = createPermissionIfNotExists("PERSON_READ", "خواندن اطلاعات اشخاص");
        Permission personWrite = createPermissionIfNotExists("PERSON_WRITE", "نوشتن اطلاعات اشخاص");
        Permission personDelete = createPermissionIfNotExists("PERSON_DELETE", "حذف اشخاص");
        Permission simcardRead = createPermissionIfNotExists("SIMCARD_READ", "خواندن اطلاعات سیم کارت");
        Permission simcardWrite = createPermissionIfNotExists("SIMCARD_WRITE", "نوشتن اطلاعات سیم کارت");
        Permission simcardDelete = createPermissionIfNotExists("SIMCARD_DELETE", "حذف سیم کارت");
        Permission adminAccess = createPermissionIfNotExists("ADMIN_ACCESS", "دسترسی مدیریتی");
        
        // Create Roles
        Role adminRole = createRoleIfNotExists(Role.RoleType.ADMIN, "مدیر سیستم");
        Role managerRole = createRoleIfNotExists(Role.RoleType.MANAGER, "مدیر");
        Role userRole = createRoleIfNotExists(Role.RoleType.USER, "کاربر عادی");
        
        // Assign Permissions to Roles
        if (adminRole.getPermissions().isEmpty()) {
            adminRole.getPermissions().addAll(Set.of(
                personRead, personWrite, personDelete,
                simcardRead, simcardWrite, simcardDelete,
                adminAccess
            ));
            roleRepository.save(adminRole);
        }
        
        if (managerRole.getPermissions().isEmpty()) {
            managerRole.getPermissions().addAll(Set.of(
                personRead, personWrite,
                simcardRead, simcardWrite
            ));
            roleRepository.save(managerRole);
        }
        
        if (userRole.getPermissions().isEmpty()) {
            userRole.getPermissions().addAll(Set.of(
                personRead,
                simcardRead, simcardWrite, simcardDelete
            ));
            roleRepository.save(userRole);
        }
        
        log.info("Roles and Permissions initialized");
    }
    
    private void initializeAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName(Role.RoleType.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEmail("admin@example.com");
            adminUser.setEnabled(true);
            adminUser.setAccountNonExpired(true);
            adminUser.setAccountNonLocked(true);
            adminUser.setCredentialsNonExpired(true);
            
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);
            
            userRepository.save(adminUser);
            log.info("Admin user created: admin/admin123");
        }
    }
    
    private void initializeDepartments() {
        if (departmentRepository.count() == 0) {
            Department itDept = new Department("فناوری اطلاعات", "دپارتمان فناوری اطلاعات");
            Department hrDept = new Department("منابع انسانی", "دپارتمان منابع انسانی");
            Department financeDept = new Department("مالی", "دپارتمان مالی");
            
            departmentRepository.save(itDept);
            departmentRepository.save(hrDept);
            departmentRepository.save(financeDept);
            
            log.info("Departments initialized");
        }
    }
    
    private Permission createPermissionIfNotExists(String name, String description) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    Permission permission = new Permission(name, description);
                    return permissionRepository.save(permission);
                });
    }
    
    private Role createRoleIfNotExists(Role.RoleType roleType, String description) {
        return roleRepository.findByName(roleType)
                .orElseGet(() -> {
                    Role role = new Role(roleType, description);
                    return roleRepository.save(role);
                });
    }
}

