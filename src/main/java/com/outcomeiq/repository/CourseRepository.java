package com.outcomeiq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.outcomeiq.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}