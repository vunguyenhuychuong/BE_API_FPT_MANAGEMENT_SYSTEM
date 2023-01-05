package com.java8.tms.user.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.Authority;
import com.java8.tms.common.entity.Role;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.security.userprincipal.UserPrinciple;
import com.java8.tms.role.customException.RoleIdNotFoundException;
import com.java8.tms.role.service.impl.RoleServiceImpl;
import com.java8.tms.user.dto.EmailDetailsDTO;
import com.java8.tms.user.dto.SignupUserDTO;
import com.java8.tms.user.service.SignupService;

@Service
public class SignupServiceImpl implements SignupService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SignupServiceImpl.class);
	@Autowired
	private UserServiceImpl userServiceImpl;
	@Autowired
	private EmailServiceImpl emailServiceImpl;
	@Autowired
	private RoleServiceImpl roleServiceImpl;
	
	@Override
	public ResponseEntity<ResponseObject> createNewAccount(SignupUserDTO signupUserDTO) {
		LOGGER.info(
				"Start method existsByEmail, setUpUser, setUpEmailDetailsForSignup, sendMailNoAttachment  on  user-management/user");
		UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Role role = roleServiceImpl.findRoleById(signupUserDTO.getRoleId())
                .orElseThrow(() -> new RoleIdNotFoundException(signupUserDTO.getRoleId(), "Not Found!"));

		if(!checkAccessSignUpRolePermission(role, userPrinciple)){
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
					.body(new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(), "Can't create account with this role", null, null));
        }
		ArrayList<String> arr = new ArrayList<>();
		// Check email in Data base
		boolean checkExistedEmail = userServiceImpl.existsByEmail(signupUserDTO.getEmail());
		if (checkExistedEmail) {
			LOGGER.error("Email existed: {}", signupUserDTO.getEmail());
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
					.body(new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(), "Email existed", null, null));
		}
		
		//Check birthday if null
		if (signupUserDTO.getBirthday() == null) {
			arr.add("birthday: Required field!");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Invalid request", null, arr));
		} 
		// Validation birthday and enough 15 years old
		if (!checkSignUpDateValidtion(signupUserDTO.getBirthday())) {
			arr.add("birthday: Need to be at least 15 years old");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Invalid request", null, arr));
		}
		if (signupUserDTO.getRoleId() == null) {
			arr.add("roleId: Required field!");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Invalid request", null, arr));
		}

		User user = userServiceImpl.setUpUser(signupUserDTO, role);

		// Setup email
		EmailDetailsDTO emailDetails = emailServiceImpl.setUpEmailDetailsForSignup(user.getEmail(), user.getPassword(),
				user.getFullname());

		// Send mail
		boolean checkSendEmail = emailServiceImpl.sendMailNoAttachment(emailDetails);
		if (!checkSendEmail) {
			LOGGER.error("Failed to send mail while creating a new account for: {}", user.getEmail());
			return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
					.body(new ResponseObject(HttpStatus.FAILED_DEPENDENCY.toString(),
							"Failed to send mail while creating a new account", null, user.getEmail()));
		}

		// Save new account
		userServiceImpl.createNewAccount(user);
		LOGGER.info("Create account successfully for: {}", user.getEmail());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ResponseObject(HttpStatus.CREATED.toString(), "Create account successfully", null, null));
	}
	
	private boolean checkSignUpDateValidtion(Date date) {
		boolean valid = true;
		Instant birthday = date.toInstant();
		Instant dateWithSub15Years = Instant.now().minusSeconds(473040000);
		if (birthday.isAfter(dateWithSub15Years)) {
			valid = false;
		}
		return valid;
	}
	
	private boolean checkAccessSignUpRolePermission(Role role, UserPrinciple userPrinciple) {
		boolean check = true;
		List<Authority> listRoleAuthority = new ArrayList<>(role.getAuthorities());
		List<? extends GrantedAuthority> listUserAuthority = userPrinciple.getAuthorities().stream().collect(Collectors.toList());
		
		for (Authority roleAuthority : listRoleAuthority) {
			if(roleAuthority.appendAuthority().equals("FULL_ACCESS_USER")) {
				for (GrantedAuthority grantedAuthority : listUserAuthority) {
					check = false;
					if(grantedAuthority.toString().equals("FULL_ACCESS_USER")) {
						check = true;
						break;
					} 
				}
				break;
			}
		}
		return check;
	}
}
