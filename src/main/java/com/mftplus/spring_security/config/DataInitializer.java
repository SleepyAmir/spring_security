package com.mftplus.spring_security.config;

import com.mftplus.spring_security.bankAccount.model.entity.BankAccount;
import com.mftplus.spring_security.bankAccount.model.enums.AccountType;
import com.mftplus.spring_security.bankAccount.repository.BankAccountRepository;
import com.mftplus.spring_security.core.model.*;
import com.mftplus.spring_security.core.repository.*;
import com.mftplus.spring_security.home.model.entity.Home;
import com.mftplus.spring_security.home.model.enums.HomeStatus;
import com.mftplus.spring_security.home.model.enums.HomeType;
import com.mftplus.spring_security.home.repository.HomeRepository;
import com.mftplus.spring_security.product.model.Product;
import com.mftplus.spring_security.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.SecureRandom;
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
    private final BankAccountRepository bankAccountRepository;
    private final ProductRepository productRepository;
    private final HomeRepository homeRepository;

    private final SecureRandom random = new SecureRandom();

    @Override
    public void run(String... args) {
        initializeRolesAndPermissions();
        initializeDepartments();
        initializeAdminUser();
        initializeTestUsers();
        initializeBankAccounts();
        initializeProducts();
        initializeHomes();
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

        log.info("✅ Roles and Permissions initialized");
    }

    private void initializeDepartments() {
        if (departmentRepository.count() == 0) {
            Department itDept = new Department("فناوری اطلاعات", "دپارتمان فناوری اطلاعات");
            Department hrDept = new Department("منابع انسانی", "دپارتمان منابع انسانی");
            Department financeDept = new Department("مالی", "دپارتمان مالی");

            departmentRepository.save(itDept);
            departmentRepository.save(hrDept);
            departmentRepository.save(financeDept);

            log.info("✅ Departments initialized");
        }
    }

    private void initializeAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName(Role.RoleType.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            Department itDept = departmentRepository.findByName("فناوری اطلاعات")
                    .orElseThrow(() -> new RuntimeException("IT Department not found"));

            // ایجاد Person برای ادمین - با اعتبارسنجی صحیح
            Person adminPerson = new Person();
            adminPerson.setFirstName("مدیر");
            adminPerson.setLastName("سیستم");
            adminPerson.setNationalCode("1234567890"); // 10 رقم
            adminPerson.setEmail("admin@example.com"); // ایمیل معتبر
            adminPerson.setMobile("09123456789"); // فرمت 09xxxxxxxxx
            adminPerson.setDepartment(itDept);
            adminPerson.setActive(true);
            adminPerson = personRepository.save(adminPerson);

            // ایجاد User
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEmail("admin@example.com");
            adminUser.setEnabled(true);
            adminUser.setAccountNonExpired(true);
            adminUser.setAccountNonLocked(true);
            adminUser.setCredentialsNonExpired(true);
            adminUser.setPerson(adminPerson);

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);

            userRepository.save(adminUser);
            log.info("✅ Admin user created: admin/admin123");
        }
    }

    private void initializeTestUsers() {
        Role userRole = roleRepository.findByName(Role.RoleType.USER)
                .orElseThrow(() -> new RuntimeException("User role not found"));

        Department hrDept = departmentRepository.findByName("منابع انسانی")
                .orElseThrow(() -> new RuntimeException("HR Department not found"));

        Department financeDept = departmentRepository.findByName("مالی")
                .orElseThrow(() -> new RuntimeException("Finance Department not found"));

        // کاربر تست 1 - علی احمدی
        if (userRepository.findByUsername("ali.ahmadi").isEmpty()) {
            Person person1 = new Person();
            person1.setFirstName("علی");
            person1.setLastName("احمدی");
            person1.setNationalCode("9876543210"); // 10 رقم
            person1.setEmail("ali.ahmadi@example.com");
            person1.setMobile("09121111111"); // فرمت 09xxxxxxxxx
            person1.setDepartment(hrDept);
            person1.setActive(true);
            person1 = personRepository.save(person1);

            User user1 = new User();
            user1.setUsername("ali.ahmadi");
            user1.setPassword(passwordEncoder.encode("123456"));
            user1.setEmail("ali.ahmadi@example.com");
            user1.setEnabled(true);
            user1.setAccountNonExpired(true);
            user1.setAccountNonLocked(true);
            user1.setCredentialsNonExpired(true);
            user1.setPerson(person1);
            user1.setRoles(Set.of(userRole));

            userRepository.save(user1);
            log.info("✅ Test user 1 created: ali.ahmadi/123456");
        }

        // کاربر تست 2 - زهرا محمدی
        if (userRepository.findByUsername("zahra.mohammadi").isEmpty()) {
            Person person2 = new Person();
            person2.setFirstName("زهرا");
            person2.setLastName("محمدی");
            person2.setNationalCode("5555555555"); // 10 رقم
            person2.setEmail("zahra.mohammadi@example.com");
            person2.setMobile("09122222222"); // فرمت 09xxxxxxxxx
            person2.setDepartment(financeDept);
            person2.setActive(true);
            person2 = personRepository.save(person2);

            User user2 = new User();
            user2.setUsername("zahra.mohammadi");
            user2.setPassword(passwordEncoder.encode("123456"));
            user2.setEmail("zahra.mohammadi@example.com");
            user2.setEnabled(true);
            user2.setAccountNonExpired(true);
            user2.setAccountNonLocked(true);
            user2.setCredentialsNonExpired(true);
            user2.setPerson(person2);
            user2.setRoles(Set.of(userRole));

            userRepository.save(user2);
            log.info("✅ Test user 2 created: zahra.mohammadi/123456");
        }
    }

    private void initializeBankAccounts() {
        if (bankAccountRepository.count() == 0) {
            User admin = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("Admin user not found"));
            User user1 = userRepository.findByUsername("ali.ahmadi")
                    .orElseThrow(() -> new RuntimeException("User 1 not found"));
            User user2 = userRepository.findByUsername("zahra.mohammadi")
                    .orElseThrow(() -> new RuntimeException("User 2 not found"));

            // حساب‌های ادمین
            createBankAccount(admin, AccountType.CHECKING, new BigDecimal("50000000")); // 50 میلیون
            createBankAccount(admin, AccountType.SAVINGS, new BigDecimal("100000000")); // 100 میلیون

            // حساب‌های کاربر 1
            createBankAccount(user1, AccountType.CHECKING, new BigDecimal("30000000")); // 30 میلیون
            createBankAccount(user1, AccountType.SAVINGS, new BigDecimal("75000000")); // 75 میلیون

            // حساب‌های کاربر 2
            createBankAccount(user2, AccountType.CHECKING, new BigDecimal("45000000")); // 45 میلیون
            createBankAccount(user2, AccountType.SAVINGS, new BigDecimal("120000000")); // 120 میلیون

            log.info("✅ Bank accounts initialized");
        }
    }

    private void createBankAccount(User user, AccountType type, BigDecimal balance) {
        BankAccount account = new BankAccount();
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(balance);
        account.setType(type);
        account.setUser(user);
        account.setDeleted(false);
        bankAccountRepository.save(account);
    }

    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder("6037");
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private void initializeProducts() {
        if (productRepository.count() == 0) {
            User admin = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("Admin user not found"));
            User user1 = userRepository.findByUsername("ali.ahmadi")
                    .orElseThrow(() -> new RuntimeException("User 1 not found"));

            // محصولات با اعتبارسنجی Pattern صحیح
            // Pattern: ^[a-zA-Z\s]{3,20}$ برای name
            // Pattern: ^[a-zA-Z0-9\s.,!?-]{3,255}$ برای description

            createProduct("Laptop", "High performance laptop for work and gaming.", 25000000.0, admin);
            createProduct("Mouse", "Wireless optical mouse with ergonomic design.", 500000.0, admin);
            createProduct("Keyboard", "Mechanical gaming keyboard with RGB lights.", 2500000.0, user1);
            createProduct("Monitor", "27 inch Full HD display with IPS panel.", 8000000.0, user1);
            createProduct("Headphones", "Noise cancelling wireless headphones.", 3500000.0, admin);
            createProduct("Webcam", "HD webcam for video conferencing.", 1200000.0, user1);
            createProduct("Speaker", "Bluetooth portable speaker with bass.", 1800000.0, admin);
            createProduct("Tablet", "10 inch Android tablet with stylus.", 12000000.0, user1);
            createProduct("Charger", "Fast charging USB-C power adapter.", 350000.0, admin);
            createProduct("Cable", "Premium USB-C to Lightning cable.", 250000.0, user1);

            log.info("✅ Products initialized");
        }
    }

    private void createProduct(String name, String description, Double price, User user) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setUser(user);
        product.setDeleted(false);
        productRepository.save(product);
    }

    private void initializeHomes() {
        if (homeRepository.count() == 0) {
            User admin = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("Admin user not found"));
            User user2 = userRepository.findByUsername("zahra.mohammadi")
                    .orElseThrow(() -> new RuntimeException("User 2 not found"));

            // خانه 1 - آپارتمان تهران
            // اعتبارسنجی: address (min=5, max=255), city (min=2, max=100), state (min=2, max=100)
            // postalCode: Pattern ^[0-9]{10}$, area: min=5.0 max=10000.0
            // numberOfRooms: min=1 max=50, floor: min=0 max=200, totalFloors: min=1 max=200
            // yearBuilt: min=1800, price: min=1
            Home home1 = Home.builder()
                    .address("Valiasr Street, Fifth Alley, Number 12") // حداقل 5 کاراکتر
                    .city("Tehran") // حداقل 2 کاراکتر
                    .state("Tehran") // حداقل 2 کاراکتر
                    .postalCode("1234567890") // دقیقاً 10 رقم
                    .type(HomeType.APARTMENT)
                    .status(HomeStatus.AVAILABLE)
                    .area(120.0) // بین 5 تا 10000
                    .numberOfRooms(3) // بین 1 تا 50
                    .floor(5) // بین 0 تا 200
                    .totalFloors(8) // بین 1 تا 200
                    .yearBuilt(2018) // بیشتر از 1800
                    .hasParking(true)
                    .hasElevator(true)
                    .hasStorage(true)
                    .hasBalcony(true)
                    .hasPool(false)
                    .hasGym(false)
                    .price(15000000000L) // حداقل 1
                    .description("Brand new apartment in prime location of Valiasr.") // حداکثر 1000 کاراکتر
                    .user(admin)
                    .deleted(false)
                    .build();
            homeRepository.save(home1);

            // خانه 2 - ویلا شمال
            Home home2 = Home.builder()
                    .address("Chalus Road, Sangadeh Village, Plot 5")
                    .city("Nowshahr")
                    .state("Mazandaran")
                    .postalCode("9876543210") // 10 رقم
                    .type(HomeType.VILLA)
                    .status(HomeStatus.AVAILABLE)
                    .area(250.0)
                    .numberOfRooms(5)
                    .floor(0)
                    .totalFloors(2)
                    .yearBuilt(2020)
                    .hasParking(true)
                    .hasElevator(false)
                    .hasStorage(true)
                    .hasBalcony(true)
                    .hasPool(true)
                    .hasGym(false)
                    .price(25000000000L)
                    .description("Luxury villa with swimming pool and sea view. Perfect for summer vacation.")
                    .user(admin)
                    .deleted(false)
                    .build();
            homeRepository.save(home2);

            // خانه 3 - دوبلکس اصفهان
            Home home3 = Home.builder()
                    .address("Ahmad Abad Square, Golestan Alley, Unit 15")
                    .city("Isfahan")
                    .state("Isfahan")
                    .postalCode("8888888888") // 10 رقم
                    .type(HomeType.DUPLEX)
                    .status(HomeStatus.AVAILABLE)
                    .area(180.0)
                    .numberOfRooms(4)
                    .floor(3)
                    .totalFloors(4)
                    .yearBuilt(2019)
                    .hasParking(true)
                    .hasElevator(true)
                    .hasStorage(true)
                    .hasBalcony(true)
                    .hasPool(false)
                    .hasGym(true)
                    .price(12000000000L)
                    .description("Modern duplex with gym and complete facilities in central Isfahan.")
                    .user(user2)
                    .deleted(false)
                    .build();
            homeRepository.save(home3);

            // خانه 4 - پنت‌هاوس تهران (RESERVED)
            Home home4 = Home.builder()
                    .address("Elahieh, Saadat Abad Tower, Floor 20")
                    .city("Tehran")
                    .state("Tehran")
                    .postalCode("1111111111") // 10 رقم
                    .type(HomeType.PENTHOUSE)
                    .status(HomeStatus.RESERVED)
                    .area(300.0)
                    .numberOfRooms(4)
                    .floor(20)
                    .totalFloors(20)
                    .yearBuilt(2021)
                    .hasParking(true)
                    .hasElevator(true)
                    .hasStorage(true)
                    .hasBalcony(true)
                    .hasPool(true)
                    .hasGym(true)
                    .price(50000000000L)
                    .description("Luxury penthouse with 360 degree panoramic view. Swimming pool and gym included.")
                    .user(user2)
                    .deleted(false)
                    .build();
            homeRepository.save(home4);

            // خانه 5 - استودیو کوچک (ارزان برای تست خرید)
            Home home5 = Home.builder()
                    .address("Shahrak Gharb, Phase 1, Building 8")
                    .city("Tehran")
                    .state("Tehran")
                    .postalCode("2222222222") // 10 رقم
                    .type(HomeType.STUDIO)
                    .status(HomeStatus.AVAILABLE)
                    .area(45.0) // حداقل 5
                    .numberOfRooms(1)
                    .floor(2)
                    .totalFloors(5)
                    .yearBuilt(2015)
                    .hasParking(false)
                    .hasElevator(false)
                    .hasStorage(false)
                    .hasBalcony(false)
                    .hasPool(false)
                    .hasGym(false)
                    .price(3000000000L)
                    .description("Compact studio apartment, ideal for single residents. Good for testing purchase.")
                    .user(admin)
                    .deleted(false)
                    .build();
            homeRepository.save(home5);

            // خانه 6 - تاون‌هاوس
            Home home6 = Home.builder()
                    .address("Lavasan, Green Valley Complex, Unit 23")
                    .city("Tehran")
                    .state("Tehran")
                    .postalCode("3333333333") // 10 رقم
                    .type(HomeType.TOWNHOUSE)
                    .status(HomeStatus.AVAILABLE)
                    .area(200.0)
                    .numberOfRooms(4)
                    .floor(0)
                    .totalFloors(3)
                    .yearBuilt(2022)
                    .hasParking(true)
                    .hasElevator(false)
                    .hasStorage(true)
                    .hasBalcony(true)
                    .hasPool(false)
                    .hasGym(false)
                    .price(18000000000L)
                    .description("Beautiful townhouse in peaceful mountain area with modern design and green space.")
                    .user(admin)
                    .deleted(false)
                    .build();
            homeRepository.save(home6);

            // خانه 7 - دفتر کاری (OFFICE)
            Home home7 = Home.builder()
                    .address("Sattarkhan Street, Business Tower, Floor 12")
                    .city("Tehran")
                    .state("Tehran")
                    .postalCode("4444444444") // 10 رقم
                    .type(HomeType.OFFICE)
                    .status(HomeStatus.AVAILABLE)
                    .area(85.0)
                    .numberOfRooms(3)
                    .floor(12)
                    .totalFloors(15)
                    .yearBuilt(2017)
                    .hasParking(true)
                    .hasElevator(true)
                    .hasStorage(true)
                    .hasBalcony(false)
                    .hasPool(false)
                    .hasGym(false)
                    .price(6000000000L)
                    .description("Modern office space with great location for business. Includes meeting rooms.")
                    .user(user2)
                    .deleted(false)
                    .build();
            homeRepository.save(home7);

            log.info("✅ Homes initialized (7 properties)");
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