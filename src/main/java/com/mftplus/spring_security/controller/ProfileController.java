package com.mftplus.spring_security.controller;

import com.mftplus.spring_security.bankAccount.dto.BankAccountDto;
import com.mftplus.spring_security.bankAccount.service.BankAccountService;
import com.mftplus.spring_security.core.security.SecurityUser;
import com.mftplus.spring_security.home.dto.HomeDto;
import com.mftplus.spring_security.home.service.HomeService;
import com.mftplus.spring_security.product.dto.ProductDto;
import com.mftplus.spring_security.product.service.ProductService;
import com.mftplus.spring_security.purchase.dto.PurchaseDto;
import com.mftplus.spring_security.purchase.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final BankAccountService bankAccountService;
    private final ProductService productService;
    private final HomeService homeService;
    private final PurchaseService purchaseService;

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return ((SecurityUser) authentication.getPrincipal()).getId();
        }
        return null;
    }

    // صفحه اصلی پروفایل - نمایش حساب‌های بانکی
    @GetMapping
    public String profile(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Authentication authentication,
            Model model) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<BankAccountDto> accounts = bankAccountService.findByUserId(userId, pageable);

        model.addAttribute("accounts", accounts);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("activeTab", "accounts");

        return "profile/index";
    }

    // نمایش کالاها
    @GetMapping("/products")
    public String showProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication,
            Model model) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.findAll(pageable);

        model.addAttribute("products", products);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("activeTab", "products");

        return "profile/products";
    }

    // نمایش خانه‌ها
    @GetMapping("/homes")
    public String showHomes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication,
            Model model) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<HomeDto> homes = homeService.findAll(pageable);

        model.addAttribute("homes", homes);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("activeTab", "homes");

        return "profile/homes";
    }

    // نمایش فرم خرید محصول
    @GetMapping("/products/{productId}/purchase")
    public String showProductPurchaseForm(
            @PathVariable Long productId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        ProductDto product = productService.findById(productId);
        if (product == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "محصول یافت نشد");
            return "redirect:/profile/products";
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<BankAccountDto> accounts = bankAccountService.findByUserId(userId, pageable);

        model.addAttribute("product", product);
        model.addAttribute("accounts", accounts.getContent());
        model.addAttribute("username", authentication.getName());

        return "profile/purchase-product";
    }

    // خرید محصول
    @PostMapping("/products/{productId}/purchase")
    public String purchaseProduct(
            @PathVariable Long productId,
            @RequestParam Long bankAccountId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            PurchaseDto purchaseDto = new PurchaseDto();
            purchaseDto.setProductId(productId);
            purchaseDto.setBankAccountId(bankAccountId);

            purchaseService.purchase(userId, purchaseDto);

            redirectAttributes.addFlashAttribute("successMessage", "محصول با موفقیت خریداری شد");
            return "redirect:/profile/my-purchases";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile/products/" + productId + "/purchase";
        }
    }

    // نمایش فرم خرید خانه
    @GetMapping("/homes/{homeId}/purchase")
    public String showHomePurchaseForm(
            @PathVariable Long homeId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        HomeDto home = homeService.findById(homeId);
        if (home == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "خانه یافت نشد");
            return "redirect:/profile/homes";
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<BankAccountDto> accounts = bankAccountService.findByUserId(userId, pageable);

        model.addAttribute("home", home);
        model.addAttribute("accounts", accounts.getContent());
        model.addAttribute("username", authentication.getName());

        return "profile/purchase-home";
    }

    // خرید خانه
    @PostMapping("/homes/{homeId}/purchase")
    public String purchaseHome(
            @PathVariable Long homeId,
            @RequestParam Long bankAccountId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            // TODO: باید سرویس خرید خانه را پیاده‌سازی کنید
            homeService.markAsSold(homeId);

            redirectAttributes.addFlashAttribute("successMessage", "خانه با موفقیت خریداری شد");
            return "redirect:/profile/my-homes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile/homes/" + homeId + "/purchase";
        }
    }

    // نمایش خریدهای کاربر
    @GetMapping("/my-purchases")
    public String myPurchases(
            Authentication authentication,
            Model model) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("purchases", purchaseService.findByUserId(userId));
        model.addAttribute("username", authentication.getName());
        model.addAttribute("activeTab", "purchases");

        return "profile/my-purchases";
    }

    // نمایش خانه‌های خریداری شده
    @GetMapping("/my-homes")
    public String myHomes(
            Authentication authentication,
            Model model) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        // TODO: باید سرویس لیست خانه‌های خریداری شده را پیاده‌سازی کنید
        model.addAttribute("homes", homeService.findAllByPersonId(userId));
        model.addAttribute("username", authentication.getName());
        model.addAttribute("activeTab", "my-homes");

        return "profile/my-homes";
    }
}