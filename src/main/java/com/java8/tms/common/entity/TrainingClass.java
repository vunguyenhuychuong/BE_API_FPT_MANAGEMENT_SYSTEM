package com.java8.tms.common.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.java8.tms.common.utils.DateTimeUtils;
import lombok.*;
import org.hibernate.annotations.Type;
import java.io.Serializable;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class TrainingClass implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(type = "uuid-char")
	private UUID id;

	private String name;

	// courseCode bên DTO
	@Column(unique = true)
	private String courseCode;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.TIME_FORMAT)
	@DateTimeFormat(pattern = DateTimeUtils.TIME_FORMAT)
	private LocalTime startTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.TIME_FORMAT)
	@DateTimeFormat(pattern = DateTimeUtils.TIME_FORMAT)
	private LocalTime endTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_FORMAT)
	@DateTimeFormat(pattern = DateTimeUtils.DATE_FORMAT)
	private LocalDate startDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_FORMAT)
	@DateTimeFormat(pattern = DateTimeUtils.DATE_FORMAT)
	private LocalDate endDate;

	private int duration; // month

	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "created_by_id")
	private User createdBy; // FK

	// bên FILE excel ko có
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
	@DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
	@CreatedDate
	private LocalDateTime createdDate;

	// updatedBy bên DTO
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "updated_by_id")
	private User updatedBy; // FK

	// updatedDate bên DTO
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
	@DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
	@LastModifiedDate
	private LocalDateTime updatedDate;

	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "reviewed_by_id")
	private User reviewedBy; // FK , null

	// Được phép null
	private LocalDateTime reviewedDate;

	// Được phép null khi đọc file lên
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "approved_by_id")
	private User approvedBy; // FK
	// Được phép null
	private LocalDateTime approvedDate;

	// universityCode bên DTO
	private String universityCode;

	// 3 trường này đc null
	private int plannedAttendee;
	private int acceptedAttendee;
	private int actualAttendee;

	// private Long recerId; // FK recerId nối qua bảng recer để lấy thông tin

	// locationId bên DTO
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "class_location_id", nullable = true)
	private ClassLocation classLocation;

	// attendeeType bên DTO
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "attendee_level_id", nullable = true)
	private AttendeeLevel attendeeLevel;

	// formatType bên DTO
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "format_type_id", nullable = true)
	private FormatType formatType;

	// status bên DTO
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "class_status_id", nullable = true)
	private ClassStatus classStatus;

	// technicalGroup bên DTO
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "technical_group_id", nullable = true)
	private TechnicalGroup technicalGroup;

	// programContentId bên DTO
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "program_content_id", nullable = true)
	private ProgramContent programContent;

	// fsu bên DTO
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "fsu_id", nullable = true)
	private FSU fsu;

	// trainingProgram bên DTO
	@OneToOne
	@JsonIgnore
	@JoinColumn(name = "training_program_id")
	private TrainingProgram trainingProgram;

	// ClassTrainer - relation n-n TrainingClass với Account tạo ra table
	// class_trainers
	// trainer bên DTO
	@ManyToMany(fetch = FetchType.EAGER)
	@JsonIgnore
	@JoinTable(name = "class_trainers", joinColumns = @JoinColumn(name = "class_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "trainer_id", referencedColumnName = "id"))
	private Set<User> account_trainers;

	// ClassAdmin - relation n-n TrainingClass với Account tạo ra table class_admins
	// ClassAdmin bên DTO
	@ManyToMany(fetch = FetchType.EAGER)
	@JsonIgnore
	@JoinTable(name = "class_admins", joinColumns = @JoinColumn(name = "class_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "admin_id", referencedColumnName = "id"))
	private Set<User> account_admins;

	// Được phép null
	@ManyToMany(fetch = FetchType.EAGER)
	@JsonIgnore
	@JoinTable(name = "class_trainee", joinColumns = @JoinColumn(name = "class_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "trainee_id", referencedColumnName = "id"))
	private List<User> account_trainee; // relationship association

	@OneToMany(mappedBy = "trainingClass")
	@JsonIgnore
	private Set<ClassCalendar> classCalendars;

}
