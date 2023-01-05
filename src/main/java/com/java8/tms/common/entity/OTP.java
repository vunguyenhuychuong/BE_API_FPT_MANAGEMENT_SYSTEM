package com.java8.tms.common.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "otp")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class OTP {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	@Type(type = "org.hibernate.type.UUIDCharType")
	private UUID id;
	
	@OneToOne
	private User user;
	
	private String otpNumber;

	private Date otpExpiredTime;
	
	@Column(name = "valid_otp")
	private boolean validOTP;
	
	private boolean accessChangePassword;
}
