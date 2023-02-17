package com.neotech.countrydetector.service.model;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class PhonePrefixCode {

    String prefixCode;
    Set<Country> countries;
}
