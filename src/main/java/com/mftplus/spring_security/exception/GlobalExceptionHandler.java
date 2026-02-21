package com.mftplus.spring_security.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRuntimeException(RuntimeException ex, Model model, RedirectAttributes redirectAttributes) {
        log.error("RuntimeException occurred: {}", ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/error";
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex, RedirectAttributes redirectAttributes) {
        log.error("IllegalArgumentException occurred: {}", ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/error";
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationExceptions(MethodArgumentNotValidException ex, RedirectAttributes redirectAttributes) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Validation errors: {}", errors);
        redirectAttributes.addFlashAttribute("validationErrors", errors);
        redirectAttributes.addFlashAttribute("error", "خطا در اعتبارسنجی داده‌ها");
        return "redirect:/error";
    }
    
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        // Ignore favicon.ico and logout static resource errors
        String resourcePath = ex.getResourcePath();
        if (resourcePath != null && !resourcePath.equals("favicon.ico") && !resourcePath.equals("logout")) {
            log.debug("Resource not found: {}", resourcePath);
        }
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model, RedirectAttributes redirectAttributes) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("error", "خطای غیرمنتظره رخ داد. لطفا با مدیر سیستم تماس بگیرید.");
        return "redirect:/error";
    }
}

