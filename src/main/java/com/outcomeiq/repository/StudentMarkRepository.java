package com.outcomeiq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.outcomeiq.entity.StudentMark;
import java.util.Optional;

public interface StudentMarkRepository extends JpaRepository<StudentMark, Long> {

    boolean existsByQuestion_Id(Long questionId);

    boolean existsByStudent_IdAndQuestion_Id(
            Long studentId, Long questionId);

    Optional<StudentMark> findByStudent_IdAndQuestion_Id(
            Long studentId, Long questionId);
}
