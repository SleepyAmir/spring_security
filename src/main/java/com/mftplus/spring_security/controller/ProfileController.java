package com.mftplus.spring_security.controller;


import com.mftplus.spring_security.Purchase.Purchase;
import com.mftplus.spring_security.Purchase.PurchaseService;
import com.mftplus.spring_security.bankAccount.dto.BankAccountDto;
import com.mftplus.spring_security.bankAccount.service.BankAccountService;
import com.mftplus.spring_security.core.security.SecurityUser;
import com.mftplus.spring_security.home.dto.HomeDto;
import com.mftplus.spring_security.home.service.HomeService;
import com.mftplus.spring_security.product.dto.ProductDto;
import com.mftplus.spring_security.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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

    // ═══════════════════════════════════════════════════════════════════
    // صفحه اصلی
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping
    public String index(Authentication authentication, Model model) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) return "redirect:/login";

        List<BankAccountDto> accounts = bankAccountService.findByUserId(userId);

        model.addAttribute("accounts", accounts);
        model.addAttribute("username", authentication.getName());
        return "profile/index";
    }

    // ═══════════════════════════════════════════════════════════════════
    // لیست Products
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/products")
    public String products(
            @RequestParam(defaultValue = "0") int page,
            Authentication authentication,
            Model model) {

        Page<ProductDto> products = productService.findAll(PageRequest.of(page, 10, Sort.by("id").descending()));

        model.addAttribute("products", products);
        model.addAttribute("username", authentication.getName());
        return "profile/products";
    }

    // ═══════════════════════════════════════════════════════════════════
    // خرید Product
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/products/{id}/purchase")
    public String purchaseProductPage(@PathVariable Long id, Authentication authentication, Model model) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) return "redirect:/login";

        ProductDto product = productService.findById(id);
        List<BankAccountDto> accounts = bankAccountService.findByUserId(userId);

        model.addAttribute("product", product);
        model.addAttribute("accounts", accounts);
        model.addAttribute("username", authentication.getName());

        return "profile/purchase-product";
    }

    @PostMapping("/products/{id}/purchase")
    public String purchaseProduct(
            @PathVariable Long id,
            @RequestParam Long bankAccountId,
            Authentication authentication,
            RedirectAttributes ra) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) return "redirect:/login";

        try {
            Purchase purchase = purchaseService.buyProduct(userId, id, bankAccountId);
            ra.addFlashAttribute("successMessage", "خرید موفق! فاکتور: " + purchase.getInvoiceNumber());
            return "redirect:/profile/my-purchases";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile/products/" + id + "/purchase";
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // لیست Homes
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/homes")
    public String homes(
            @RequestParam(defaultValue = "0") int page,
            Authentication authentication,
            Model model) {

        Page<HomeDto> homes = homeService.findAll(PageRequest.of(page, 10, Sort.by("createdAt").descending()));

        model.addAttribute("homes", homes);
        model.addAttribute("username", authentication.getName());
        return "profile/homes";
    }

    // ═══════════════════════════════════════════════════════════════════
    // خرید Home
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/homes/{id}/purchase")
    public String purchaseHomePage(@PathVariable Long id, Authentication authentication, Model model) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) return "redirect:/login";

        HomeDto home = homeService.findById(id);
        List<BankAccountDto> accounts = bankAccountService.findByUserId(userId);

        model.addAttribute("home", home);
        model.addAttribute("accounts", accounts);
        model.addAttribute("username", authentication.getName());

        return "profile/purchase-home";
    }

    @PostMapping("/homes/{id}/purchase")
    public String purchaseHome(
            @PathVariable Long id,
            @RequestParam Long bankAccountId,
            Authentication authentication,
            RedirectAttributes ra) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) return "redirect:/login";

        try {
            Purchase purchase = purchaseService.buyHome(userId, id, bankAccountId);
            ra.addFlashAttribute("successMessage", "خرید موفق! فاکتور: " + purchase.getInvoiceNumber());
            return "redirect:/profile/my-purchases";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile/homes/" + id + "/purchase";
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // خریدهای من
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/my-purchases")
    public String myPurchases(Authentication authentication, Model model) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) return "redirect:/login";

        List<Purchase> purchases = purchaseService.findUserPurchases(userId);

        model.addAttribute("purchases", purchases);
        model.addAttribute("username", authentication.getName());
        return "profile/my-purchases";
    }
}