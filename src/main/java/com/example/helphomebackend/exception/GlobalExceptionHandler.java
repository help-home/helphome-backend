package com.example.helphomebackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 비즈니스 로직 예외 (IllegalArgumentException 포함)
    @ExceptionHandler({BusinessException.class, IllegalAccessError.class})
    public ResponseEntity<ErrorResponse> handleBusinessException(RuntimeException e, HttpServletRequest request) {
        log.warn("Business exception occurred: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                "BAD_REQUEST",
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 중복 리소스 예외
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException e, HttpServletRequest request) {

        log.warn("Duplicate resource exception: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                "CONFLICT",
                HttpStatus.CONFLICT.value(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // 리소스 없음 예외
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException e, HttpServletRequest request) {

        log.warn("Resource not found: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                "NOT_FOUND",
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.notFound().build();
    }

    // Bean Validation 예외 (@Valid 검증 실패)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        log.warn("Validation exception occurred");

        List<ErrorResponse.ValidationError> validationErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ErrorResponse.ValidationError.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .rejectedValue(fieldError.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status("VALIDATION_FAILED")
                .code(HttpStatus.BAD_REQUEST.value())
                .message("입력값 검증에 실패했습니다")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 일반적인 예외 (예상하지 못한 오류)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception e, HttpServletRequest request) {

        log.error("Unexpected error occurred", e);

        ErrorResponse errorResponse = ErrorResponse.of(
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 내부 오류가 발생했습니다",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
