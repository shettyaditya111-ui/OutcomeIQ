package com.outcomeiq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.outcomeiq.entity.Question;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByCourse_Id(Long courseId);

    List<Question> findByCourse_IdAndAssessmentType(Long courseId, String assessmentType);

    List<Question> findByCourse_IdAndCourseOutcome_Id(Long courseId, Long coId);
}