package com.mftplus.spring_security.controller;

import com.mftplus.spring_security.core.dto.PersonDto;
import com.mftplus.spring_security.core.model.Department;
import com.mftplus.spring_security.core.service.DepartmentService;
import com.mftplus.spring_security.core.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {
    
    private final PersonService personService;
    private final DepartmentService departmentService;
    
    @GetMapping
    public String listPersons(Model model,
                              @RequestParam(required = false) String firstName,
                              @RequestParam(required = false) String lastName,
                              @RequestParam(required = false) String nationalCode) {
        List<PersonDto> persons;
        if (firstName != null || lastName != null || nationalCode != null) {
            persons = personService.search(firstName, lastName, nationalCode);
        } else {
            persons = personService.findAllActive();
        }
        model.addAttribute("persons", persons);
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("nationalCode", nationalCode);
        model.addAttribute("title", "لیست اشخاص");
        return "person/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("person", new PersonDto());
        List<Department> departments = departmentService.findAllActive();
        model.addAttribute("departments", departments);
        model.addAttribute("title", "افزودن شخص جدید");
        return "person/create";
    }
    
    @PostMapping("/create")
    public String createPerson(@Valid @ModelAttribute("person") PersonDto personDto,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<Department> departments = departmentService.findAllActive();
            model.addAttribute("departments", departments);
            return "person/create";
        }
        
        try {
            personService.save(personDto);
            redirectAttributes.addFlashAttribute("success", "شخص با موفقیت ایجاد شد");
            return "redirect:/persons";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            List<Department> departments = departmentService.findAllActive();
            model.addAttribute("departments", departments);
            return "person/create";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return personService.findById(id)
                .map(person -> {
                    model.addAttribute("person", person);
                    List<Department> departments = departmentService.findAllActive();
                    model.addAttribute("departments", departments);
                    model.addAttribute("title", "ویرایش شخص");
                    return "person/edit";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "شخص یافت نشد");
                    return "redirect:/persons";
                });
    }
    
    @PostMapping("/edit/{id}")
    public String updatePerson(@PathVariable Long id,
                              @Valid @ModelAttribute("person") PersonDto personDto,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<Department> departments = departmentService.findAllActive();
            model.addAttribute("departments", departments);
            return "person/edit";
        }
        
        try {
            personService.update(id, personDto);
            redirectAttributes.addFlashAttribute("success", "شخص با موفقیت به‌روزرسانی شد");
            return "redirect:/persons";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            List<Department> departments = departmentService.findAllActive();
            model.addAttribute("departments", departments);
            return "person/edit";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deletePerson(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            personService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "شخص با موفقیت حذف شد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/persons";
    }
    
    @GetMapping("/view/{id}")
    public String viewPerson(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return personService.findById(id)
                .map(person -> {
                    model.addAttribute("person", person);
                    model.addAttribute("title", "مشاهده شخص");
                    return "person/view";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "شخص یافت نشد");
                    return "redirect:/persons";
                });
    }
}

