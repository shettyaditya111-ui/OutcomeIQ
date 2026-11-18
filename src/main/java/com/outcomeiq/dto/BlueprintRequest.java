package com.outcomeiq.dto;

import java.util.ArrayList;
import java.util.List;

public class BlueprintRequest {

    private Long courseId;
    private String title = "Continuous Internal Evaluation";
    private String assessmentType = "ALL";
    private String academicYear = "2026-2027";
    private String examDate;
    private int durationMinutes = 90;
    private int targetMarks = 50;
    private String instructions = "1. Answer all questions.\n2. Figures to the right indicate maximum marks.\n3. Assume suitable data wherever necessary.";
    private List<Long> coIds = new ArrayList<>();
    private List<String> bloomLevels = new ArrayList<>();

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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

    public int getTargetMarks() {
        return targetMarks;
    }

    public void setTargetMarks(int targetMarks) {
        this.targetMarks = targetMarks;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<Long> getCoIds() {
        return coIds;
    }

    public void setCoIds(List<Long> coIds) {
        this.coIds = coIds;
    }

    public List<String> getBloomLevels() {
        return bloomLevels;
    }

    public void setBloomLevels(List<String> bloomLevels) {
        this.bloomLevels = bloomLevels;
    }
}

