package com.mftplus.spring_security.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @Size(min = 6, message = "رمز عبور باید حداقل 6 کاراکتر باشد")
    private String password; // optional; if empty, keep current

    @Email(message = "ایمیل معتبر نیست")
    @Size(max = 100, message = "ایمیل نمی‌تواند بیشتر از 100 کاراکتر باشد")
    private String email;

    private Long personId;

    private Set<String> roleNames;

    private Boolean enabled;
}
