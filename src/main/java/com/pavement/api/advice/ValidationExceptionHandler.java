package com.pavement.api.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toError)
                .collect(Collectors.toList());

        Map<String, Object> body = base(request, 400, "Bad Request");
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBadJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        Map<String, Object> body = base(request, 400, "Bad Request");
        body.put("errors", List.of(Map.of(
                "field", "_",
                "message", "Malformed JSON or wrong types"
        )));
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        Map<String, Object> body = base(request, 400, "Bad Request");
        body.put("errors", ex.getConstraintViolations().stream()
                .map(v -> Map.of("field", v.getPropertyPath().toString(), "message", v.getMessage()))
                .collect(Collectors.toList()));
        return ResponseEntity.badRequest().body(body);
    }

    private Map<String, Object> base(HttpServletRequest request, int status, String error) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        body.put("error", error);
        body.put("path", request.getRequestURI());
        return body;
    }

    private Map<String, String> toError(FieldError fe) {
        return Map.of(
                "field", fe.getField(),
                "message", fe.getDefaultMessage()
        );
    }
}
