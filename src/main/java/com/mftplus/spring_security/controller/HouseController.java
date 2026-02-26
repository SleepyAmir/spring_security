package com.mftplus.spring_security.controller;

import com.mftplus.spring_security.core.service.PersonService;
import com.mftplus.spring_security.home.dto.HomeDto;
import com.mftplus.spring_security.home.model.enums.HomeStatus;
import com.mftplus.spring_security.home.model.enums.HomeType;
import com.mftplus.spring_security.home.service.HomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/homes")
@RequiredArgsConstructor
public class HouseController {

    private final HomeService   homeService;
    private final PersonService personService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "0")  int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false)    String city,
                       @RequestParam(required = false)    String state,
                       @RequestParam(required = false)    HomeStatus status,
                       @RequestParam(required = false)    HomeType type) {

        PageRequest pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        boolean hasFilter = (city != null && !city.isBlank()) || (state != null && !state.isBlank())
                || status != null || type != null;

        model.addAttribute("homes", hasFilter
                ? homeService.findAllByFilter(city, state, status, type, pr)
                : homeService.findAll(pr));
        addEnums(model);
        model.addAttribute("filterCity",   city);
        model.addAttribute("filterState",  state);
        model.addAttribute("filterStatus", status);
        model.addAttribute("filterType",   type);
        return "home/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("homeDto", new HomeDto());
        addFormData(model);
        return "home/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("homeDto") HomeDto dto,
                         BindingResult br, Model model,
                         RedirectAttributes ra) {
        if (br.hasErrors()) { addFormData(model); return "home/form"; }
        homeService.save(dto);
        ra.addFlashAttribute("successMessage", "Home created successfully.");
        return "redirect:/homes";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("homeDto", homeService.findById(id));
        addFormData(model);
        return "home/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("homeDto") HomeDto dto,
                         BindingResult br, Model model,
                         RedirectAttributes ra) {
        if (br.hasErrors()) { addFormData(model); return "home/form"; }
        homeService.update(id, dto);
        ra.addFlashAttribute("successMessage", "Home updated successfully.");
        return "redirect:/homes";
    }


    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("home", homeService.findById(id));
        return "home/view";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        homeService.deleteById(id);
        ra.addFlashAttribute("successMessage", "Home deleted.");
        return "redirect:/homes";
    }

    @PostMapping("/restore/{id}")
    public String restore(@PathVariable Long id, RedirectAttributes ra) {
        homeService.restoreById(id);
        ra.addFlashAttribute("successMessage", "Home restored.");
        return "redirect:/homes/deleted";
    }

    @GetMapping("/deleted")
    public String deleted(Model model,
                          @RequestParam(defaultValue = "0")  int page,
                          @RequestParam(defaultValue = "10") int size) {
        model.addAttribute("homes", homeService.findAllDeleted(PageRequest.of(page, size)));
        return "home/deleted";
    }

    @PostMapping("/sell/{id}")
    public String sell(@PathVariable Long id, RedirectAttributes ra) {
        homeService.markAsSold(id);
        ra.addFlashAttribute("successMessage", "Home marked as SOLD.");
        return "redirect:/homes";
    }

    @PostMapping("/reserve/{id}")
    public String reserve(@PathVariable Long id, RedirectAttributes ra) {
        homeService.markAsReserved(id);
        ra.addFlashAttribute("successMessage", "Home marked as RESERVED.");
        return "redirect:/homes";
    }

    @PostMapping("/available/{id}")
    public String available(@PathVariable Long id, RedirectAttributes ra) {
        homeService.markAsAvailable(id);
        ra.addFlashAttribute("successMessage", "Home is now AVAILABLE.");
        return "redirect:/homes";
    }

    @GetMapping("/person/{personId}")
    public String byPerson(@PathVariable Long personId, Model model,
                           @RequestParam(defaultValue = "0")  int page,
                           @RequestParam(defaultValue = "10") int size) {
        model.addAttribute("homes", homeService.findAllByPersonId(personId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
        addEnums(model);
        return "home/list";
    }

    private void addFormData(Model model) {
        model.addAttribute("persons", personService.findAllActive());
        addEnums(model);
    }

    private void addEnums(Model model) {
        model.addAttribute("homeTypes",    HomeType.values());
        model.addAttribute("homeStatuses", HomeStatus.values());
    }
}