package com.mftplus.spring_security.controller;

import com.mftplus.spring_security.core.security.SecurityUser;
import com.mftplus.spring_security.simcard.dto.SimCardDto;
import com.mftplus.spring_security.simcard.service.SimCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/simcards")
@RequiredArgsConstructor
public class SimCardController {

    private final SimCardService simCardService;

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return ((SecurityUser) authentication.getPrincipal()).getId();
        }
        return null;
    }
    
    @GetMapping
    public String listSimCards(Model model, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }
        model.addAttribute("simcards", simCardService.findAllByOwnerId(userId));
        model.addAttribute("title", "لیست سیم کارت‌ها");
        return "simcard/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("simcard", new SimCardDto());
        model.addAttribute("title", "افزودن سیم کارت جدید");
        return "simcard/create";
    }
    
    @PostMapping("/create")
    public String createSimCard(@Valid @ModelAttribute("simcard") SimCardDto simCardDto,
                               BindingResult result,
                               Model model,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "simcard/create";
        }
        
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            simCardService.save(simCardDto, userId);
            redirectAttributes.addFlashAttribute("success", "سیم کارت با موفقیت ایجاد شد");
            return "redirect:/simcards";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "simcard/create";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, 
                              Model model, 
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }
        
        return simCardService.findByIdAndOwnerId(id, userId)
                .map(simcard -> {
                    model.addAttribute("simcard", simcard);
                    model.addAttribute("title", "ویرایش سیم کارت");
                    return "simcard/edit";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "سیم کارت یافت نشد یا شما اجازه دسترسی ندارید");
                    return "redirect:/simcards";
                });
    }
    
    @PostMapping("/edit/{id}")
    public String updateSimCard(@PathVariable Long id,
                               @Valid @ModelAttribute("simcard") SimCardDto simCardDto,
                               BindingResult result,
                               Model model,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "simcard/edit";
        }
        
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            simCardService.update(id, simCardDto, userId);
            redirectAttributes.addFlashAttribute("success", "سیم کارت با موفقیت به‌روزرسانی شد");
            return "redirect:/simcards";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "simcard/edit";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteSimCard(@PathVariable Long id, 
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            simCardService.deleteById(id, userId);
            redirectAttributes.addFlashAttribute("success", "سیم کارت با موفقیت حذف شد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/simcards";
    }
}

