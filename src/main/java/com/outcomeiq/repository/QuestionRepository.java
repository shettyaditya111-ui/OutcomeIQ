package com.outcomeiq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.outcomeiq.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

}