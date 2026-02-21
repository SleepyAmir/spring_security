package com.mftplus.spring_security.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "نام دپارتمان الزامی است")
    @Size(min = 2, max = 100, message = "نام دپارتمان باید بین 2 تا 100 کاراکتر باشد")
    @Column(unique = true, nullable = false, length = 100)
    private String name;
    
    @Size(max = 500, message = "توضیحات نمی‌تواند بیشتر از 500 کاراکتر باشد")
    @Column(length = 500)
    private String description;
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Person> persons = new HashSet<>();
    
    @Column(nullable = false)
    private Boolean active = true;
    
    public Department(String name, String description) {
        this.name = name;
        this.description = description;
        this.active = true;
    }
}

