package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.outcomeiq.service.CourseOutcomeService;

@Controller
public class CourseOutcomeController {

    private final CourseOutcomeService coService;

    public CourseOutcomeController(CourseOutcomeService coService) {
        this.coService = coService;
    }

    @GetMapping("/cos")
    public String showCOs(Model model) {
        model.addAttribute("cos", coService.getAllCourseOutcomes());
        model.addAttribute("courses", coService.getAllCourses());
        return "cos";
    }

    @PostMapping("/addCO")
    public String addCO(
            @RequestParam String coCode,
            @RequestParam String description,
            @RequestParam Long courseId,
            RedirectAttributes redirectAttributes) {

        try {
            coService.addCourseOutcome(coCode, description, courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Course Outcome added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding CO: " + e.getMessage());
        }

        return "redirect:/cos";
    }

    @PostMapping("/updateCO")
    public String updateCO(
            @RequestParam Long id,
            @RequestParam String coCode,
            @RequestParam String description,
            @RequestParam Long courseId,
            RedirectAttributes redirectAttributes) {

        try {
            coService.updateCourseOutcome(id, coCode, description, courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Course Outcome updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating CO: " + e.getMessage());
        }

        return "redirect:/cos";
    }

    @GetMapping("/deleteCO/{id}")
    public String deleteCO(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            coService.deleteCourseOutcome(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course Outcome deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting CO: " + e.getMessage());
        }
        return "redirect:/cos";
    }
}
