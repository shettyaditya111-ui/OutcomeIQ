package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.outcomeiq.entity.Course;
import com.outcomeiq.repository.CourseRepository;

@Controller
public class CourseController {

    private final CourseRepository repo;

    public CourseController(CourseRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", repo.findAll());
        return "courses";
    }

    @PostMapping("/addCourse")
    public String addCourse(Course course) {
        repo.save(course);
        return "redirect:/courses";
    }

    @GetMapping("/deleteCourse/{id}")
    public String deleteCourse(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/courses";
    }
}
