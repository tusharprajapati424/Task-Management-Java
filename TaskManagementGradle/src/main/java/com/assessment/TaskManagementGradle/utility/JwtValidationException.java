package com.assessment.TaskManagementGradle.utility;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter  // Lombok automatically generates getStatus()
public class JwtValidationException extends RuntimeException {
    private final HttpStatus status;

    public JwtValidationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
