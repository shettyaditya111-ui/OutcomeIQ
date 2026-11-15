package com.outcomeiq.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "co_po_mappings",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"co_id", "po_id"}))
public class CoPoMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "co_id", nullable = false)
    private CourseOutcome courseOutcome;

    @ManyToOne
    @JoinColumn(name = "po_id", nullable = false)
    private ProgramOutcome programOutcome;

    private int mappingStrength;

    public Long getId() {
        return id;
    }

    public CourseOutcome getCourseOutcome() {
        return courseOutcome;
    }

    public void setCourseOutcome(CourseOutcome courseOutcome) {
        this.courseOutcome = courseOutcome;
    }

    public ProgramOutcome getProgramOutcome() {
        return programOutcome;
    }

    public void setProgramOutcome(ProgramOutcome programOutcome) {
        this.programOutcome = programOutcome;
    }

    public int getMappingStrength() {
        return mappingStrength;
    }

    public void setMappingStrength(int mappingStrength) {
        this.mappingStrength = mappingStrength;
    }
}