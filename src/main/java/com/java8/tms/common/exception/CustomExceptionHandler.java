package com.java8.tms.common.exception;

import com.java8.tms.authority.customException.AuthorityNotFoundException;
import com.java8.tms.common.dto.ErrorResponse;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.exception.model.ResourceNotFoundException;
import com.java8.tms.common.security.jwt.JwtTokenException;
import com.java8.tms.common.security.jwt.RefreshTokenException;
import com.java8.tms.fsu.service.impl.FsuNotFoundException;
import com.java8.tms.program_syllabus.exception.InvalidRequestForSaveProgramException;
import com.java8.tms.program_syllabus.exception.UserNotFoundException;
import com.java8.tms.role.customException.RoleIdNotFoundException;
import com.java8.tms.role.customException.RoleNameNotFoundException;
import com.java8.tms.role.service.RoleNotFoundException;
import com.java8.tms.training_program.exception.InvalidRequestForFilterTrainingProgramException;
import com.java8.tms.user.custom_exception.OtpNotFoundException;
import com.sun.jdi.InternalException;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestControllerAdvice
@Log4j2
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse accessDenied(AccessDeniedException e){
        return new ErrorResponse(new Date(), HttpStatus.FORBIDDEN.toString(), "Access is denied");

    }

    //new code
    @ExceptionHandler({MaxUploadSizeExceededException.class, FileSizeLimitExceededException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerFileUploadException(Exception e, WebRequest request) {
        e.printStackTrace();
        return new ErrorResponse(new Date(), HttpStatus.BAD_REQUEST.toString(), "The csv file must be less than 10MB");
    }

    @ExceptionHandler(value = RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRoleNotFoundException(RoleNotFoundException ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    }

    @ExceptionHandler({FileNotFoundException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerFileNotFoundException(Exception e, WebRequest request) {
        logger.error(e.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage());
    }

    @ExceptionHandler(value = FsuNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFsuNotFoundException(FsuNotFoundException ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    }

    @ExceptionHandler(value = com.java8.tms.user.custom_exception.RoleAuthorizationAccessDeniedException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handleRoleAuthorizationAccessDeniedException(com.java8.tms.user.custom_exception.RoleAuthorizationAccessDeniedException ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.NOT_ACCEPTABLE.toString(), ex.getMessage());
    }


    // Xử lý tất cả các exception chưa được khai báo
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerException(Exception e, WebRequest request) {
        e.printStackTrace();
        return new ErrorResponse(new Date(), HttpStatus.BAD_REQUEST.toString(), e.getMessage());
    }
// end

    /**
     * <p>
     * Handle exception occurred when there are invalid input field in user's
     * request for filter training program
     * </p>
     *
     * @param exception {@code Exception}
     * @param request   {@code WebRequest}
     * @return an error response for this exception
     * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
     */
    @ExceptionHandler(InvalidRequestForFilterTrainingProgramException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handlerInvalidRequestException(Exception exception, WebRequest request) {
        exception.printStackTrace();
        return new ErrorResponse(new Date(), HttpStatus.NOT_ACCEPTABLE.toString(), exception.getMessage());
    }

    /**
     * <p>
     * Handle all exceptions that are undeclared
     * </p>
     *
     * @param exception {@code Exception}
     * @param request   {@code WebRequest}
     * @return an error response for this exception
     * @author Pham Xuan Kien
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerException(Throwable exception, WebRequest request) {
        log.error(exception.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.BAD_REQUEST.toString(), exception.getMessage());
    }

    /**
     * <p>
     * Handle invalid request for Create New Program
     * </p>
     *
     * @param e
     * @param request
     * @return ErrorResponse
     * @author Luu Thanh Huy
     */
    @ExceptionHandler(InvalidRequestForSaveProgramException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerInvalidRequest(Exception e, WebRequest request) {
        log.error(e.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.BAD_REQUEST.toString(), e.getMessage());
    }

    /**
     * <p>
     * UUID not found for the training program
     * </p>
     *
     * @param exception {@code Exception}
     * @param request   {@code WebRequest}
     * @return ErrorResponse
     * @author Tran Long, Vien Binh
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ErrorResponse resourceNotFoundException(ResourceNotFoundException exception, WebRequest request) {
        log.error(exception.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.NO_CONTENT.toString(), exception.getMessage());
    }

    @ExceptionHandler(value = RoleIdNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRoleNotFoundException(RoleIdNotFoundException ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    }

    @ExceptionHandler(value = RoleNameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRoleNameNotFoundException(RoleNameNotFoundException ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    }


    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    }

    @ExceptionHandler(value = AuthorityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAuthorityNotFoundException(AuthorityNotFoundException ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    }

    @ExceptionHandler(value = OtpNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleOtpNotFoundException(OtpNotFoundException ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    }

    @ExceptionHandler(value = InternalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalException(InternalException ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
    }

    @ExceptionHandler(value = RefreshTokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleTokenRefreshException(RefreshTokenException ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.FORBIDDEN.toString(), ex.getMessage());
    }

    @ExceptionHandler(value = JwtTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleJwtTokenException(JwtTokenException ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.UNAUTHORIZED.toString(), ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseObject> handleConstraintViolation(ConstraintViolationException ex,
                                                                    WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Invalid request", null, errors));
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final List<String> errors = new ArrayList<>();

        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        logger.error(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Invalid request", null, errors));
    }
}