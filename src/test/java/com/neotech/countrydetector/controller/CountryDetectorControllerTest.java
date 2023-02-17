package com.neotech.countrydetector.controller;

import com.neotech.countrydetector.service.CountryDetectorService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CountryDetectorController.class)
class CountryDetectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryDetectorService countryDetectorService;

    @Test
    @SneakyThrows
    void testCountryByPhoneSuccessful() {

        when(countryDetectorService.getCountries("+39 529 111 2222"))
                .thenReturn(List.of("Italy", "Vatican City"));

        mockMvc.perform(post("/countryByPhone")
                        .content("{\"phoneNumber\": \"+39 529 111 2222\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("+39 529 111 2222"))
                .andExpect(jsonPath("$.countryNames").isArray())
                .andExpect(jsonPath("$.countryNames", hasSize(2)))
                .andExpect(jsonPath("$.countryNames", hasItem("Italy")))
                .andExpect(jsonPath("$.countryNames", hasItem("Vatican City")));

        verify(countryDetectorService, times(1))
                .getCountries(eq("+39 529 111 2222"));
    }

    @Test
    @SneakyThrows
    void testCountryByPhoneBadRequest() {

        mockMvc.perform(post("/countryByPhone")
                        .content("{\"phoneNumber\": \"00000000\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(countryDetectorService, never()).getCountries(anyString());
    }
}