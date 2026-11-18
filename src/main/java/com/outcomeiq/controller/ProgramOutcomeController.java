package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.outcomeiq.service.ProgramOutcomeService;

@Controller
public class ProgramOutcomeController {

    private final ProgramOutcomeService poService;

    public ProgramOutcomeController(ProgramOutcomeService poService) {
        this.poService = poService;
    }

    @GetMapping("/pos")
    public String pos(Model model) {
        model.addAttribute("pos", poService.getAllProgramOutcomes());
        return "pos";
    }

    @PostMapping("/addPO")
    public String addPO(
            @RequestParam String poCode,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {

        try {
            poService.addProgramOutcome(poCode, description);
            redirectAttributes.addFlashAttribute("successMessage", "Program Outcome added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding PO: " + e.getMessage());
        }

        return "redirect:/pos";
    }

    @PostMapping("/updatePO")
    public String updatePO(
            @RequestParam Long id,
            @RequestParam String poCode,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {

        try {
            poService.updateProgramOutcome(id, poCode, description);
            redirectAttributes.addFlashAttribute("successMessage", "Program Outcome updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating PO: " + e.getMessage());
        }

        return "redirect:/pos";
    }

    @GetMapping("/deletePO/{id}")
    public String deletePO(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            poService.deleteProgramOutcome(id);
            redirectAttributes.addFlashAttribute("successMessage", "Program Outcome deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting PO: " + e.getMessage());
        }

        return "redirect:/pos";
    }
}
