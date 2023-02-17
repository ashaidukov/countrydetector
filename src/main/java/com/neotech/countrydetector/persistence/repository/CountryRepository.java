package com.neotech.countrydetector.persistence.repository;

import com.neotech.countrydetector.persistence.model.Country;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CountryRepository extends CrudRepository<Country, Integer> {

    Country findCountryByCode(String countryCode);
}
