package com.kisanconnect.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

/**
 * Thrown when a requested resource cannot be found.
 * Produces a 404 response with RFC 7807 ProblemDetail.
 */
public class ResourceNotFoundException extends ErrorResponseException {

    public ResourceNotFoundException(String resourceType, Object id) {
        super(HttpStatus.NOT_FOUND);
        this.setTitle(resourceType + " Not Found");
        this.setDetail(resourceType + " with id '" + id + "' was not found.");
    }

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND);
        this.setTitle("Resource Not Found");
        this.setDetail(message);
    }
}
