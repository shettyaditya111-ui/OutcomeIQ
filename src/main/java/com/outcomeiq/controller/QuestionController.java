package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.outcomeiq.entity.Question;
import com.outcomeiq.entity.Course;
import com.outcomeiq.entity.CourseOutcome;
import com.outcomeiq.repository.QuestionRepository;
import com.outcomeiq.repository.CourseRepository;
import com.outcomeiq.repository.CourseOutcomeRepository;

@Controller
public class QuestionController {

    private final QuestionRepository questionRepo;
    private final CourseRepository courseRepo;
    private final CourseOutcomeRepository coRepo;

    public QuestionController(QuestionRepository questionRepo,
                              CourseRepository courseRepo,
                              CourseOutcomeRepository coRepo) {
        this.questionRepo = questionRepo;
        this.courseRepo = courseRepo;
        this.coRepo = coRepo;
    }

    @GetMapping("/questions")
    public String questions(Model model) {
        model.addAttribute("questions", questionRepo.findAll());
        model.addAttribute("courses", courseRepo.findAll());
        model.addAttribute("cos", coRepo.findAll());
        return "questions";
    }

    @PostMapping("/addQuestion")
    public String addQuestion(@ModelAttribute Question question,
                              @RequestParam Long courseId,
                              @RequestParam Long coId) {

        Course course = courseRepo.findById(courseId).orElse(null);
        CourseOutcome co = coRepo.findById(coId).orElse(null);

        if (course != null && co != null &&
            co.getCourse().getId().equals(course.getId()) &&
            question.getMarks() > 0) {

            question.setCourse(course);
            question.setCourseOutcome(co);
            questionRepo.save(question);
        }

        return "redirect:/questions";
    }

    @GetMapping("/deleteQuestion/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        questionRepo.deleteById(id);
        return "redirect:/questions";
    }
}
