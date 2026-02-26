package com.mftplus.spring_security.controller;

import com.mftplus.spring_security.bankAccount.dto.BankAccountDto;
import com.mftplus.spring_security.bankAccount.service.BankAccountService;
import com.mftplus.spring_security.core.security.SecurityUser;
import com.mftplus.spring_security.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/bankAccount")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    // ⚠️ متد کمکی برای گرفتن userId
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return ((SecurityUser) authentication.getPrincipal()).getId();
        }
        return null;
    }

    private void prepareListModel(Model model, int page, int size, String accountNumber, Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("balance").descending());
        Page<BankAccountDto> bankAccountPage;

        if (accountNumber != null && !accountNumber.isBlank()) {
            bankAccountPage = bankAccountService.findByAccountNumber(accountNumber, pageable);
        } else if (userId != null) {
            bankAccountPage = bankAccountService.findByUserId(userId, pageable);
        } else {
            bankAccountPage = bankAccountService.findAll(pageable);
        }

        model.addAttribute("bankAccounts", bankAccountPage);
    }

    @GetMapping
    public String getAllBankAcounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String accountNumber,
            Authentication authentication,
            Model model) {

        Long userId = getCurrentUserId(authentication);
        prepareListModel(model, page, size, accountNumber, userId);

        if (!model.containsAttribute("bankAccount")) {
            model.addAttribute("bankAccount", new BankAccountDto());
        }

        return "bankAccount/list";
    }

    @GetMapping("/trash")
    public String getTrash(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("balance").descending());
        Page<BankAccountDto> deletedPage = bankAccountService.findAllDeleted(pageable);

        model.addAttribute("bankAccounts", deletedPage);
        model.addAttribute("isTrash", true);

        if (!model.containsAttribute("bankAccount")) {
            model.addAttribute("bankAccount", new BankAccountDto());
        }

        return "bankAccount/list";
    }

    @PostMapping
    public String createBankAccount(
            @Valid @ModelAttribute("bankAccount") BankAccountDto bankAccountDto,
            BindingResult result,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            prepareListModel(model, page, size, null, userId);
            model.addAttribute("showModal", true);
            model.addAttribute("formAction", "/bankAccount");
            return "bankAccount/list";
        }

        bankAccountService.save(bankAccountDto, userId); // ⚠️ userId اضافه شد
        redirectAttributes.addFlashAttribute("successMessage", "Account created successfully!");
        return "redirect:/bankAccount";
    }

    @PutMapping("/{id}")
    public String updateBankAccount(
            @PathVariable Long id,
            @Valid @ModelAttribute("bankAccount") BankAccountDto bankAccountDto,
            BindingResult result,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestParam(defaultValue = "0") int page) {

        Long userId = getCurrentUserId(authentication);

        if (result.hasErrors()) {
            prepareListModel(model, page, 10, null, userId);
            model.addAttribute("showModal", true);
            model.addAttribute("formAction", "/bankAccount/" + id);
            bankAccountDto.setId(id);
            return "bankAccount/list";
        }

        if (bankAccountService.findById(id) == null) {
            throw new ResourceNotFoundException("Bank Account not found with id: " + id);
        }

        bankAccountDto.setId(id);
        bankAccountService.update(bankAccountDto);
        redirectAttributes.addFlashAttribute("successMessage", "Account updated successfully!");
        return "redirect:/bankAccount";
    }

    @DeleteMapping("/{id}")
    public String deleteBankAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (bankAccountService.findById(id) == null) {
            throw new ResourceNotFoundException("Cannot delete. Bank Account not found with id: " + id);
        }

        bankAccountService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Account deleted (moved to trash).");
        return "redirect:/bankAccount";
    }

    @PostMapping("/restore/{id}")
    public String restoreBankAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bankAccountService.restoreById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Account restored successfully!");
        return "redirect:/bankAccount/trash";
    }
}