package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.outcomeiq.service.StudentMarkService;

@Controller
public class StudentMarkController {

    private final StudentMarkService markService;

    public StudentMarkController(StudentMarkService markService) {
        this.markService = markService;
    }

    @GetMapping("/marks")
    public String marks(Model model) {
        model.addAttribute("marks", markService.getAllMarks());
        model.addAttribute("students", markService.getAllStudents());
        model.addAttribute("questions", markService.getAllQuestions());
        return "marks";
    }

    @PostMapping("/addMark")
    public String addMark(
            @RequestParam Long studentId,
            @RequestParam Long questionId,
            @RequestParam double marksObtained,
            RedirectAttributes redirectAttributes) {

        try {
            markService.addMark(studentId, questionId, marksObtained);
            redirectAttributes.addFlashAttribute("successMessage", "Marks recorded successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error recording marks: " + e.getMessage());
        }

        return "redirect:/marks";
    }

    @PostMapping("/updateMark")
    public String updateMark(
            @RequestParam Long id,
            @RequestParam Long studentId,
            @RequestParam Long questionId,
            @RequestParam double marksObtained,
            RedirectAttributes redirectAttributes) {

        try {
            markService.updateMark(id, studentId, questionId, marksObtained);
            redirectAttributes.addFlashAttribute("successMessage", "Marks updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating marks: " + e.getMessage());
        }

        return "redirect:/marks";
    }

    @GetMapping("/deleteMark/{id}")
    public String deleteMark(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            markService.deleteMark(id);
            redirectAttributes.addFlashAttribute("successMessage", "Mark record deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting mark: " + e.getMessage());
        }

        return "redirect:/marks";
    }
}
