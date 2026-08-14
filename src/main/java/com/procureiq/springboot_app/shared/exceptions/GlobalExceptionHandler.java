package com.procureiq.springboot_app.shared.exceptions;

import com.procureiq.springboot_app.shared.types.single.ApiSingleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> errorPayload = new HashMap<>();
        errorPayload.put("message", "Validation failed");
        errorPayload.put("details", errors);

        return new ResponseEntity<>(ApiSingleResponse.success(400, errorPayload), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ApiSingleResponse.success(409, ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(ApiSingleResponse.success(400, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "";
        boolean isEmail = msg.contains("uq_users_email") || msg.contains("users_email_key") || msg.contains("email");
        HttpStatus status = isEmail ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        String responseMsg = isEmail
            ? "An account with this email address already exists. Please sign in instead."
            : "Database constraint violation occurred";
        return new ResponseEntity<>(ApiSingleResponse.success(status.value(), responseMsg), status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ApiSingleResponse.success(404, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "An unexpected server error occurred";
        return new ResponseEntity<>(ApiSingleResponse.success(500, msg), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
