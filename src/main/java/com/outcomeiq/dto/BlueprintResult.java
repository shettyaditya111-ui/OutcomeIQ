package com.outcomeiq.dto;

import com.outcomeiq.entity.Course;
import com.outcomeiq.entity.Question;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlueprintResult {

    private boolean success;
    private String message;
    private List<Question> questions = new ArrayList<>();
    private int totalMarks;
    private int targetMarks;
    private Map<String, Integer> coBreakdown = new LinkedHashMap<>();
    private Map<String, Integer> bloomBreakdown = new LinkedHashMap<>();
    private Course course;
    private String title;
    private String assessmentType;
    private String academicYear;
    private String examDate;
    private int durationMinutes;
    private String instructions;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public int getTargetMarks() {
        return targetMarks;
    }

    public void setTargetMarks(int targetMarks) {
        this.targetMarks = targetMarks;
    }

    public Map<String, Integer> getCoBreakdown() {
        return coBreakdown;
    }

    public void setCoBreakdown(Map<String, Integer> coBreakdown) {
        this.coBreakdown = coBreakdown;
    }

    public Map<String, Integer> getBloomBreakdown() {
        return bloomBreakdown;
    }

    public void setBloomBreakdown(Map<String, Integer> bloomBreakdown) {
        this.bloomBreakdown = bloomBreakdown;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}

