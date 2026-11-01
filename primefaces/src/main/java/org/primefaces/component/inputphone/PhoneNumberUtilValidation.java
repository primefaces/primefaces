/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.inputphone;

import java.util.List;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * Validation of phone numbers with Google's libphonenumber, only loaded if it is available
 * ({@link org.primefaces.config.PrimeEnvironment#isPhoneNumberUtilAvailable()}).
 */
final class PhoneNumberUtilValidation {

    private PhoneNumberUtilValidation() {
        // NOOP
    }

    /**
     * @param number a number in the E.164 format
     * @param onlyCountries the countries the number may belong to, all if empty
     * @param excludeCountries the countries the number must not belong to
     * @return whether the number is valid for its country and the country is allowed
     */
    static boolean isValid(String number, List<String> onlyCountries, List<String> excludeCountries) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber phoneNumber = phoneNumberUtil.parse(number, null);
            if (!phoneNumberUtil.isValidNumber(phoneNumber)) {
                return false;
            }
            // "001" for a number without country (e.g. +800), it is no ISO code of the lists
            String country = phoneNumberUtil.getRegionCodeForNumber(phoneNumber);
            return (onlyCountries.isEmpty() || onlyCountries.contains(country)) && !excludeCountries.contains(country);
        }
        catch (NumberParseException e) {
            return false;
        }
    }
}
