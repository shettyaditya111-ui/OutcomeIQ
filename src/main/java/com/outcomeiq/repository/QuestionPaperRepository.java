package com.outcomeiq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.outcomeiq.entity.QuestionPaper;
import java.util.List;

public interface QuestionPaperRepository extends JpaRepository<QuestionPaper, Long> {

    List<QuestionPaper> findAllByOrderByCreatedAtDesc();

    List<QuestionPaper> findByCourse_IdOrderByCreatedAtDesc(Long courseId);
}

