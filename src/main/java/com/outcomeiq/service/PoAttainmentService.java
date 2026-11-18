
package com.outcomeiq.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outcomeiq.entity.*;
import com.outcomeiq.repository.*;

@Service
@Transactional(readOnly = true)
public class PoAttainmentService {

    private final ProgramOutcomeRepository poRepository;
    private final CoPoMappingRepository mappingRepository;
    private final QuestionRepository questionRepository;
    private final StudentMarkRepository markRepository;
    private final StudentRepository studentRepository;

    public PoAttainmentService(
            ProgramOutcomeRepository poRepository,
            CoPoMappingRepository mappingRepository,
            QuestionRepository questionRepository,
            StudentMarkRepository markRepository,
            StudentRepository studentRepository) {

        this.poRepository = poRepository;
        this.mappingRepository = mappingRepository;
        this.questionRepository = questionRepository;
        this.markRepository = markRepository;
        this.studentRepository = studentRepository;
    }

    public record Result(
            String poCode,
            String description,
            double attainment,
            int level,
            int mappedCOs) {}

    public List<Result> calculate() {

        List<Question> questions = questionRepository.findAll();
        List<StudentMark> marks = markRepository.findAll();
        List<Student> students = studentRepository.findAll();
        List<CoPoMapping> mappings = mappingRepository.findAll();
        List<ProgramOutcome> programOutcomes = poRepository.findAll();

        Map<Long, List<Question>> questionsByCO = questions.stream()
                .filter(q -> q != null
                        && q.getCourseOutcome() != null
                        && q.getCourseOutcome().getId() != null)
                .collect(Collectors.groupingBy(
                        q -> q.getCourseOutcome().getId()));

        Map<Long, List<StudentMark>> marksByQuestion = marks.stream()
                .filter(m -> m != null
                        && m.getQuestion() != null
                        && m.getQuestion().getId() != null)
                .collect(Collectors.groupingBy(
                        m -> m.getQuestion().getId()));

        Map<Long, Double> coAttainment = new HashMap<>();

        for (Map.Entry<Long, List<Question>> entry : questionsByCO.entrySet()) {

            List<Question> coQuestions = entry.getValue();

            double maximumMarks = coQuestions.stream()
                    .mapToDouble(question -> question.getMarks())
                    .sum();

            if (maximumMarks <= 0) {
                continue;
            }

            Question firstQuestion = coQuestions.get(0);

            Long courseId = firstQuestion.getCourse() == null
                    ? null
                    : firstQuestion.getCourse().getId();

            if (courseId == null) {
                continue;
            }

            Set<Long> courseStudentIds = students.stream()
                    .filter(student -> student != null
                            && student.getId() != null
                            && student.getCourse() != null
                            && student.getCourse().getId() != null
                            && student.getCourse().getId().equals(courseId))
                    .map(student -> student.getId())
                    .collect(Collectors.toSet());

            if (courseStudentIds.isEmpty()) {
                continue;
            }

            long studentsMeetingTarget = 0;

            for (Long studentId : courseStudentIds) {

                double obtained = 0;

                for (Question question : coQuestions) {

                    List<StudentMark> questionMarks =
                            marksByQuestion.getOrDefault(
                                    question.getId(), List.of());

                    obtained += questionMarks.stream()
                            .filter(mark -> mark.getStudent() != null
                                    && mark.getStudent().getId() != null
                                    && mark.getStudent().getId().equals(studentId))
                            .mapToDouble(mark -> mark.getMarksObtained())
                            .sum();
                }

                if (obtained >= maximumMarks * 0.60) {
                    studentsMeetingTarget++;
                }
            }

            double percentage =
                    studentsMeetingTarget * 100.0 / courseStudentIds.size();

            coAttainment.put(entry.getKey(), percentage);
        }

        List<Result> results = new ArrayList<>();

        for (ProgramOutcome po : programOutcomes) {

            if (po == null || po.getId() == null) {
                continue;
            }

            List<CoPoMapping> poMappings = mappings.stream()
                    .filter(mapping -> mapping != null
                            && mapping.getProgramOutcome() != null
                            && mapping.getProgramOutcome().getId() != null
                            && mapping.getProgramOutcome().getId().equals(po.getId())
                            && mapping.getCourseOutcome() != null
                            && mapping.getCourseOutcome().getId() != null)
                    .toList();

            double weightedTotal = 0;
            int totalWeight = 0;
            int includedCOs = 0;

            for (CoPoMapping mapping : poMappings) {

                Long coId = mapping.getCourseOutcome().getId();

                Double attainment = coAttainment.get(coId);

                if (attainment == null) {
                    continue;
                }

                int weight = mapping.getMappingStrength();

                if (weight <= 0) {
                    continue;
                }

                weightedTotal += attainment * weight;
                totalWeight += weight;
                includedCOs++;
            }

            double percentage = totalWeight == 0
                    ? 0
                    : weightedTotal / totalWeight;

            int level = percentage < 40 ? 0
                    : percentage < 60 ? 1
                    : percentage < 80 ? 2 : 3;

            results.add(new Result(
                    po.getPoCode(),
                    po.getDescription(),
                    Math.round(percentage * 100.0) / 100.0,
                    level,
                    includedCOs
            ));
        }

        return results;
    }
}
