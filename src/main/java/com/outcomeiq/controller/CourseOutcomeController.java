package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.outcomeiq.entity.Course;
import com.outcomeiq.entity.CourseOutcome;
import com.outcomeiq.repository.CourseRepository;
import com.outcomeiq.repository.CourseOutcomeRepository;

@Controller
public class CourseOutcomeController {

    private final CourseOutcomeRepository coRepo;
    private final CourseRepository courseRepo;

    public CourseOutcomeController(
            CourseOutcomeRepository coRepo,
            CourseRepository courseRepo) {
        this.coRepo = coRepo;
        this.courseRepo = courseRepo;
    }

    @GetMapping("/cos")
    public String showCOs(Model model) {
        model.addAttribute("cos", coRepo.findAll());
        model.addAttribute("courses", courseRepo.findAll());
        return "cos";
    }

    @PostMapping("/addCO")
    public String addCO(
            @RequestParam String coCode,
            @RequestParam String description,
            @RequestParam Long courseId) {

        Course course = courseRepo.findById(courseId).orElseThrow();

        CourseOutcome co = new CourseOutcome();
        co.setCoCode(coCode);
        co.setDescription(description);
        co.setCourse(course);

        coRepo.save(co);

        return "redirect:/cos";
    }

    @GetMapping("/deleteCO/{id}")
    public String deleteCO(@PathVariable Long id) {
        coRepo.deleteById(id);
        return "redirect:/cos";
    }
}