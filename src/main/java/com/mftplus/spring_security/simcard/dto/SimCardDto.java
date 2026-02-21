package com.mftplus.spring_security.simcard.dto;

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
public class SimCardDto {
    
    private Long id;
    
    @Pattern(regexp = "^09[0-9]{9}$", message = "شماره سیم کارت معتبر نیست")
    private String phoneNumber;
    
    @Size(max = 20, message = "شماره سریال نمی‌تواند بیشتر از 20 کاراکتر باشد")
    private String serialNumber;
    
    @Size(max = 50, message = "اپراتور نمی‌تواند بیشتر از 50 کاراکتر باشد")
    private String operator;
    
    private Long ownerId;
    
    private String ownerUsername;
    
    private Boolean active;
}

