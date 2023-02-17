package com.neotech.countrydetector.controller;

import com.neotech.countrydetector.rest.model.AdditionalError;
import com.neotech.countrydetector.rest.model.ErrorResponse;
import com.neotech.countrydetector.rest.model.ValidationError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CountryDetectorControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<AdditionalError> additionalErrors = ex.getBindingResult().getAllErrors().stream()
                .map(this::buildValidationError)
                .collect(Collectors.toList());
        var errorResponse = ErrorResponse.builder()
                .status(status)
                .message("Validation error")
                .debugMessage(ex.getMessage())
                .additionalErrors(additionalErrors)
                .build();
        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }

    private ValidationError buildValidationError(ObjectError error) {
        FieldError fieldError = (FieldError) error;
        return ValidationError.builder()
                .object(fieldError.getObjectName())
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue())
                .build();
    }
}
