
package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.outcomeiq.entity.Student;
import com.outcomeiq.entity.Course;
import com.outcomeiq.repository.StudentRepository;
import com.outcomeiq.repository.CourseRepository;

@Controller
public class StudentController {

    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;

    public StudentController(StudentRepository studentRepo,
                             CourseRepository courseRepo) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
    }

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", studentRepo.findAll());
        model.addAttribute("courses", courseRepo.findAll());
        return "students";
    }

    @PostMapping("/addStudent")
    public String addStudent(@ModelAttribute Student student,
                             @RequestParam Long courseId) {

        Course course = courseRepo.findById(courseId).orElse(null);

        if (course != null) {
            student.setCourse(course);
            studentRepo.save(student);
        }

        return "redirect:/students";
    }

    @PostMapping("/updateStudent")
    public String updateStudent(@RequestParam Long id,
                                @RequestParam String usn,
                                @RequestParam String studentName,
                                @RequestParam Long courseId) {

        Student student = studentRepo.findById(id).orElse(null);
        Course course = courseRepo.findById(courseId).orElse(null);

        if (student != null && course != null) {
            student.setUsn(usn);
            student.setStudentName(studentName);
            student.setCourse(course);
            studentRepo.save(student);
        }

        return "redirect:/students";
    }

    @GetMapping("/deleteStudent/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentRepo.deleteById(id);
        return "redirect:/students";
    }
}
