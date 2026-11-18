
package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.outcomeiq.service.QuestionService;

@Controller
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/questions")
    public String questions(Model model) {
        model.addAttribute("questions", questionService.getAllQuestions());
        model.addAttribute("courses", questionService.getAllCourses());
        model.addAttribute("cos", questionService.getAllCourseOutcomes());
        model.addAttribute("bloomLevels", QuestionService.BLOOM_LEVELS);
        return "questions";
    }

    @PostMapping("/addQuestion")
    public String addQuestion(
            @RequestParam String questionText,
            @RequestParam String bloomLevel,
            @RequestParam int marks,
            @RequestParam(required = false) Integer yearUsed,
            @RequestParam String assessmentType,
            @RequestParam Long courseId,
            @RequestParam Long coId,
            RedirectAttributes redirectAttributes) {
        try {
            questionService.addQuestion(
                    questionText, bloomLevel, marks,
                    yearUsed, assessmentType, courseId, coId);

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Question added successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", exception.getMessage());
        }
        return "redirect:/questions";
    }

    @PostMapping("/updateQuestion")
    public String updateQuestion(
            @RequestParam Long id,
            @RequestParam String questionText,
            @RequestParam String bloomLevel,
            @RequestParam int marks,
            @RequestParam(required = false) Integer yearUsed,
            @RequestParam String assessmentType,
            @RequestParam Long courseId,
            @RequestParam Long coId,
            RedirectAttributes redirectAttributes) {
        try {
            questionService.updateQuestion(
                    id, questionText, bloomLevel, marks,
                    yearUsed, assessmentType, courseId, coId);

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Question updated successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", exception.getMessage());
        }
        return "redirect:/questions";
    }

    @GetMapping("/deleteQuestion/{id}")
    public String deleteQuestion(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            questionService.deleteQuestion(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Question deleted successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", exception.getMessage());
        }
        return "redirect:/questions";
    }
}
