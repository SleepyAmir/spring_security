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
            createBankAccount(admin, AccountType.CHECKING, new BigDecimal("5000000000")); // 50 میلیون
            createBankAccount(admin, AccountType.SAVINGS, new BigDecimal("1000000000000000")); // 100 میلیون

            // حساب‌های کاربر 1
            createBankAccount(user1, AccountType.CHECKING, new BigDecimal("30000000")); // 30 میلیون
            createBankAccount(user1, AccountType.SAVINGS, new BigDecimal("750000000000")); // 75 میلیون

            // حساب‌های کاربر 2
            createBankAccount(user2, AccountType.CHECKING, new BigDecimal("45000000")); // 45 میلیون
            createBankAccount(user2, AccountType.SAVINGS, new BigDecimal("12000000000000")); // 120 میلیون

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
            User user2 = userRepository.findByUsername("zahra.mohammadi")
                    .orElseThrow(() -> new RuntimeException("User 2 not found"));

            // محصولات قبلی (10 عدد)
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

            // ۱۰ محصول جدید
            createProduct("Smartphone", "Flagship smartphone with 5G support and 108MP camera.", 15000000.0, user2);
            createProduct("Smart Watch", "Fitness tracker with heart rate monitor and GPS.", 4500000.0, admin);
            createProduct("External SSD", "1TB portable SSD with USB 3.2 interface.", 3500000.0, user1);
            createProduct("Printer", "Wireless all-in-one printer with scanner and copier.", 5200000.0, user2);
            createProduct("Router", "Dual-band WiFi 6 router for high-speed internet.", 2800000.0, admin);
            createProduct("Power Bank", "20000mAh power bank with fast charging support.", 850000.0, user1);
            createProduct("Memory Card", "128GB microSD card for smartphones and cameras.", 450000.0, user2);
            createProduct("USB Hub", "7-port USB 3.0 hub with individual power switches.", 650000.0, admin);
            createProduct("Laptop Stand", "Adjustable aluminum laptop stand for desk.", 1200000.0, user1);
            createProduct("Microphone", "Professional USB microphone for podcasting and streaming.", 3800000.0, user2);

            log.info("✅ Products initialized (20 products total)");
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
            User user1 = userRepository.findByUsername("ali.ahmadi")
                    .orElseThrow(() -> new RuntimeException("User 1 not found"));

            // خانه‌های قبلی (7 عدد)
            Home home1 = Home.builder()
                    .address("Valiasr Street, Fifth Alley, Number 12")
                    .city("Tehran")
                    .state("Tehran")
                    .postalCode("1234567890")
                    .type(HomeType.APARTMENT)
                    .status(HomeStatus.AVAILABLE)
                    .area(120.0)
                    .numberOfRooms(3)
                    .floor(5)
                    .totalFloors(8)
                    .yearBuilt(2018)
                    .hasParking(true)
                    .hasElevator(true)
                    .hasStorage(true)
                    .hasBalcony(true)
                    .hasPool(false)
                    .hasGym(false)
                    .price(15000000000L)
                    .description("Brand new apartment in prime location of Valiasr.")
                    .user(admin)
                    .deleted(false)
                    .build();
            homeRepository.save(home1);

            Home home2 = Home.builder()
                    .address("Chalus Road, Sangadeh Village, Plot 5")
                    .city("Nowshahr")
                    .state("Mazandaran")
                    .postalCode("9876543210")
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
                    .description("Luxury villa with swimming pool and sea view.")
                    .user(admin)
                    .deleted(false)
                    .build();
            homeRepository.save(home2);

            Home home3 = Home.builder()
                    .address("Ahmad Abad Square, Golestan Alley, Unit 15")
                    .city("Isfahan")
                    .state("Isfahan")
                    .postalCode("8888888888")
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
                    .description("Modern duplex with gym and complete facilities.")
                    .user(user2)
                    .deleted(false)
                    .build();
            homeRepository.save(home3);

            Home home4 = Home.builder()
                    .address("Elahieh, Saadat Abad Tower, Floor 20")
                    .city("Tehran")
                    .state("Tehran")
                    .postalCode("1111111111")
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
                    .description("Luxury penthouse with 360 degree panoramic view.")
                    .user(user2)
                    .deleted(false)
                    .build();
            homeRepository.save(home4);

            Home home5 = Home.builder()
                    .address("Shahrak Gharb, Phase 1, Building 8")
                    .city("Tehran")
                    .state("Tehran")
                    .postalCode("2222222222")
                    .type(HomeType.STUDIO)
                    .status(HomeStatus.AVAILABLE)
                    .area(45.0)
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
                    .description("Compact studio apartment, ideal for single residents.")
                    .user(admin)
                    .deleted(false)
                    .build();
            homeRepository.save(home5);

            Home home6 = Home.builder()
                    .address("Lavasan, Green Valley Complex, Unit 23")
                    .city("Tehran")
                    .state("Tehran")
                    .postalCode("3333333333")
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
                    .description("Beautiful townhouse in peaceful mountain area.")
                    .user(admin)
                    .deleted(false)
                    .build();
            homeRepository.save(home6);

            Home home7 = Home.builder()
                    .address("Sattarkhan Street, Business Tower, Floor 12")
                    .city("Tehran")
                    .state("Tehran")
                    .postalCode("4444444444")
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
                    .description("Modern office space with great location for business.")
                    .user(user2)
                    .deleted(false)
                    .build();
            homeRepository.save(home7);

            // ۱۰ خانه جدید
            Home home8 = Home.builder()
                    .address("Vanak Square, Ghods Street, Building 45, Unit 8")
                    .city("Tehran")
                    .state("Tehran")
                    .postalCode("5555555555")
                    .type(HomeType.APARTMENT)
                    .status(HomeStatus.AVAILABLE)
                    .area(95.0)
                    .numberOfRooms(2)
                    .floor(4)
                    .totalFloors(7)
                    .yearBuilt(2020)
                    .hasParking(true)
                    .hasElevator(true)
                    .hasStorage(false)
                    .hasBalcony(true)
                    .hasPool(false)
                    .hasGym(false)
                    .price(12000000000L)
                    .description("Cozy 2-bedroom apartment in the heart of Tehran, close to metro.")
                    .user(user1)
                    .deleted(false)
                    .build();
            homeRepository.save(home8);

            Home home9 = Home.builder()
                    .address("Kish Island, Maryam Complex, Block A, Unit 12")
                    .city("Kish")
                    .state("Hormozgan")
                    .postalCode("6666666666")
                    .type(HomeType.APARTMENT)
                    .status(HomeStatus.AVAILABLE)
                    .area(75.0)
                    .numberOfRooms(2)
                    .floor(2)
                    .totalFloors(5)
                    .yearBuilt(2021)
                    .hasParking(false)
                    .hasElevator(true)
                    .hasStorage(false)
                    .hasBalcony(true)
                    .hasPool(true)
                    .hasGym(true)
                    .price(18000000000L)
                    .description("Beautiful apartment in Kish Island, with access to beach and resort facilities.")
                    .user(admin)
                    .deleted(false)
                    .build();
            homeRepository.save(home9);

            Home home10 = Home.builder()
                    .address("Mashhad, Ahmadabad Avenue, Corner of 12th Street")
                    .city("Mashhad")
                    .state("Khorasan Razavi")
                    .postalCode("7777777777")
                    .type(HomeType.APARTMENT)
                    .status(HomeStatus.AVAILABLE)
                    .area(110.0)
                    .numberOfRooms(3)
                    .floor(6)
                    .totalFloors(8)
                    .yearBuilt(2019)
                    .hasParking(true)
                    .hasElevator(true)
                    .hasStorage(true)
                    .hasBalcony(false)
                    .hasPool(false)
                    .hasGym(false)
                    .price(9000000000L)
                    .description("Spacious apartment near holy shrine, suitable for families.")
                    .user(user2)
                    .deleted(false)
                    .build();
            homeRepository.save(home10);

            Home home11 = Home.builder()
                    .address("Shiraz, Eram Street, Golha Complex")
                    .city("Shiraz")
                    .state("Fars")
                    .postalCode("8888888881")
                    .type(HomeType.DUPLEX)
                    .status(HomeStatus.AVAILABLE)
                    .area(160.0)
                    .numberOfRooms(4)
                    .floor(1)
                    .totalFloors(2)
                    .yearBuilt(2020)
                    .hasParking(true)
                    .hasElevator(false)
                    .hasStorage(true)
                    .hasBalcony(true)
                    .hasPool(false)
                    .hasGym(false)
                    .price(14000000000L)
                    .description("Lovely duplex with garden access in beautiful Shiraz.")
                    .user(admin)
                    .deleted(false)
                    .build();
            homeRepository.save(home11);

            Home home12 = Home.builder()
                    .address("Tabriz, El Goli Avenue, Luxury Tower, Floor 10")
                    .city("Tabriz")
                    .state("East Azerbaijan")
                    .postalCode("9999999999")
                    .type(HomeType.PENTHOUSE)
                    .status(HomeStatus.AVAILABLE)
                    .area(220.0)
                    .numberOfRooms(4)
                    .floor(10)
                    .totalFloors(12)
                    .yearBuilt(2022)
                    .hasParking(true)
                    .hasElevator(true)
                    .hasStorage(true)
                    .hasBalcony(true)
                    .hasPool(false)
                    .hasGym(true)
                    .price(22000000000L)
                    .description("Luxury penthouse with amazing view of El Goli lake.")
                    .user(user1)
                    .deleted(false)
                    .build();
            homeRepository.save(home12);

            Home home13 = Home.builder()
                    .address("Ramsar, Seaside Boulevard, Villa No. 7")
                    .city("Ramsar")
                    .state("Mazandaran")
                    .postalCode("1010101010")
                    .type(HomeType.VILLA)
                    .status(HomeStatus.AVAILABLE)
                    .area(320.0)
                    .numberOfRooms(6)
                    .floor(0)
                    .totalFloors(2)
                    .yearBuilt(2018)
                    .hasParking(true)
                    .hasElevator(false)
                    .hasStorage(true)
                    .hasBalcony(true)
                    .hasPool(true)
                    .hasGym(true)
                    .price(35000000000L)
                    .description("Spacious villa with private pool and direct access to the sea.")
                    .user(admin)
                    .deleted(false)
                    .build();
            homeRepository.save(home13);

            Home home14 = Home.builder()
                    .address("Karaj, Mehrshahr, Phase 3, Block 7")
                    .city("Karaj")
                    .state("Alborz")
                    .postalCode("1111111112")
                    .type(HomeType.APARTMENT)
                    .status(HomeStatus.SOLD)
                    .area(85.0)
                    .numberOfRooms(2)
                    .floor(3)
                    .totalFloors(6)
                    .yearBuilt(2017)
                    .hasParking(true)
                    .hasElevator(true)
                    .hasStorage(false)
                    .hasBalcony(true)
                    .hasPool(false)
                    .hasGym(false)
                    .price(7500000000L)
                    .description("Affordable apartment in good neighborhood, already sold for testing.")
                    .user(user2)
                    .deleted(false)
                    .build();
            homeRepository.save(home14);

            Home home15 = Home.builder()
                    .address("Qeshm Island, Zeytoon Complex, Unit 45")
                    .city("Qeshm")
                    .state("Hormozgan")
                    .postalCode("1212121212")
                    .type(HomeType.APARTMENT)
                    .status(HomeStatus.RESERVED)
                    .area(90.0)
                    .numberOfRooms(2)
                    .floor(1)
                    .totalFloors(4)
                    .yearBuilt(2021)
                    .hasParking(true)
                    .hasElevator(false)
                    .hasStorage(false)
                    .hasBalcony(true)
                    .hasPool(true)
                    .hasGym(false)
                    .price(15000000000L)
                    .description("Beautiful apartment in Qeshm, close to beach, currently reserved.")
                    .user(admin)
                    .deleted(false)
                    .build();
            homeRepository.save(home15);

            Home home16 = Home.builder()
                    .address("Hamedan, Abbasabad Square, Building 23")
                    .city("Hamedan")
                    .state("Hamedan")
                    .postalCode("1313131313")
                    .type(HomeType.STUDIO)
                    .status(HomeStatus.AVAILABLE)
                    .area(40.0)
                    .numberOfRooms(1)
                    .floor(2)
                    .totalFloors(3)
                    .yearBuilt(2016)
                    .hasParking(false)
                    .hasElevator(false)
                    .hasStorage(false)
                    .hasBalcony(false)
                    .hasPool(false)
                    .hasGym(false)
                    .price(2800000000L)
                    .description("Small studio apartment, budget-friendly option.")
                    .user(user1)
                    .deleted(false)
                    .build();
            homeRepository.save(home16);

            Home home17 = Home.builder()
                    .address("Bandar Abbas, Golshahr, Phase 2, Villa 12")
                    .city("Bandar Abbas")
                    .state("Hormozgan")
                    .postalCode("1414141414")
                    .type(HomeType.VILLA)
                    .status(HomeStatus.AVAILABLE)
                    .area(180.0)
                    .numberOfRooms(3)
                    .floor(0)
                    .totalFloors(1)
                    .yearBuilt(2019)
                    .hasParking(true)
                    .hasElevator(false)
                    .hasStorage(true)
                    .hasBalcony(true)
                    .hasPool(false)
                    .hasGym(false)
                    .price(12000000000L)
                    .description("Comfortable villa in southern Iran with large yard.")
                    .user(user2)
                    .deleted(false)
                    .build();
            homeRepository.save(home17);

            log.info("✅ Homes initialized (17 properties total)");
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