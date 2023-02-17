package com.neotech.countrydetector.service;

import com.neotech.countrydetector.persistence.model.Country;
import com.neotech.countrydetector.persistence.model.PhonePrefixCode;
import com.neotech.countrydetector.persistence.repository.CountryRepository;
import com.neotech.countrydetector.persistence.repository.PhonePrefixCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataService {

    private final CountryRepository countryRepository;

    private final PhonePrefixCodeRepository phonePrefixCodeRepository;

    public List<String> findCountries(String prefixCode) {
        var codes = phonePrefixCodeRepository.findPhonePrefixCodeByPrefixCode(prefixCode);
        return codes.stream().flatMap(code -> code.getCountries().stream())
                .map(Country::getName)
                .collect(Collectors.toList());
    }

    public void clearPhoneCodes() {
        phonePrefixCodeRepository.deleteAll();
        countryRepository.deleteAll();
    }

    public void savePhoneCodes(List<com.neotech.countrydetector.service.model.PhonePrefixCode> phonePrefixCodes) {
        for (com.neotech.countrydetector.service.model.PhonePrefixCode phonePrefixCode : phonePrefixCodes) {
            var persistentPhonePrefixCode = new PhonePrefixCode();
            persistentPhonePrefixCode.setPrefixCode(phonePrefixCode.getPrefixCode());
            for (com.neotech.countrydetector.service.model.Country country: phonePrefixCode.getCountries()) {
                var persistentCountry  = countryRepository.findCountryByCode(country.getCode());
                if (persistentCountry == null) {
                    persistentCountry = new Country();
                    persistentCountry.setCode(country.getCode());
                    persistentCountry.setName(country.getName());
                    persistentCountry = countryRepository.save(persistentCountry);
                }
                persistentPhonePrefixCode.getCountries().add(persistentCountry);
            }
            phonePrefixCodeRepository.save(persistentPhonePrefixCode);
        }
    }
}
