package com.neotech.countrydetector.service;

import com.neotech.countrydetector.persistence.repository.CountryRepository;
import com.neotech.countrydetector.persistence.repository.PhonePrefixCodeRepository;
import com.neotech.countrydetector.service.model.Country;
import com.neotech.countrydetector.service.model.PhonePrefixCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private PhonePrefixCodeRepository phonePrefixCodeRepository;

    @InjectMocks
    private DataService dataService;

    @Test
    void testFindCountries() {
        var uk = new com.neotech.countrydetector.persistence.model.Country();
        uk.setName("United Kingdom");
        var jersey = new com.neotech.countrydetector.persistence.model.Country();
        jersey.setName("Jersey");
        var ukPhonePrefixCode = new com.neotech.countrydetector.persistence.model.PhonePrefixCode();
        ukPhonePrefixCode.setPrefixCode("+44");
        ukPhonePrefixCode.getCountries().add(uk);
        ukPhonePrefixCode.getCountries().add(jersey);

        when(phonePrefixCodeRepository.findPhonePrefixCodeByPrefixCode("+44"))
                .thenReturn(List.of(ukPhonePrefixCode));

        assertThat(dataService.findCountries("+44"))
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(Set.of("United Kingdom", "Jersey"));

        verify(phonePrefixCodeRepository, times(1))
                .findPhonePrefixCodeByPrefixCode("+44");
    }

    @Test
    void testFindCountriesWhenNoCountriesFound() {
        when(phonePrefixCodeRepository.findPhonePrefixCodeByPrefixCode("+999"))
                .thenReturn(List.of());

        assertThat(dataService.findCountries("+999"))
                .isNotNull()
                .isEmpty();

        verify(phonePrefixCodeRepository, times(1))
                .findPhonePrefixCodeByPrefixCode("+999");
    }

    @Test
    void testClearPhoneCodes() {
        dataService.clearPhoneCodes();

        verify(countryRepository, times(1)).deleteAll();
        verify(phonePrefixCodeRepository, times(1)).deleteAll();
    }

    @Test
    void testSavePhoneCodesSavesPhoneCodeAndNewCountry() {
        lenient().when(countryRepository.findCountryByCode("BY"))
                .thenReturn(null);

        var persistentCountry = new com.neotech.countrydetector.persistence.model.Country();
        persistentCountry.setCode("BY");
        persistentCountry.setName("Belarus");
        lenient().when(countryRepository.save(any(com.neotech.countrydetector.persistence.model.Country.class)))
                .thenReturn(persistentCountry);

        var phonePrefixCode = PhonePrefixCode.builder()
                .prefixCode("+375")
                .countries(Set.of(Country.builder()
                        .code("BY")
                        .name("Belarus")
                        .build()))
                .build();

        dataService.savePhoneCodes(List.of(phonePrefixCode));

        var stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(countryRepository, times(1)).findCountryByCode(stringCaptor.capture());
        assertThat(stringCaptor.getValue())
                .isEqualTo("BY");

        var countryCaptor = ArgumentCaptor.forClass(com.neotech.countrydetector.persistence.model.Country.class);
        verify(countryRepository, times(1)).save(countryCaptor.capture());
        assertThat(countryCaptor.getValue())
                .hasFieldOrPropertyWithValue("name", "Belarus")
                .hasFieldOrPropertyWithValue("code", "BY");

        var prefixCaptor = ArgumentCaptor.forClass(com.neotech.countrydetector.persistence.model.PhonePrefixCode.class);
        verify(phonePrefixCodeRepository, times(1)).save(prefixCaptor.capture());
        assertThat(prefixCaptor.getValue()).hasFieldOrPropertyWithValue("prefixCode", "+375")
                .satisfies(code -> assertThat(code.getCountries())
                        .hasSize(1)
                        .allMatch(country -> Objects.equals(country, persistentCountry)));

    }

    @Test
    void testSavePhoneCodesSavesCodeAndAddsExistingCountry() {

        var persistentCountry = new com.neotech.countrydetector.persistence.model.Country();
        persistentCountry.setCode("BY");
        persistentCountry.setName("Belarus");
        lenient().when(countryRepository.findCountryByCode(eq("BY")))
                .thenReturn(persistentCountry);

        lenient().when(countryRepository.save(eq(persistentCountry))).thenReturn(persistentCountry);

        var phonePrefixCode = PhonePrefixCode.builder()
                .prefixCode("+375")
                .countries(Set.of(Country.builder()
                        .code("BY")
                        .name("Belarus")
                        .build()))
                .build();

        dataService.savePhoneCodes(List.of(phonePrefixCode));

        var strCaptor = ArgumentCaptor.forClass(String.class);
        verify(countryRepository, times(1)).findCountryByCode(strCaptor.capture());
        assertThat(strCaptor.getValue()).isEqualTo("BY");

        var countryCaptor = ArgumentCaptor.forClass(com.neotech.countrydetector.persistence.model.Country.class);
        verify(countryRepository, never()).save(countryCaptor.capture());

        var prefixCaptor = ArgumentCaptor.forClass(com.neotech.countrydetector.persistence.model.PhonePrefixCode.class);
        verify(phonePrefixCodeRepository, times(1)).save(prefixCaptor.capture());
        assertThat(prefixCaptor.getValue()).hasFieldOrPropertyWithValue("prefixCode", "+375")
                .satisfies(code -> assertThat(code.getCountries())
                        .hasSize(1)
                        .allMatch(country -> Objects.equals(country, persistentCountry)));
    }
}