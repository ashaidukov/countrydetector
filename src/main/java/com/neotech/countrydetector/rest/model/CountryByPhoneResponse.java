package com.neotech.countrydetector.rest.model;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@RequiredArgsConstructor
@Builder
@Jacksonized
public class CountryByPhoneResponse {

    String phoneNumber;

    List<String> countryNames;
}
