
package com.outcomeiq.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outcomeiq.entity.Course;
import com.outcomeiq.repository.CourseRepository;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Course not found: " + id));
    }

    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course updatedCourse) {
        Course existingCourse = getCourseById(id);

        existingCourse.setCourseCode(updatedCourse.getCourseCode());
        existingCourse.setCourseName(updatedCourse.getCourseName());
        existingCourse.setSemester(updatedCourse.getSemester());

        return courseRepository.save(existingCourse);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new IllegalArgumentException("Course not found: " + id);
        }

        courseRepository.deleteById(id);
    }
}
