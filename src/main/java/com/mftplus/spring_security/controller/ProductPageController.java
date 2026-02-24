package com.mftplus.spring_security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class ProductPageController {

    @GetMapping
    public String showProductPage() {
        return "product/list"; // نام فایل Thymeleaf (بدون html)
    }
}