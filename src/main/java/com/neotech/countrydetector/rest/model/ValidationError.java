package com.neotech.countrydetector.rest.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ValidationError implements AdditionalError {

    String object;

    String field;

    Object rejectedValue;

    String message;
}
