package com.outcomeiq.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String questionText;

    private String bloomLevel;

    private int marks;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "co_id")
    private CourseOutcome courseOutcome;

    public Long getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getBloomLevel() {
        return bloomLevel;
    }

    public void setBloomLevel(String bloomLevel) {
        this.bloomLevel = bloomLevel;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseOutcome getCourseOutcome() {
        return courseOutcome;
    }

    public void setCourseOutcome(CourseOutcome courseOutcome) {
        this.courseOutcome = courseOutcome;
    }
}