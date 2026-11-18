
package com.outcomeiq.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outcomeiq.entity.Course;
import com.outcomeiq.entity.CourseOutcome;
import com.outcomeiq.repository.CourseRepository;
import com.outcomeiq.repository.CourseOutcomeRepository;

@Service
@Transactional
public class CourseOutcomeService {

    private final CourseOutcomeRepository courseOutcomeRepository;
    private final CourseRepository courseRepository;

    public CourseOutcomeService(
            CourseOutcomeRepository courseOutcomeRepository,
            CourseRepository courseRepository) {
        this.courseOutcomeRepository = courseOutcomeRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseOutcome> getAllCourseOutcomes() {
        return courseOutcomeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CourseOutcome getCourseOutcomeById(Long id) {
        return courseOutcomeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Course Outcome not found."));
    }

    public CourseOutcome addCourseOutcome(
            String coCode, String description, Long courseId) {

        validateCoDetails(coCode, description);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Selected course does not exist."));

        CourseOutcome courseOutcome = new CourseOutcome();
        courseOutcome.setCoCode(coCode.trim());
        courseOutcome.setDescription(description.trim());
        courseOutcome.setCourse(course);

        return courseOutcomeRepository.save(courseOutcome);
    }

    public CourseOutcome updateCourseOutcome(
            Long id, String coCode, String description, Long courseId) {

        validateCoDetails(coCode, description);

        CourseOutcome existing = getCourseOutcomeById(id);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Selected course does not exist."));

        existing.setCoCode(coCode.trim());
        existing.setDescription(description.trim());
        existing.setCourse(course);

        return courseOutcomeRepository.save(existing);
    }

    private void validateCoDetails(String coCode, String description) {
        if (coCode == null || coCode.isBlank()) {
            throw new IllegalArgumentException("CO code cannot be empty.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("CO description cannot be empty.");
        }
    }

    public void deleteCourseOutcome(Long id) {
        if (!courseOutcomeRepository.existsById(id)) {
            throw new IllegalArgumentException("Course Outcome not found.");
        }

        courseOutcomeRepository.deleteById(id);
    }
}
