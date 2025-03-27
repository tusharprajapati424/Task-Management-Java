package com.assessment.TaskManagementGradle.utility;

import com.assessment.TaskManagementGradle.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Collect validation errors
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiResponse<Map<String, String>> response = new ApiResponse<>(errors, HttpStatus.BAD_REQUEST.value(), "Validation failed");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<ApiResponse<String>> handleJwtValidationException(JwtValidationException ex) {
        return ResponseEntity.status(ex.getStatus())  // This line needs getStatus()
                .body(new ApiResponse<>(null, ex.getStatus().value(), ex.getMessage()));
    }
}
