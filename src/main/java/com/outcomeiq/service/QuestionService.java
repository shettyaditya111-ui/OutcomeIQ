
package com.outcomeiq.service;

import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outcomeiq.entity.Course;
import com.outcomeiq.entity.CourseOutcome;
import com.outcomeiq.entity.Question;
import com.outcomeiq.repository.CourseOutcomeRepository;
import com.outcomeiq.repository.CourseRepository;
import com.outcomeiq.repository.QuestionRepository;
import com.outcomeiq.repository.StudentMarkRepository;

@Service
@Transactional
public class QuestionService {

    public static final List<String> BLOOM_LEVELS = List.of(
            "Remember", "Understand", "Apply",
            "Analyze", "Evaluate", "Create"
    );

    public static final List<String> ASSESSMENT_TYPES = List.of("CIE", "SEE");

    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final CourseOutcomeRepository courseOutcomeRepository;
    private final StudentMarkRepository studentMarkRepository;

    public QuestionService(
            QuestionRepository questionRepository,
            CourseRepository courseRepository,
            CourseOutcomeRepository courseOutcomeRepository,
            StudentMarkRepository studentMarkRepository) {

        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
        this.courseOutcomeRepository = courseOutcomeRepository;
        this.studentMarkRepository = studentMarkRepository;
    }

    @Transactional(readOnly = true)
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CourseOutcome> getAllCourseOutcomes() {
        return courseOutcomeRepository.findAll();
    }

    public Question addQuestion(
            String questionText,
            String bloomLevel,
            int marks,
            Integer yearUsed,
            String assessmentType,
            Long courseId,
            Long coId) {

        Course course = validateCourse(courseId);
        CourseOutcome co = validateCourseOutcome(coId, course);

        validateQuestionDetails(
                questionText, bloomLevel, marks, yearUsed, assessmentType);

        Question question = new Question();
        setQuestionDetails(
                question, questionText, bloomLevel, marks,
                yearUsed, assessmentType, course, co);

        return questionRepository.save(question);
    }

    public Question updateQuestion(
            Long id,
            String questionText,
            String bloomLevel,
            int marks,
            Integer yearUsed,
            String assessmentType,
            Long courseId,
            Long coId) {

        if (id == null) {
            throw new IllegalArgumentException("Question ID is required.");
        }

        Question question = questionRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Question not found."));

        Course course = validateCourse(courseId);
        CourseOutcome co = validateCourseOutcome(coId, course);

        validateQuestionDetails(
                questionText, bloomLevel, marks, yearUsed, assessmentType);

        setQuestionDetails(
                question, questionText, bloomLevel, marks,
                yearUsed, assessmentType, course, co);

        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        if (id == null || !questionRepository.existsById(id)) {
            throw new IllegalArgumentException("Question not found.");
        }

        if (studentMarkRepository.existsByQuestion_Id(id)) {
            throw new IllegalArgumentException(
                    "Cannot delete this question because student marks are recorded against it.");
        }

        questionRepository.deleteById(id);
    }

    private Course validateCourse(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("Select a course.");
        }

        return courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Course not found."));
    }

    private CourseOutcome validateCourseOutcome(
            Long coId, Course course) {

        if (coId == null) {
            throw new IllegalArgumentException("Select a Course Outcome.");
        }

        CourseOutcome co = courseOutcomeRepository.findById(coId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Course Outcome not found."));

        if (co.getCourse() == null
                || !co.getCourse().getId().equals(course.getId())) {
            throw new IllegalArgumentException(
                    "The selected CO does not belong to this course.");
        }

        return co;
    }

    private void validateQuestionDetails(
            String questionText,
            String bloomLevel,
            int marks,
            Integer yearUsed,
            String assessmentType) {

        if (questionText == null || questionText.isBlank()) {
            throw new IllegalArgumentException("Question text is required.");
        }

        if (questionText.trim().length() > 1000) {
            throw new IllegalArgumentException(
                    "Question text cannot exceed 1000 characters.");
        }

        if (bloomLevel == null
                || !BLOOM_LEVELS.contains(bloomLevel.trim())) {
            throw new IllegalArgumentException(
                    "Select a valid Bloom's taxonomy level.");
        }

        if (marks <= 0) {
            throw new IllegalArgumentException(
                    "Marks must be greater than zero.");
        }

        if (yearUsed != null
                && (yearUsed < 1900 || yearUsed > Year.now().getValue())) {
            throw new IllegalArgumentException(
                    "Enter a valid year in which the question was used.");
        }

        if (assessmentType == null
                || !ASSESSMENT_TYPES.contains(
                        assessmentType.trim().toUpperCase())) {
            throw new IllegalArgumentException(
                    "Assessment type must be CIE or SEE.");
        }
    }

    private void setQuestionDetails(
            Question question,
            String questionText,
            String bloomLevel,
            int marks,
            Integer yearUsed,
            String assessmentType,
            Course course,
            CourseOutcome co) {

        question.setQuestionText(questionText.trim());
        question.setBloomLevel(bloomLevel.trim());
        question.setMarks(marks);
        question.setYearUsed(yearUsed);
        question.setAssessmentType(assessmentType.trim().toUpperCase());
        question.setCourse(course);
        question.setCourseOutcome(co);
    }
}
