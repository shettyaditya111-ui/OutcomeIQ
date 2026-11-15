package com.outcomeiq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.outcomeiq.entity.CourseOutcome;

public interface CourseOutcomeRepository
        extends JpaRepository<CourseOutcome, Long> {
}