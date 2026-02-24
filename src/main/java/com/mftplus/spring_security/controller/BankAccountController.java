package com.mftplus.spring_security.controller;


import com.mftplus.spring_security.bankAccount.dto.BankAccountDto;
import com.mftplus.spring_security.bankAccount.service.BankAccountService;
import com.mftplus.spring_security.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private void prepareListModel(Model model, int page, int size, String name, String accountNumber) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("balance").descending());
        Page<BankAccountDto> bankAccountPage;

        if (name != null && accountNumber != null && !name.isBlank()) {
            bankAccountPage = bankAccountService.findByNameAndAccountNumber(name, accountNumber, pageable);
        } else {
            bankAccountPage = bankAccountService.findAll(pageable);
        }

        model.addAttribute("bankAccounts", bankAccountPage);
    }

    @GetMapping
    public String getAllBankAcounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String accountNumber,
            Model model) {

        prepareListModel(model, page, size, name, accountNumber);

        if (!model.containsAttribute("bankAccount")) {
            model.addAttribute("bankAccount", new BankAccountDto());
        }

        return "bankAccount/list";
    }

    // --- GET TRASH ---
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

    // --- CREATE (POST) ---
    @PostMapping
    public String createBankAccount(
            @Valid @ModelAttribute("bankAccount") BankAccountDto bankAccountDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (result.hasErrors()) {
            prepareListModel(model, page, size, null, null);
            model.addAttribute("showModal", true);
            model.addAttribute("formAction", "/bankAccount");
            return "bankAccount/list";
        }

        bankAccountService.save(bankAccountDto);
        redirectAttributes.addFlashAttribute("successMessage", "Account created successfully!");
        return "redirect:/bankAccount";
    }

    // --- UPDATE (PUT) ---
    @PutMapping("/{id}")
    public String updateBankAccount(
            @PathVariable Long id,
            @Valid @ModelAttribute("bankAccount") BankAccountDto bankAccountDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam(defaultValue = "0") int page) {

        if (result.hasErrors()) {
            prepareListModel(model, page, 10, null, null);
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

    // --- DELETE ---
    @DeleteMapping("/{id}")
    public String deleteBankAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (bankAccountService.findById(id) == null) {
            throw new ResourceNotFoundException("Cannot delete. Bank Account not found with id: " + id);
        }

        bankAccountService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Account deleted (moved to trash).");
        return "redirect:/bankAccount";
    }

    // --- RESTORE ---
    @PostMapping("/restore/{id}")
    public String restoreBankAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bankAccountService.restoreById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Account restored successfully!");
        return "redirect:/bankAccount/trash";
    }
}