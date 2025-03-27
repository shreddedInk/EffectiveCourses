package com.coffeeshop.exception;

import com.coffeeshop.dto.ErrorsResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import org.hibernate.ObjectNotFoundException;

import java.util.stream.Collectors;

@ControllerAdvice(basePackages = "com.coffeeshop.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorsResponseDTO> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, "Resource Not Found", request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorsResponseDTO> handleValidation(
            ValidationException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Validation Error", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorsResponseDTO> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        String errorsMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return new ResponseEntity<>(
                new ErrorsResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Error",
                        errorsMessage,
                        request.getDescription(false).replace("uri=", "")),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorsResponseDTO> handleEntityNotFound(
            ObjectNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, "Entity Not Found", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorsResponseDTO> handleGlobalException(
            Exception ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");

        if (path.contains("/swagger") || path.contains("/v3/api-docs") || path.contains("/api/v1/docs")) {
            return ResponseEntity.notFound().build();
        }

        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", request);
    }

    private ResponseEntity<ErrorsResponseDTO> buildErrorResponse(
            Exception ex, HttpStatus status, String error, WebRequest request) {
        ErrorsResponseDTO response = new ErrorsResponseDTO(
                status.value(),
                error,
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(response, status);
    }
}
