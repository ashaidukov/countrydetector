package com.neotech.countrydetector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryDetectorService {

    private final DataService dataService;

    public List<String> getCountries(String phoneNumber) {
        var phone = phoneNumber.replaceAll("[^\\+0-9]", "");
        if (isNorthAmericanPhoneNumber(phone)) {
            return getNorthAmericanCountries(phone);
        } else {
            return getUsualCountries(phone);
        }
    }

    private boolean isNorthAmericanPhoneNumber(String phone) {
        return phone.startsWith("+1");
    }

    private List<String> getNorthAmericanCountries(String phone) {
        var fourDigitPrefix = phone.substring(0, 5);
        var countryNames = dataService.findCountries(fourDigitPrefix);
        if (countryNames.isEmpty()) {
            countryNames = List.of("United States of America", "Canada");
        }
        return countryNames;
    }

    private List<String> getUsualCountries(String phone) {
        var threeDigitPrefix = phone.substring(0, 4);
        var countryNames = dataService.findCountries(threeDigitPrefix);
        if (!countryNames.isEmpty()) {
            return countryNames;
        }

        var twoDigitPrefix = phone.substring(0, 3);
        return dataService.findCountries(twoDigitPrefix);
    }
}
