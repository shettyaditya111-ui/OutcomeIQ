package com.outcomeiq.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outcomeiq.entity.Question;
import com.outcomeiq.entity.Student;
import com.outcomeiq.entity.StudentMark;
import com.outcomeiq.repository.QuestionRepository;
import com.outcomeiq.repository.StudentMarkRepository;
import com.outcomeiq.repository.StudentRepository;

@Service
@Transactional
public class StudentMarkService {

    private final StudentMarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final QuestionRepository questionRepository;

    public StudentMarkService(
            StudentMarkRepository markRepository,
            StudentRepository studentRepository,
            QuestionRepository questionRepository) {

        this.markRepository = markRepository;
        this.studentRepository = studentRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentMark> getAllMarks() {
        return markRepository.findAll();
    }

    @Transactional(readOnly = true)
    public StudentMark getMarkById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Mark record ID is required.");
        }

        return markRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Mark record not found."));
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<StudentMark> findMarkByStudentAndQuestion(Long studentId, Long questionId) {
        if (studentId == null || questionId == null) {
            return Optional.empty();
        }
        return markRepository.findByStudent_IdAndQuestion_Id(studentId, questionId);
    }

    @Transactional(readOnly = true)
    public boolean existsMark(Long studentId, Long questionId) {
        if (studentId == null || questionId == null) {
            return false;
        }
        return markRepository.existsByStudent_IdAndQuestion_Id(studentId, questionId);
    }

    public StudentMark addMark(
            Long studentId,
            Long questionId,
            double marksObtained) {

        Student student = validateAndGetStudent(studentId);
        Question question = validateAndGetQuestion(questionId);

        validateMarks(marksObtained, question);

        if (markRepository.existsByStudent_IdAndQuestion_Id(studentId, questionId)) {
            throw new IllegalArgumentException(
                    "Marks have already been recorded for this student and question.");
        }

        StudentMark mark = new StudentMark();
        mark.setStudent(student);
        mark.setQuestion(question);
        mark.setMarksObtained(marksObtained);

        return markRepository.save(mark);
    }

    public StudentMark saveOrUpdateMark(
            Long studentId,
            Long questionId,
            double marksObtained) {

        Student student = validateAndGetStudent(studentId);
        Question question = validateAndGetQuestion(questionId);

        validateMarks(marksObtained, question);

        StudentMark mark = markRepository.findByStudent_IdAndQuestion_Id(studentId, questionId)
                .orElseGet(() -> {
                    StudentMark newMark = new StudentMark();
                    newMark.setStudent(student);
                    newMark.setQuestion(question);
                    return newMark;
                });

        mark.setStudent(student);
        mark.setQuestion(question);
        mark.setMarksObtained(marksObtained);

        return markRepository.save(mark);
    }

    public StudentMark updateMark(
            Long id,
            Long studentId,
            Long questionId,
            double marksObtained) {

        StudentMark mark = getMarkById(id);

        Student student = validateAndGetStudent(studentId);
        Question question = validateAndGetQuestion(questionId);

        validateMarks(marksObtained, question);

        boolean duplicate = markRepository.findAll().stream()
                .anyMatch(existing ->
                        !existing.getId().equals(id)
                        && existing.getStudent().getId().equals(studentId)
                        && existing.getQuestion().getId().equals(questionId));

        if (duplicate) {
            throw new IllegalArgumentException(
                    "Marks have already been recorded for this student and question.");
        }

        mark.setStudent(student);
        mark.setQuestion(question);
        mark.setMarksObtained(marksObtained);

        return markRepository.save(mark);
    }

    public void deleteMark(Long id) {
        if (id == null || !markRepository.existsById(id)) {
            throw new IllegalArgumentException("Mark record not found.");
        }

        markRepository.deleteById(id);
    }

    private Student validateAndGetStudent(Long studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("Select a student.");
        }

        return studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found."));
    }

    private Question validateAndGetQuestion(Long questionId) {
        if (questionId == null) {
            throw new IllegalArgumentException("Select a question.");
        }

        return questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Question not found."));
    }

    private void validateMarks(double marksObtained, Question question) {
        if (!Double.isFinite(marksObtained) || marksObtained < 0) {
            throw new IllegalArgumentException(
                    "Marks must be a valid non-negative number.");
        }

        if (marksObtained > question.getMarks()) {
            throw new IllegalArgumentException(
                    "Marks obtained (" + marksObtained + ") cannot exceed maximum marks (" + question.getMarks() + ").");
        }

        if (question.getCourse() == null
                || question.getCourse().getId() == null) {
            throw new IllegalArgumentException(
                    "The question is not associated with a valid course.");
        }
    }
}
