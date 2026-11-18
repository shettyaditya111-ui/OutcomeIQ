package com.outcomeiq.controller;

import com.outcomeiq.dto.BlueprintRequest;
import com.outcomeiq.dto.BlueprintResult;
import com.outcomeiq.entity.Question;
import com.outcomeiq.entity.QuestionPaper;
import com.outcomeiq.repository.CourseOutcomeRepository;
import com.outcomeiq.repository.CourseRepository;
import com.outcomeiq.service.QuestionPaperService;
import com.outcomeiq.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class QuestionPaperController {

    private final QuestionPaperService paperService;
    private final CourseRepository courseRepository;
    private final CourseOutcomeRepository courseOutcomeRepository;

    public QuestionPaperController(
            QuestionPaperService paperService,
            CourseRepository courseRepository,
            CourseOutcomeRepository courseOutcomeRepository) {
        this.paperService = paperService;
        this.courseRepository = courseRepository;
        this.courseOutcomeRepository = courseOutcomeRepository;
    }

    @GetMapping("/paper-generator")
    public String showGenerator(Model model) {
        populateFormAttributes(model);
        model.addAttribute("blueprintRequest", new BlueprintRequest());
        model.addAttribute("blueprintResult", null);
        return "paper-generator";
    }

    @PostMapping("/paper-generator/generate")
    public String generatePaper(
            @ModelAttribute BlueprintRequest request,
            Model model) {

        populateFormAttributes(model);
        BlueprintResult result = paperService.generatePaper(request);
        model.addAttribute("blueprintRequest", request);
        model.addAttribute("blueprintResult", result);

        return "paper-generator";
    }

    @PostMapping("/paper-generator/save")
    public String savePaper(
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam(defaultValue = "CIE") String assessmentType,
            @RequestParam(defaultValue = "2026-2027") String academicYear,
            @RequestParam(required = false) String examDate,
            @RequestParam(defaultValue = "90") int durationMinutes,
            @RequestParam(defaultValue = "50") int maxMarks,
            @RequestParam(required = false) String instructions,
            @RequestParam List<Long> questionIds,
            RedirectAttributes redirectAttributes) {

        try {
            QuestionPaper paper = paperService.savePaper(
                    courseId, title, assessmentType, academicYear,
                    examDate, durationMinutes, maxMarks, instructions, questionIds);

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Question Paper '" + paper.getTitle() + "' saved successfully!");
            return "redirect:/paper-generator/view/" + paper.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Error saving paper: " + e.getMessage());
            return "redirect:/paper-generator";
        }
    }

    @GetMapping("/paper-generator/view/{id}")
    public String viewPaper(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return paperService.getPaperById(id).map(paper -> {
            model.addAttribute("paper", paper);

            Map<String, Integer> coBreakdown = new LinkedHashMap<>();
            Map<String, Integer> bloomBreakdown = new LinkedHashMap<>();

            for (Question q : paper.getQuestions()) {
                String coCode = (q.getCourseOutcome() != null) ? q.getCourseOutcome().getCoCode() : "Unassigned";
                coBreakdown.put(coCode, coBreakdown.getOrDefault(coCode, 0) + q.getMarks());

                String bloom = (q.getBloomLevel() != null) ? q.getBloomLevel() : "General";
                bloomBreakdown.put(bloom, bloomBreakdown.getOrDefault(bloom, 0) + q.getMarks());
            }

            model.addAttribute("coBreakdown", coBreakdown);
            model.addAttribute("bloomBreakdown", bloomBreakdown);
            return "paper-view";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Question Paper not found.");
            return "redirect:/paper-generator";
        });
    }

    @GetMapping("/paper-generator/delete/{id}")
    public String deletePaper(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            paperService.deletePaper(id);
            redirectAttributes.addFlashAttribute("successMessage", "Question paper deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting paper: " + e.getMessage());
        }
        return "redirect:/paper-generator";
    }

    private void populateFormAttributes(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("cos", courseOutcomeRepository.findAll());
        model.addAttribute("bloomLevels", QuestionService.BLOOM_LEVELS);
        model.addAttribute("savedPapers", paperService.getAllPapers());
    }
}

