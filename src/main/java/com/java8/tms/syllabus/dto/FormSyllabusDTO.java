package com.java8.tms.syllabus.dto;

import com.java8.tms.common.entity.AssessmentScheme;
import com.java8.tms.common.entity.DeliveryPrinciple;
import com.java8.tms.common.entity.SyllabusDay;
import com.java8.tms.common.entity.SyllabusLevel;
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
public class FormSyllabusDTO {
    private UUID id;
    boolean isTemplate;
    List<SyllabusDayDTO> syllabusDays;
    private String name;
    private String code;
    private String version;
    private int attendeeNumber;
    private String technicalRequirement;
    private String courseObjective;
    private int days;
    private int hours;
    private SyllabusStatus status;
    private UUID createdBy;
    private Date createdDate;
    private UUID updatedBy;
    private Date updatedDate;
    private AssessmentSchemeDTO assessmentScheme;
    private SyllabusLevelDTO syllabusLevel;
    private DeliveryPrincipleDTO deliveryPrinciple;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public SyllabusStatus getStatus() {
        return status;
    }

    public void setStatus(SyllabusStatus status) {
        this.status = status;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean template) {
        isTemplate = template;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
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

    public List<SyllabusDayDTO> getSyllabusDays() {
        return syllabusDays;
    }

    public void setSyllabusDays(List<SyllabusDayDTO> syllabusDays) {
        this.syllabusDays = syllabusDays;
    }

    public DeliveryPrincipleDTO getDeliveryPrinciple() {
        return deliveryPrinciple;
    }

    public void setDeliveryPrinciple(DeliveryPrincipleDTO deliveryPrinciple) {
        this.deliveryPrinciple = deliveryPrinciple;
    }
}
