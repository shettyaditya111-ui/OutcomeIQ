
package com.outcomeiq.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outcomeiq.entity.CoPoMapping;
import com.outcomeiq.entity.CourseOutcome;
import com.outcomeiq.entity.ProgramOutcome;
import com.outcomeiq.repository.CoPoMappingRepository;
import com.outcomeiq.repository.CourseOutcomeRepository;
import com.outcomeiq.repository.ProgramOutcomeRepository;

@Service
@Transactional
public class CoPoMappingService {

    private final CoPoMappingRepository mappingRepository;
    private final CourseOutcomeRepository courseOutcomeRepository;
    private final ProgramOutcomeRepository programOutcomeRepository;

    public CoPoMappingService(
            CoPoMappingRepository mappingRepository,
            CourseOutcomeRepository courseOutcomeRepository,
            ProgramOutcomeRepository programOutcomeRepository) {

        this.mappingRepository = mappingRepository;
        this.courseOutcomeRepository = courseOutcomeRepository;
        this.programOutcomeRepository = programOutcomeRepository;
    }

    @Transactional(readOnly = true)
    public List<CoPoMapping> getAllMappings() {
        return mappingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CourseOutcome> getAllCourseOutcomes() {
        return courseOutcomeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ProgramOutcome> getAllProgramOutcomes() {
        return programOutcomeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CoPoMapping getMappingById(Long id) {
        return mappingRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Mapping not found."));
    }

    public CoPoMapping addMapping(
            Long coId, Long poId, int mappingStrength) {

        validateMappingStrength(mappingStrength);

        CourseOutcome courseOutcome = courseOutcomeRepository.findById(coId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Course Outcome not found."));

        ProgramOutcome programOutcome = programOutcomeRepository.findById(poId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Program Outcome not found."));

        if (mappingRepository
                .existsByCourseOutcome_IdAndProgramOutcome_Id(coId, poId)) {
            throw new IllegalArgumentException(
                    "This CO-PO mapping already exists.");
        }

        CoPoMapping mapping = new CoPoMapping();
        mapping.setCourseOutcome(courseOutcome);
        mapping.setProgramOutcome(programOutcome);
        mapping.setMappingStrength(mappingStrength);

        return mappingRepository.save(mapping);
    }

    public CoPoMapping updateMapping(
            Long id, Long coId, Long poId, int mappingStrength) {

        validateMappingStrength(mappingStrength);

        CoPoMapping existing = getMappingById(id);

        CourseOutcome courseOutcome = courseOutcomeRepository.findById(coId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Course Outcome not found."));

        ProgramOutcome programOutcome = programOutcomeRepository.findById(poId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Program Outcome not found."));

        boolean duplicate = mappingRepository
                .existsByCourseOutcome_IdAndProgramOutcome_Id(coId, poId);

        boolean sameMapping = existing.getCourseOutcome().getId().equals(coId)
                && existing.getProgramOutcome().getId().equals(poId);

        if (duplicate && !sameMapping) {
            throw new IllegalArgumentException(
                    "This CO-PO mapping already exists.");
        }

        existing.setCourseOutcome(courseOutcome);
        existing.setProgramOutcome(programOutcome);
        existing.setMappingStrength(mappingStrength);

        return mappingRepository.save(existing);
    }

    private void validateMappingStrength(int mappingStrength) {
        if (mappingStrength < 1 || mappingStrength > 3) {
            throw new IllegalArgumentException(
                    "Mapping strength must be between 1 and 3.");
        }
    }

    public void deleteMapping(Long id) {
        if (!mappingRepository.existsById(id)) {
            throw new IllegalArgumentException("Mapping not found.");
        }

        mappingRepository.deleteById(id);
    }
}
