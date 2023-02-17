package com.neotech.countrydetector;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PhoneNumberUtils {

    public static String normalizePhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("[^\\+0-9]", "");
    }
}
