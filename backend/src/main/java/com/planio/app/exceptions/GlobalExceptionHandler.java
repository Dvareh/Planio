package com.planio.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            ObjectNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new ErrorResponse(
                                LocalDateTime.now(),
                                404,
                                "Not Found",
                                e.getMessage()
                        )
                );
    }

    @ExceptionHandler(DuplicateObjectException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException(
            DuplicateObjectException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ErrorResponse(
                                LocalDateTime.now(),
                                409,
                                "Conflict",
                                e.getMessage()
                        )
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        new ErrorResponse(
                                LocalDateTime.now(),
                                403,
                                "Forbidden",
                                e.getMessage()
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        return ResponseEntity.badRequest().body(errors);
    }
}