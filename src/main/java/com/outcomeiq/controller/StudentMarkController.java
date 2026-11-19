package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.outcomeiq.service.StudentMarkService;

import java.util.HashMap;
import java.util.Map;

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
            boolean wasExisting = markService.existsMark(studentId, questionId);
            markService.saveOrUpdateMark(studentId, questionId, marksObtained);
            if (wasExisting) {
                redirectAttributes.addFlashAttribute("successMessage", "Existing mark record was successfully updated.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Marks recorded successfully.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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

    @GetMapping("/api/marks/lookup")
    @ResponseBody
    public Map<String, Object> lookupMark(
            @RequestParam Long studentId,
            @RequestParam Long questionId) {

        Map<String, Object> response = new HashMap<>();
        markService.findMarkByStudentAndQuestion(studentId, questionId).ifPresentOrElse(
                m -> {
                    response.put("found", true);
                    response.put("id", m.getId());
                    response.put("marksObtained", m.getMarksObtained());
                    response.put("maxMarks", m.getQuestion() != null ? m.getQuestion().getMarks() : null);
                },
                () -> response.put("found", false)
        );
        return response;
    }
}
