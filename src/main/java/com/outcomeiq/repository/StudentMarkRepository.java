package com.outcomeiq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.outcomeiq.entity.StudentMark;

public interface StudentMarkRepository extends JpaRepository<StudentMark, Long> {

    boolean existsByQuestion_Id(Long questionId);

    boolean existsByStudent_IdAndQuestion_Id(
            Long studentId, Long questionId);
}