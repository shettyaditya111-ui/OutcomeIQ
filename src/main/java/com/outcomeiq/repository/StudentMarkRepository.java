package com.outcomeiq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.outcomeiq.entity.StudentMark;

public interface StudentMarkRepository extends JpaRepository<StudentMark, Long> {

}