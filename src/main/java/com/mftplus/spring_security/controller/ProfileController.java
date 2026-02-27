package com.mftplus.spring_security.controller;

import com.mftplus.spring_security.Purchase.dto.PurchaseDto;
import com.mftplus.spring_security.Purchase.service.PurchaseService;
import com.mftplus.spring_security.bankAccount.dto.BankAccountDto;
import com.mftplus.spring_security.bankAccount.service.BankAccountService;
import com.mftplus.spring_security.core.security.SecurityUser;
import com.mftplus.spring_security.home.dto.HomeDto;
import com.mftplus.spring_security.home.service.HomeService;
import com.mftplus.spring_security.product.dto.ProductDto;
import com.mftplus.spring_security.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final BankAccountService bankAccountService;
    private final ProductService productService;
    private final HomeService homeService;
    private final PurchaseService purchaseService;

    // ═══════════════════════════════════════════════════════════════════
    // صفحه اصلی پروفایل - حساب‌های بانکی
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping
    public String profileIndex(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication,
            Model model) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("balance").descending());
        Page<BankAccountDto> accounts = bankAccountService.findByUserId(userId, pageable);

        model.addAttribute("username", authentication.getName());
        model.addAttribute("accounts", accounts);
        model.addAttribute("activeTab", "accounts");

        return "profile/index";
    }

    // ═══════════════════════════════════════════════════════════════════
    // لیست کالاها (Products)
    // ═══════════════════════════════════════════════════════════════════

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

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ProductDto> products = productService.findAll(pageable);

        model.addAttribute("username", authentication.getName());
        model.addAttribute("products", products);
        model.addAttribute("activeTab", "products");

        return "profile/products";
    }

    // ═══════════════════════════════════════════════════════════════════
    // صفحه خرید کالا (GET)
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/products/{productId}/purchase")
    public String showProductPurchasePage(
            @PathVariable Long productId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            ProductDto product = productService.findById(productId);

            List<BankAccountDto> accounts = bankAccountService.findByUserId(userId);

            model.addAttribute("product", product);
            model.addAttribute("accounts", accounts);
            model.addAttribute("username", authentication.getName());

            return "profile/purchase-product";

        } catch (Exception e) {
            log.error("Error loading product purchase page: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile/products";
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // انجام خرید کالا (POST)
    // ═══════════════════════════════════════════════════════════════════

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
            PurchaseDto purchase = purchaseService.buyProduct(userId, productId, bankAccountId);
            log.info("Product purchased successfully. Invoice: {}", purchase.getInvoiceNumber());

            redirectAttributes.addFlashAttribute("successMessage",
                    "کالا با موفقیت خریداری شد. شماره فاکتور: " + purchase.getInvoiceNumber());
            return "redirect:/profile/my-purchases";

        } catch (IllegalArgumentException e) {
            log.error("Product purchase failed: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile/products/" + productId + "/purchase";
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // لیست خانه‌ها (Homes)
    // ═══════════════════════════════════════════════════════════════════

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

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<HomeDto> homes = homeService.findAll(pageable);

        model.addAttribute("username", authentication.getName());
        model.addAttribute("homes", homes);
        model.addAttribute("activeTab", "homes");

        return "profile/homes";
    }

    // ═══════════════════════════════════════════════════════════════════
    // صفحه خرید خانه (GET)
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/homes/{homeId}/purchase")
    public String showHomePurchasePage(
            @PathVariable Long homeId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            HomeDto home = homeService.findById(homeId);

            List<BankAccountDto> accounts = bankAccountService.findByUserId(userId);

            model.addAttribute("home", home);
            model.addAttribute("accounts", accounts);
            model.addAttribute("username", authentication.getName());

            return "profile/purchase-home";

        } catch (Exception e) {
            log.error("Error loading home purchase page: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile/homes";
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // انجام خرید خانه (POST)
    // ═══════════════════════════════════════════════════════════════════

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
            PurchaseDto purchase = purchaseService.buyHome(userId, homeId, bankAccountId);
            log.info("Home purchased successfully. Invoice: {}", purchase.getInvoiceNumber());

            redirectAttributes.addFlashAttribute("successMessage",
                    "خانه با موفقیت خریداری شد. شماره فاکتور: " + purchase.getInvoiceNumber());
            return "redirect:/profile/my-purchases";

        } catch (IllegalArgumentException e) {
            log.error("Home purchase failed: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile/homes/" + homeId + "/purchase";
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // لیست خریدهای من
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/my-purchases")
    public String showMyPurchases(
            Authentication authentication,
            Model model) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        List<PurchaseDto> purchases = purchaseService.findAllByUserId(userId);

        model.addAttribute("username", authentication.getName());
        model.addAttribute("purchases", purchases);
        model.addAttribute("activeTab", "purchases");

        return "profile/my-purchases";
    }



    // ═══════════════════════════════════════════════════════════════════
    // متد کمکی برای گرفتن userId
    // ═══════════════════════════════════════════════════════════════════

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return ((SecurityUser) authentication.getPrincipal()).getId();
        }
        return null;
    }

}