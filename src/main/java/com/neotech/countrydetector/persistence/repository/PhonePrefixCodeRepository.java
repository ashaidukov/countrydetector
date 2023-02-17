package com.neotech.countrydetector.persistence.repository;

import com.neotech.countrydetector.persistence.model.PhonePrefixCode;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PhonePrefixCodeRepository extends CrudRepository<PhonePrefixCode, Integer> {

    List<PhonePrefixCode> findPhonePrefixCodeByPrefixCode(String prefixCode);
}
