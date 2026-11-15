package com.outcomeiq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.outcomeiq.entity.CoPoMapping;

public interface CoPoMappingRepository extends JpaRepository<CoPoMapping, Long> {
}