package com.outcomeiq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.outcomeiq.entity.ProgramOutcome;

public interface ProgramOutcomeRepository extends JpaRepository<ProgramOutcome, Long> {
}