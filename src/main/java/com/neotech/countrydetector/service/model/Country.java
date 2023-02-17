package com.neotech.countrydetector.service.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Country {

     String code;
     String name;
}
