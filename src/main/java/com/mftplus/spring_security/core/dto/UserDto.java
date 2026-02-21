package com.mftplus.spring_security.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "نام کاربری الزامی است")
    @Size(min = 3, max = 50, message = "نام کاربری باید بین 3 تا 50 کاراکتر باشد")
    private String username;

    @Size(min = 6, message = "رمز عبور باید حداقل 6 کاراکتر باشد")
    private String password; // optional on update

    @Email(message = "ایمیل معتبر نیست")
    @Size(max = 100, message = "ایمیل نمی‌تواند بیشتر از 100 کاراکتر باشد")
    private String email;

    private Long personId;
    private String personFullName;

    private Set<String> roleNames; // e.g. ADMIN, USER, MANAGER

    private Boolean enabled;
}
