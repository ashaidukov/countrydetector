package com.neotech.countrydetector.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryDetectorServiceTest {

    @Mock
    private DataService dataService;

    @InjectMocks
    private CountryDetectorService countryDetectorService;

    @Test
    void testGetCountriesInNorthAmericaOutsideUSAndCanada() {
        when(dataService.findCountries("+1939"))
                .thenReturn(List.of("Puerto Rico"));

        var foundCountries = countryDetectorService.getCountries("+1 939 888 7777");
        assertEquals(List.of("Puerto Rico"), foundCountries);

        verify(dataService, times(1)).findCountries("+1939");
    }

    @Test
    void testGetCountriesInNorthAmericaInUSOrCanada() {
        when(dataService.findCountries("+1800")).thenReturn(List.of());

        var foundCountries = countryDetectorService.getCountries("+1 800 633 0738");
        assertEquals(List.of("United States of America", "Canada"), foundCountries);

        verify(dataService, times(1)).findCountries("+1800");
    }

    @Test
    void testGetCountriesFoundByThreeDigitPrefix() {
        when(dataService.findCountries("+356")).thenReturn(List.of("Malta"));

        var foundCountries = countryDetectorService.getCountries("+356 0633 0738");
        assertEquals(List.of("Malta"), foundCountries);

        verify(dataService, times(1)).findCountries("+356");
        verifyNoMoreInteractions(dataService);
    }

    @Test
    void testGetCountriesFoundByTwoDigitPrefix() {
        when(dataService.findCountries("+791")).thenReturn(List.of());
        when(dataService.findCountries("+79")).thenReturn(List.of("Russia"));

        var foundCountries = countryDetectorService.getCountries("+7 913 700 0000");
        assertEquals(List.of("Russia"), foundCountries);

        verify(dataService, times(1)).findCountries("+791");
        verify(dataService, times(1)).findCountries("+79");
    }

    @Test
    void testCountryNotFound() {
        when(dataService.findCountries("+999")).thenReturn(List.of());
        when(dataService.findCountries("+99")).thenReturn(List.of());

        assertTrue(countryDetectorService.getCountries("+999 0000 0000").isEmpty());

        verify(dataService, times(1)).findCountries("+999");
        verify(dataService, times(1)).findCountries("+99");
    }
}