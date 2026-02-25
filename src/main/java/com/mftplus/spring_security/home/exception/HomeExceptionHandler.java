package com.mftplus.spring_security.home.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice(basePackages = "com.mftplus.spring_security.home")
public class HomeExceptionHandler {

    @ExceptionHandler(HomeNotFoundException.class)
    public String handleNotFound(HomeNotFoundException ex, RedirectAttributes ra) {
        log.warn("[{}] {}", ex.getErrorCode(), ex.getMessage());
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/homes";
    }

    @ExceptionHandler(HomeAlreadySoldException.class)
    public String handleAlreadySold(HomeAlreadySoldException ex, RedirectAttributes ra) {
        log.warn("[{}] {}", ex.getErrorCode(), ex.getMessage());
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/homes";
    }

    @ExceptionHandler(HomeNotAvailableException.class)
    public String handleNotAvailable(HomeNotAvailableException ex, RedirectAttributes ra) {
        log.warn("[{}] {}", ex.getErrorCode(), ex.getMessage());
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/homes";
    }

    @ExceptionHandler(HomeOwnerNotFoundException.class)
    public String handleOwnerNotFound(HomeOwnerNotFoundException ex, RedirectAttributes ra) {
        log.warn("[{}] {}", ex.getErrorCode(), ex.getMessage());
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/homes";
    }

    @ExceptionHandler(HomeValidationException.class)
    public String handleValidation(HomeValidationException ex, RedirectAttributes ra) {
        log.warn("[{}] field='{}' {}", ex.getErrorCode(), ex.getField(), ex.getMessage());
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/homes";
    }

    @ExceptionHandler(HomeException.class)
    public String handleGeneric(HomeException ex, RedirectAttributes ra) {
        log.error("[{}] {}", ex.getErrorCode(), ex.getMessage(), ex);
        ra.addFlashAttribute("errorMessage", "Unexpected error: " + ex.getMessage());
        return "redirect:/homes";
    }
}