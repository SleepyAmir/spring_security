package com.mftplus.spring_security.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {
    
    private Long id;
    
    @NotBlank(message = "نام الزامی است")
    @Size(min = 2, max = 50, message = "نام باید بین 2 تا 50 کاراکتر باشد")
    private String firstName;
    
    @NotBlank(message = "نام خانوادگی الزامی است")
    @Size(min = 2, max = 50, message = "نام خانوادگی باید بین 2 تا 50 کاراکتر باشد")
    private String lastName;
    
    @NotBlank(message = "کد ملی الزامی است")
    @Pattern(regexp = "^[0-9]{10}$", message = "کد ملی باید 10 رقم باشد")
    private String nationalCode;
    
    @Email(message = "ایمیل معتبر نیست")
    @Size(max = 100, message = "ایمیل نمی‌تواند بیشتر از 100 کاراکتر باشد")
    private String email;
    
    @Pattern(regexp = "^09[0-9]{9}$", message = "شماره موبایل معتبر نیست")
    private String mobile;
    
    @NotNull(message = "دپارتمان الزامی است")
    private Long departmentId;
    
    private String departmentName;
    
    private Boolean active;
}

