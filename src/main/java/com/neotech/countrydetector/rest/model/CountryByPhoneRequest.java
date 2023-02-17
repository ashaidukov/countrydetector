package com.neotech.countrydetector.rest.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@Jacksonized
public class CountryByPhoneRequest {

    @NotEmpty(message = "phone number is empty")
//	@Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$", message = "invalid phone number")
    @Pattern(regexp = "^\\+(?:[0-9]\\x20?){6,14}[0-9]$", message = "Invalid phone number")
//	@Pattern(regexp = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$", message = "invalid phone number")
    private String phoneNumber;
}
