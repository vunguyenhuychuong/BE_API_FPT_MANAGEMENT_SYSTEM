/**
 * <p>
 * XXX
 * </p>
 *
 * @author kiet phan
 */
package com.java8.tms.syllabus.dto;

import com.java8.tms.common.meta.SyllabusStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * DTO
 * </p>
 *
 * @author kiet phan
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyllabusDTO {
    private UUID id;
    private String name;
    private String code;
    private String version;
    private int attendeeNumber;
    private String technicalRequirement;
    private String courseObjective;
    private List<OutputStandardDTO> outputStandardCovered;
    private int days;
    private int hours;
    private SyllabusStatus status;
    boolean isTemplate;
    private UUID createdBy;
    private UserDTO createdByUser;
    private Date createdDate;
    private UUID updatedBy;
    private UserDTO updatedByUser;
    private Date updatedDate;
    private AssessmentSchemeDTO assessmentScheme;
    private SyllabusLevelDTO syllabusLevel;
    private List<SyllabusDayDTO> syllabusDays;
    private DeliveryPrincipleDTO deliveryPrinciple;
}
