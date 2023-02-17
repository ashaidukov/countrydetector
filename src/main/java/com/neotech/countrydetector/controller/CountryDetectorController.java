package com.neotech.countrydetector.controller;

import com.neotech.countrydetector.rest.model.CountryByPhoneRequest;
import com.neotech.countrydetector.rest.model.CountryByPhoneResponse;
import com.neotech.countrydetector.service.CountryDetectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CountryDetectorController {

    private final CountryDetectorService countryDetectorService;

    @PostMapping(
            value = "countryByPhone",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CountryByPhoneResponse countryByPhone(@RequestBody @Valid CountryByPhoneRequest request) {

        var countries = countryDetectorService.getCountries(request.getPhoneNumber());
        return CountryByPhoneResponse.builder()
                .phoneNumber(request.getPhoneNumber())
                .countryNames(countries)
                .build();
    }
}
