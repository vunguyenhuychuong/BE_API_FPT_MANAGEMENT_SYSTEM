package com.java8.tms.syllabus.dto;

import com.java8.tms.common.meta.SyllabusStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormSyllabusDTOWithoutId {
    boolean isTemplate;
    List<SyllabusDayDTOWithoutId> syllabusDays;
    private String name;
    private String code;
    private String version;
    private int attendeeNumber;
    private String technicalRequirement;
    private String courseObjective;
    private int days;
    private int hours;
    private AssessmentSchemeDTO assessmentScheme;
    private SyllabusLevelDTO syllabusLevel;
    private DeliveryPrincipleDTO deliveryPrinciple;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getAttendeeNumber() {
        return attendeeNumber;
    }

    public void setAttendeeNumber(int attendeeNumber) {
        this.attendeeNumber = attendeeNumber;
    }

    public String getTechnicalRequirement() {
        return technicalRequirement;
    }

    public void setTechnicalRequirement(String technicalRequirement) {
        this.technicalRequirement = technicalRequirement;
    }

    public String getCourseObjective() {
        return courseObjective;
    }

    public void setCourseObjective(String courseObjective) {
        this.courseObjective = courseObjective;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean template) {
        isTemplate = template;
    }

    public AssessmentSchemeDTO getAssessmentScheme() {
        return assessmentScheme;
    }

    public void setAssessmentScheme(AssessmentSchemeDTO assessmentScheme) {
        this.assessmentScheme = assessmentScheme;
    }

    public SyllabusLevelDTO getSyllabusLevel() {
        return syllabusLevel;
    }

    public void setSyllabusLevel(SyllabusLevelDTO syllabusLevel) {
        this.syllabusLevel = syllabusLevel;
    }

    public List<SyllabusDayDTOWithoutId> getSyllabusDays() {
        return syllabusDays;
    }

    public void setSyllabusDays(List<SyllabusDayDTOWithoutId> syllabusDays) {
        this.syllabusDays = syllabusDays;
    }

    public DeliveryPrincipleDTO getDeliveryPrinciple() {
        return deliveryPrinciple;
    }

    public void setDeliveryPrinciple(DeliveryPrincipleDTO deliveryPrinciple) {
        this.deliveryPrinciple = deliveryPrinciple;
    }
}
