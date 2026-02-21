# سیستم مدیریت با Spring Security

این پروژه یک سیستم مدیریت کامل با Spring Boot 3.5.11 و Spring Security 6 است که شامل مدیریت اشخاص، دپارتمان‌ها، کاربران، نقش‌ها، دسترسی‌ها و سیم کارت‌ها می‌باشد.

## 📋 فهرست مطالب

- [ویژگی‌ها](#ویژگی‌ها)
- [تکنولوژی‌های استفاده شده](#تکنولوژی‌های-استفاده-شده)
- [ساختار پروژه](#ساختار-پروژه)
- [نصب و راه‌اندازی](#نصب-و-راه‌اندازی)
- [پیکربندی دیتابیس](#پیکربندی-دیتابیس)
- [امنیت](#امنیت)
- [معماری](#معماری)
- [API و صفحات](#api-و-صفحات)
- [نقش‌ها و دسترسی‌ها](#نقش‌ها-و-دسترسی‌ها)

## ✨ ویژگی‌ها

- ✅ احراز هویت و مدیریت دسترسی کامل با Spring Security 6
- ✅ رمزگذاری رمز عبور با BCrypt
- ✅ مدیریت نقش‌ها و دسترسی‌ها (RBAC)
- ✅ محافظت در برابر CSRF و XSS
- ✅ مدیریت Session و Logout امن
- ✅ CRUD کامل برای اشخاص
- ✅ مدیریت سیم کارت‌ها (هر کاربر حداکثر 10 سیم کارت)
- ✅ کش کردن دپارتمان‌ها با H2
- ✅ Logging با Aspect-Oriented Programming
- ✅ مدیریت خطا با GlobalExceptionHandler
- ✅ رابط کاربری ساده و کاربرپسند با Thymeleaf و Bootstrap 5

## 🛠 تکنولوژی‌های استفاده شده

- **Spring Boot**: 3.5.11
- **Java**: JDK 17
- **Build Tool**: Maven
- **Database**: MySQL (اصلی) و H2 (برای کش)
- **Security**: Spring Security 6
- **Template Engine**: Thymeleaf
- **UI Framework**: Bootstrap 5
- **ORM**: JPA/Hibernate
- **Logging**: SLF4J + Logback
- **AOP**: Spring AOP برای Logging

## 📁 ساختار پروژه

```
spring_security/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/mftplus/spring_security/
│   │   │       ├── aspect/              # Aspect برای Logging
│   │   │       ├── config/              # پیکربندی‌ها
│   │   │       ├── controller/          # Controller های UI
│   │   │       ├── core/                # ماژول اصلی
│   │   │       │   ├── dto/             # Data Transfer Objects
│   │   │       │   ├── model/           # Entity ها
│   │   │       │   ├── repository/      # Repository ها
│   │   │       │   └── service/         # Service ها
│   │   │       ├── exception/           # Exception Handler
│   │   │       ├── simcard/             # ماژول سیم کارت
│   │   │       │   ├── dto/
│   │   │       │   ├── model/
│   │   │       │   ├── repository/
│   │   │       │   └── service/
│   │   │       └── SpringSecurityApplication.java
│   │   └── resources/
│   │       ├── templates/               # Thymeleaf Templates
│   │       │   ├── layout.html          # Layout مشترک
│   │       │   ├── login.html
│   │       │   ├── dashboard.html
│   │       │   ├── person/              # صفحات Person
│   │       │   └── simcard/             # صفحات SimCard
│   │       └── application.yaml         # پیکربندی
│   └── test/
└── pom.xml
```

## 🚀 نصب و راه‌اندازی

### پیش‌نیازها

- JDK 17 یا بالاتر
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### مراحل نصب

1. **ایجاد دیتابیس MySQL**:
```sql
CREATE DATABASE mft CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **پیکربندی دیتابیس**:
اطمینان حاصل کنید که MySQL در حال اجرا است و اطلاعات اتصال در `application.yaml` صحیح است.

3. **اجرای پروژه**:
```bash
mvn clean install
mvn spring-boot:run
```

4. **دسترسی به برنامه**:
- URL: http://localhost:8080
- نام کاربری پیش‌فرض: `admin`
- رمز عبور پیش‌فرض: `admin123`

## 🗄 پیکربندی دیتابیس

### MySQL (دیتابیس اصلی)

پروژه از MySQL به عنوان دیتابیس اصلی استفاده می‌کند. تمام Entity ها در این دیتابیس ذخیره می‌شوند.

**تنظیمات در `application.yaml`**:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mft?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: root
    password: root123
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### H2 (برای کش)

H2 برای کش کردن دیتابیس Department استفاده می‌شود. کنسول H2 در آدرس `/h2-console` در دسترس است.

## 🔒 امنیت

### پیکربندی Spring Security

پیکربندی امنیت در کلاس `SecurityConfig` انجام شده است.

#### ویژگی‌های امنیتی پیاده‌سازی شده:

1. **رمزگذاری رمز عبور**:
   - استفاده از BCrypt با strength 12
   - تمام رمزهای عبور به صورت hash ذخیره می‌شوند
   - پیکربندی در `PasswordEncoderConfig`

2. **CSRF Protection**:
   - استفاده از CookieCsrfTokenRepository
   - تمام فرم‌ها باید CSRF token داشته باشند
   - H2 console از CSRF معاف است

3. **XSS Protection**:
   - Content Security Policy (CSP)
   - X-XSS-Protection header
   - X-Content-Type-Options header
   - X-Frame-Options header

4. **Session Management**:
   - حداکثر 1 session همزمان برای هر کاربر
   - Timeout: 30 دقیقه
   - پاک کردن Session و Cookies هنگام Logout
   - HttpOnly cookies

5. **Authentication**:
   - فرم Login سفارشی در `/login`
   - استفاده از UserDetailsService
   - مدیریت خطاهای احراز هویت
   - Redirect به dashboard پس از ورود موفق

6. **Authorization**:
   - Role-Based Access Control (RBAC)
   - Permission-Based Access Control
   - دسترسی‌های مبتنی بر نقش
   - هر کاربر فقط سیم کارت‌های خود را می‌بیند

7. **Logout**:
   - URL: `/logout`
   - پاک کردن Session
   - پاک کردن Cookies (JSESSIONID, XSRF-TOKEN)
   - پاک کردن Authentication
   - Redirect به صفحه Login

### نقش‌ها (Roles)

سه نقش پیش‌فرض در سیستم تعریف شده است:

1. **ADMIN** (مدیر سیستم):
   - دسترسی کامل به تمام بخش‌ها
   - می‌تواند اشخاص را ایجاد، ویرایش و حذف کند
   - می‌تواند سیم کارت‌ها را مدیریت کند
   - دسترسی به `/admin/**`

2. **MANAGER** (مدیر):
   - می‌تواند اشخاص را مشاهده و ایجاد کند
   - می‌تواند سیم کارت‌ها را مشاهده و ایجاد کند
   - نمی‌تواند حذف کند
   - دسترسی به `/manager/**`

3. **USER** (کاربر عادی):
   - می‌تواند اشخاص را مشاهده کند
   - می‌تواند سیم کارت‌های خود را مدیریت کند
   - هر کاربر فقط سیم کارت‌های خود را می‌بیند

### دسترسی‌ها (Permissions)

- `PERSON_READ`: خواندن اطلاعات اشخاص
- `PERSON_WRITE`: نوشتن اطلاعات اشخاص
- `PERSON_DELETE`: حذف اشخاص
- `SIMCARD_READ`: خواندن اطلاعات سیم کارت
- `SIMCARD_WRITE`: نوشتن اطلاعات سیم کارت
- `SIMCARD_DELETE`: حذف سیم کارت
- `ADMIN_ACCESS`: دسترسی مدیریتی

## 🏗 معماری

### لایه Model (Entity)

#### ماژول Core:
- **User**: کاربران سیستم (پیاده‌سازی UserDetails)
- **Person**: اطلاعات اشخاص
- **Department**: دپارتمان‌ها (کش می‌شوند)
- **Role**: نقش‌ها (ADMIN, USER, MANAGER)
- **Permission**: دسترسی‌ها

#### ماژول SimCard:
- **SimCard**: اطلاعات سیم کارت‌ها
- هر سیم کارت به یک User متصل است
- هر کاربر حداکثر 10 سیم کارت فعال می‌تواند داشته باشد

### لایه Repository

تمام Repository ها از `JpaRepository` ارث‌بری می‌کنند و متدهای سفارشی برای جستجو دارند.

### لایه Service

- **PersonService**: منطق کسب‌وکار برای مدیریت اشخاص
- **DepartmentService**: مدیریت دپارتمان‌ها با کش (`@Cacheable`)
- **SimCardService**: مدیریت سیم کارت‌ها با محدودیت 10 عدد برای هر کاربر
- **UserDetailsServiceImpl**: سرویس احراز هویت

### لایه Controller

- **HomeController**: صفحه اصلی
- **LoginController**: صفحه ورود
- **DashboardController**: داشبورد
- **PersonController**: CRUD اشخاص
- **SimCardController**: CRUD سیم کارت‌ها (فقط سیم کارت‌های کاربر فعلی)
- **ErrorController**: مدیریت خطاها

### Aspect و Exception Handling

- **LoggingAspect**: لاگ تمام متدهای Service و Controller
  - لاگ ورود به متد
  - لاگ خروج از متد
  - لاگ خطاها
  - لاگ زمان اجرا
- **GlobalExceptionHandler**: مدیریت خطاهای سراسری
  - RuntimeException
  - IllegalArgumentException
  - MethodArgumentNotValidException
  - NoResourceFoundException (برای favicon)

## 📄 API و صفحات

### صفحات عمومی

- `/` - صفحه اصلی (redirect به login)
- `/login` - صفحه ورود
- `/error` - صفحه خطا
- `/access-denied` - صفحه دسترسی غیرمجاز

### صفحات احراز هویت شده

- `/dashboard` - داشبورد اصلی
- `/persons` - لیست اشخاص (با جستجو)
- `/persons/create` - افزودن شخص جدید
- `/persons/edit/{id}` - ویرایش شخص
- `/persons/view/{id}` - مشاهده شخص
- `/persons/delete/{id}` - حذف شخص
- `/simcards` - لیست سیم کارت‌های کاربر فعلی
- `/simcards/create` - افزودن سیم کارت جدید
- `/simcards/edit/{id}` - ویرایش سیم کارت
- `/simcards/delete/{id}` - حذف سیم کارت

## 🔧 پیکربندی‌های مهم

### Cache Configuration

دپارتمان‌ها در H2 کش می‌شوند:
```java
@Cacheable(value = "departments", key = "'all'")
public List<Department> findAll() { ... }
```

### Session Configuration

- حداکثر 1 session همزمان
- Timeout: 30 دقیقه
- پاک کردن Session و Cookies هنگام Logout

### Security Headers

- X-Content-Type-Options: nosniff
- X-Frame-Options: SAMEORIGIN
- X-XSS-Protection: 1; mode=block
- Content-Security-Policy

## 📝 لاگ‌ها

سیستم از Aspect برای لاگ کردن تمام متدهای Service و Controller استفاده می‌کند:

- لاگ ورود به متد با نام کاربر
- لاگ خروج از متد
- لاگ خطاها با جزئیات
- لاگ زمان اجرا

## 🧪 تست

برای اجرای تست‌ها:
```bash
mvn test
```

## 📚 منابع

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)

## 👤 نویسنده

این پروژه با استفاده از Spring Boot 3.5.11 و Spring Security 6 توسعه یافته است.

## 📄 مجوز

این پروژه برای استفاده آموزشی و تجاری آزاد است.
