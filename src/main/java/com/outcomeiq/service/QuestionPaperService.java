package com.outcomeiq.service;

import com.outcomeiq.dto.BlueprintRequest;
import com.outcomeiq.dto.BlueprintResult;
import com.outcomeiq.entity.Course;
import com.outcomeiq.entity.CourseOutcome;
import com.outcomeiq.entity.Question;
import com.outcomeiq.entity.QuestionPaper;
import com.outcomeiq.repository.CourseRepository;
import com.outcomeiq.repository.QuestionPaperRepository;
import com.outcomeiq.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionPaperService {

    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final QuestionPaperRepository paperRepository;

    public QuestionPaperService(
            QuestionRepository questionRepository,
            CourseRepository courseRepository,
            QuestionPaperRepository paperRepository) {
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
        this.paperRepository = paperRepository;
    }

    @Transactional(readOnly = true)
    public List<QuestionPaper> getAllPapers() {
        return paperRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Optional<QuestionPaper> getPaperById(Long id) {
        return paperRepository.findById(id);
    }

    public void deletePaper(Long id) {
        paperRepository.deleteById(id);
    }

    public QuestionPaper savePaper(
            Long courseId,
            String title,
            String assessmentType,
            String academicYear,
            String examDate,
            int durationMinutes,
            int maxMarks,
            String instructions,
            List<Long> questionIds) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        List<Question> selectedQuestions = questionRepository.findAllById(questionIds);
        int totalMarks = selectedQuestions.stream().mapToInt(Question::getMarks).sum();

        QuestionPaper paper = new QuestionPaper();
        paper.setTitle(title);
        paper.setCourse(course);
        paper.setAssessmentType(assessmentType);
        paper.setAcademicYear(academicYear);
        paper.setExamDate(examDate);
        paper.setDurationMinutes(durationMinutes);
        paper.setMaxMarks(maxMarks);
        paper.setTotalMarks(totalMarks);
        paper.setInstructions(instructions);
        paper.setCreatedAt(LocalDateTime.now());
        paper.setQuestions(selectedQuestions);

        return paperRepository.save(paper);
    }

    @Transactional(readOnly = true)
    public BlueprintResult generatePaper(BlueprintRequest request) {
        BlueprintResult result = new BlueprintResult();
        result.setTargetMarks(request.getTargetMarks());
        result.setTitle(request.getTitle());
        result.setAssessmentType(request.getAssessmentType());
        result.setAcademicYear(request.getAcademicYear());
        result.setExamDate(request.getExamDate());
        result.setDurationMinutes(request.getDurationMinutes());
        result.setInstructions(request.getInstructions());

        if (request.getCourseId() == null) {
            result.setSuccess(false);
            result.setMessage("Please select a Course to generate the question paper.");
            return result;
        }

        Course course = courseRepository.findById(request.getCourseId()).orElse(null);
        if (course == null) {
            result.setSuccess(false);
            result.setMessage("Selected Course does not exist.");
            return result;
        }
        result.setCourse(course);

        // Fetch candidate questions
        List<Question> pool = questionRepository.findByCourse_Id(course.getId());
        if (pool.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("No questions found in Question Bank for " + course.getCourseCode() + " - " + course.getCourseName() + ". Please add questions first.");
            return result;
        }

        // Filter by Assessment Type if specified
        if (request.getAssessmentType() != null && !"ALL".equalsIgnoreCase(request.getAssessmentType())) {
            pool = pool.stream()
                    .filter(q -> q.getAssessmentType() != null && q.getAssessmentType().equalsIgnoreCase(request.getAssessmentType()))
                    .collect(Collectors.toList());
        }

        // Filter by Selected COs if specified
        if (request.getCoIds() != null && !request.getCoIds().isEmpty()) {
            Set<Long> allowedCoIds = new HashSet<>(request.getCoIds());
            pool = pool.stream()
                    .filter(q -> q.getCourseOutcome() != null && allowedCoIds.contains(q.getCourseOutcome().getId()))
                    .collect(Collectors.toList());
        }

        // Filter by Selected Bloom Levels if specified
        if (request.getBloomLevels() != null && !request.getBloomLevels().isEmpty()) {
            Set<String> allowedBloom = new HashSet<>(request.getBloomLevels());
            pool = pool.stream()
                    .filter(q -> q.getBloomLevel() != null && allowedBloom.contains(q.getBloomLevel()))
                    .collect(Collectors.toList());
        }

        if (pool.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("No questions matched your filter criteria (CO / Bloom / Assessment Type). Try broadening your selection.");
            return result;
        }

        // Shuffle pool for randomized diversity on each click
        Collections.shuffle(pool, new Random());

        // Blueprint selection algorithm
        List<Question> selected = selectQuestionsForBlueprint(pool, request.getTargetMarks());

        // Sort questions logically: by marks ascending (Part A short questions first, then Part B long questions)
        selected.sort(Comparator.comparingInt(Question::getMarks).thenComparing(q -> q.getCourseOutcome() != null ? q.getCourseOutcome().getCoCode() : ""));

        int totalCalculated = selected.stream().mapToInt(Question::getMarks).sum();
        result.setQuestions(selected);
        result.setTotalMarks(totalCalculated);

        // Compute Breakdown
        Map<String, Integer> coBreakdown = new LinkedHashMap<>();
        Map<String, Integer> bloomBreakdown = new LinkedHashMap<>();

        for (Question q : selected) {
            String coCode = (q.getCourseOutcome() != null) ? q.getCourseOutcome().getCoCode() : "Unassigned";
            coBreakdown.put(coCode, coBreakdown.getOrDefault(coCode, 0) + q.getMarks());

            String bloom = (q.getBloomLevel() != null) ? q.getBloomLevel() : "General";
            bloomBreakdown.put(bloom, bloomBreakdown.getOrDefault(bloom, 0) + q.getMarks());
        }

        result.setCoBreakdown(coBreakdown);
        result.setBloomBreakdown(bloomBreakdown);

        if (totalCalculated == request.getTargetMarks()) {
            result.setSuccess(true);
            result.setMessage("Successfully generated blueprint-compliant question paper with exact target marks (" + totalCalculated + " marks).");
        } else {
            result.setSuccess(true);
            result.setMessage("Generated question paper with closest available combination (" + totalCalculated + " / " + request.getTargetMarks() + " marks). Add more questions to the Question Bank for exact target match.");
        }

        return result;
    }

    /**
     * Balances questions across Course Outcomes and solves subset-sum closest to targetMarks.
     */
    private List<Question> selectQuestionsForBlueprint(List<Question> pool, int targetMarks) {
        int poolTotal = pool.stream().mapToInt(Question::getMarks).sum();
        if (poolTotal <= targetMarks) {
            return new ArrayList<>(pool);
        }

        // Group pool by CO to ensure round-robin representation
        Map<Long, List<Question>> byCo = pool.stream()
                .filter(q -> q.getCourseOutcome() != null)
                .collect(Collectors.groupingBy(q -> q.getCourseOutcome().getId()));

        List<Question> chosen = new ArrayList<>();
        int currentSum = 0;

        // Phase 1: Round-robin pick from each CO to ensure all COs are represented
        List<Long> coKeys = new ArrayList<>(byCo.keySet());
        Collections.shuffle(coKeys);

        boolean addedAny = true;
        Set<Long> usedQuestionIds = new HashSet<>();

        while (addedAny && currentSum < targetMarks) {
            addedAny = false;
            for (Long coId : coKeys) {
                List<Question> coList = byCo.get(coId);
                for (Question q : coList) {
                    if (!usedQuestionIds.contains(q.getId()) && (currentSum + q.getMarks() <= targetMarks)) {
                        chosen.add(q);
                        usedQuestionIds.add(q.getId());
                        currentSum += q.getMarks();
                        addedAny = true;
                        break;
                    }
                }
                if (currentSum >= targetMarks) break;
            }
        }

        // Phase 2: If we have not hit exact targetMarks, search remaining pool to find exact gap or closest
        if (currentSum < targetMarks) {
            int gap = targetMarks - currentSum;
            // Look for a single question that fills the gap exactly
            Optional<Question> exactFiller = pool.stream()
                    .filter(q -> !usedQuestionIds.contains(q.getId()) && q.getMarks() == gap)
                    .findFirst();

            if (exactFiller.isPresent()) {
                chosen.add(exactFiller.get());
                currentSum += exactFiller.get().getMarks();
            } else {
                // Try small questions that can fit
                List<Question> remaining = pool.stream()
                        .filter(q -> !usedQuestionIds.contains(q.getId()))
                        .sorted(Comparator.comparingInt(Question::getMarks))
                        .collect(Collectors.toList());

                for (Question q : remaining) {
                    if (currentSum + q.getMarks() <= targetMarks) {
                        chosen.add(q);
                        currentSum += q.getMarks();
                    }
                }
            }
        }

        return chosen;
    }
}

