package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.outcomeiq.service.CoPoMappingService;

@Controller
public class CoPoMappingController {

    private final CoPoMappingService mappingService;

    public CoPoMappingController(CoPoMappingService mappingService) {
        this.mappingService = mappingService;
    }

    @GetMapping("/mappings")
    public String mappings(Model model) {
        model.addAttribute("mappings", mappingService.getAllMappings());
        model.addAttribute("cos", mappingService.getAllCourseOutcomes());
        model.addAttribute("pos", mappingService.getAllProgramOutcomes());
        return "mappings";
    }

    @PostMapping("/addMapping")
    public String addMapping(
            @RequestParam Long coId,
            @RequestParam Long poId,
            @RequestParam int mappingStrength,
            RedirectAttributes redirectAttributes) {

        try {
            mappingService.addMapping(coId, poId, mappingStrength);
            redirectAttributes.addFlashAttribute("successMessage", "CO-PO mapping added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding mapping: " + e.getMessage());
        }

        return "redirect:/mappings";
    }

    @PostMapping("/updateMapping")
    public String updateMapping(
            @RequestParam Long id,
            @RequestParam Long coId,
            @RequestParam Long poId,
            @RequestParam int mappingStrength,
            RedirectAttributes redirectAttributes) {

        try {
            mappingService.updateMapping(id, coId, poId, mappingStrength);
            redirectAttributes.addFlashAttribute("successMessage", "CO-PO mapping updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating mapping: " + e.getMessage());
        }

        return "redirect:/mappings";
    }

    @GetMapping("/deleteMapping/{id}")
    public String deleteMapping(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            mappingService.deleteMapping(id);
            redirectAttributes.addFlashAttribute("successMessage", "CO-PO mapping deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting mapping: " + e.getMessage());
        }

        return "redirect:/mappings";
    }
}
