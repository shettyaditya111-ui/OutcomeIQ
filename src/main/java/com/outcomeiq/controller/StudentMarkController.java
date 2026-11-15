package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.outcomeiq.entity.Student;
import com.outcomeiq.entity.Question;
import com.outcomeiq.entity.StudentMark;

import com.outcomeiq.repository.StudentRepository;
import com.outcomeiq.repository.QuestionRepository;
import com.outcomeiq.repository.StudentMarkRepository;

@Controller
public class StudentMarkController {

    private final StudentMarkRepository markRepo;
    private final StudentRepository studentRepo;
    private final QuestionRepository questionRepo;

    public StudentMarkController(StudentMarkRepository markRepo,
                                 StudentRepository studentRepo,
                                 QuestionRepository questionRepo) {
        this.markRepo = markRepo;
        this.studentRepo = studentRepo;
        this.questionRepo = questionRepo;
    }

    @GetMapping("/marks")
    public String marks(Model model) {
        model.addAttribute("marks", markRepo.findAll());
        model.addAttribute("students", studentRepo.findAll());
        model.addAttribute("questions", questionRepo.findAll());
        return "marks";
    }

    @PostMapping("/addMark")
    public String addMark(@RequestParam Long studentId,
                          @RequestParam Long questionId,
                          @RequestParam double marksObtained) {

        Student student = studentRepo.findById(studentId).orElse(null);
        Question question = questionRepo.findById(questionId).orElse(null);

        if (student != null && question != null
                && student.getCourse().getId().equals(question.getCourse().getId())
                && marksObtained >= 0
                && marksObtained <= question.getMarks()) {

            StudentMark mark = new StudentMark();
            mark.setStudent(student);
            mark.setQuestion(question);
            mark.setMarksObtained(marksObtained);

            markRepo.save(mark);
        }

        return "redirect:/marks";
    }

    @GetMapping("/deleteMark/{id}")
    public String deleteMark(@PathVariable Long id) {
        markRepo.deleteById(id);
        return "redirect:/marks";
    }
}