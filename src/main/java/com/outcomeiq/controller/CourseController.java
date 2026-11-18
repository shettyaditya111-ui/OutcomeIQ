package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.outcomeiq.entity.Course;
import com.outcomeiq.service.CourseService;

@Controller
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "courses";
    }

    @PostMapping("/addCourse")
    public String addCourse(Course course, RedirectAttributes redirectAttributes) {
        try {
            courseService.addCourse(course);
            redirectAttributes.addFlashAttribute("successMessage", "Course added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding course: " + e.getMessage());
        }
        return "redirect:/courses";
    }

    @PostMapping("/updateCourse")
    public String updateCourse(@RequestParam Long id, Course course, RedirectAttributes redirectAttributes) {
        try {
            courseService.updateCourse(id, course);
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating course: " + e.getMessage());
        }
        return "redirect:/courses";
    }

    @GetMapping("/deleteCourse/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting course: " + e.getMessage());
        }
        return "redirect:/courses";
    }
}
