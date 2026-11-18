
package com.outcomeiq.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outcomeiq.entity.ProgramOutcome;
import com.outcomeiq.repository.ProgramOutcomeRepository;

@Service
@Transactional
public class ProgramOutcomeService {

    private final ProgramOutcomeRepository programOutcomeRepository;

    public ProgramOutcomeService(
            ProgramOutcomeRepository programOutcomeRepository) {
        this.programOutcomeRepository = programOutcomeRepository;
    }

    @Transactional(readOnly = true)
    public List<ProgramOutcome> getAllProgramOutcomes() {
        return programOutcomeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ProgramOutcome getProgramOutcomeById(Long id) {
        return programOutcomeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Program Outcome not found."));
    }

    public ProgramOutcome addProgramOutcome(
            String poCode, String description) {

        validatePoDetails(poCode, description);

        ProgramOutcome programOutcome = new ProgramOutcome();
        programOutcome.setPoCode(poCode.trim());
        programOutcome.setDescription(description.trim());

        return programOutcomeRepository.save(programOutcome);
    }

    public ProgramOutcome updateProgramOutcome(
            Long id, String poCode, String description) {

        validatePoDetails(poCode, description);

        ProgramOutcome existing = getProgramOutcomeById(id);
        existing.setPoCode(poCode.trim());
        existing.setDescription(description.trim());

        return programOutcomeRepository.save(existing);
    }

    private void validatePoDetails(String poCode, String description) {
        if (poCode == null || poCode.isBlank()) {
            throw new IllegalArgumentException("PO code cannot be empty.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("PO description cannot be empty.");
        }
    }

    public void deleteProgramOutcome(Long id) {
        if (!programOutcomeRepository.existsById(id)) {
            throw new IllegalArgumentException("Program Outcome not found.");
        }

        programOutcomeRepository.deleteById(id);
    }
}
