package com.neotech.countrydetector.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class ErrorResponse {

    HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();

    String message;

    String debugMessage;

    List<AdditionalError> additionalErrors;
}

