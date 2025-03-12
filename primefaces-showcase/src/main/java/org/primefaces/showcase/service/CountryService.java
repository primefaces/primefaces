/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.showcase.service;

import org.primefaces.showcase.domain.Country;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class CountryService {

    private List<Country> countries;
    private Map<Integer, Country> countriesAsMap;
    private List<Country> locales;
    private Map<Integer, Country> localesAsMap;

    public static Stream<Country> toCountryStream(String... isoCodes) {
        return Stream.of(isoCodes)
                .map(isoCode -> Locale.of("", isoCode))
                .map(CountryService::toCountry);
    }

    public static Country toCountry(Locale locale) {
        return CountryService.toCountry(locale, false);
    }

    public static Country toCountry(Locale locale, boolean rtl) {
        //use hash code from locale to have a reproducible ID (required for CountryConverter)
        return new Country(locale.hashCode(), locale, rtl);
    }

    @PostConstruct
    public void init() {
        countries = CountryService.toCountryStream(Locale.getISOCountries())
                .sorted(Comparator.comparing(Country::getName))
                .collect(Collectors.toList());

        locales = new ArrayList<>();

        locales.add(CountryService.toCountry(Locale.US));
        locales.add(CountryService.toCountry(Locale.UK));
        locales.add(CountryService.toCountry(Locale.of("en", "AU")));
        locales.add(CountryService.toCountry(Locale.FRANCE));
        locales.add(CountryService.toCountry(Locale.GERMANY));
        locales.add(CountryService.toCountry(Locale.of("de", "AT")));
        locales.add(CountryService.toCountry(Locale.of("de", "CH")));
        locales.add(CountryService.toCountry(Locale.ITALY));
        locales.add(CountryService.toCountry(Locale.KOREA));
        locales.add(CountryService.toCountry(Locale.of("es", "ES")));
        locales.add(CountryService.toCountry(Locale.of("ca", "ES")));
        locales.add(CountryService.toCountry(Locale.of("nl", "NL")));
        locales.add(CountryService.toCountry(Locale.of("pt", "BR")));
        locales.add(CountryService.toCountry(Locale.of("pt", "PT")));
        locales.add(CountryService.toCountry(Locale.of("ar", "SA"), true));
        locales.add(CountryService.toCountry(Locale.of("ar", "TN"), true));
        locales.add(CountryService.toCountry(Locale.of("bg", "BG")));
        locales.add(CountryService.toCountry(Locale.of("bn", "BD")));
        locales.add(CountryService.toCountry(Locale.of("bs", "BA")));
        locales.add(CountryService.toCountry(Locale.of("cs", "CZ")));
        locales.add(CountryService.toCountry(Locale.of("el", "GR")));
        locales.add(CountryService.toCountry(Locale.of("et", "EE")));
        locales.add(CountryService.toCountry(Locale.of("fa", "IR"), true));
        locales.add(CountryService.toCountry(Locale.of("fi", "FI")));
        locales.add(CountryService.toCountry(Locale.of("da", "DK")));
        locales.add(CountryService.toCountry(Locale.of("hi", "IN")));
        locales.add(CountryService.toCountry(Locale.of("in", "ID")));
        locales.add(CountryService.toCountry(Locale.of("is", "IS")));
        locales.add(CountryService.toCountry(Locale.of("hr", "HR")));
        locales.add(CountryService.toCountry(Locale.of("ja", "JP")));
        locales.add(CountryService.toCountry(Locale.of("hu", "HU")));
        locales.add(CountryService.toCountry(Locale.of("he", "IL"), true));
        locales.add(CountryService.toCountry(Locale.of("ka", "GE")));
        locales.add(CountryService.toCountry(Locale.of("ckb", "IQ"), true));
        locales.add(CountryService.toCountry(Locale.of("km", "KH")));
        locales.add(CountryService.toCountry(Locale.of("ky", "KG")));
        locales.add(CountryService.toCountry(Locale.of("kk", "KZ")));
        locales.add(CountryService.toCountry(Locale.of("lt", "LT")));
        locales.add(CountryService.toCountry(Locale.of("lv", "LV")));
        locales.add(CountryService.toCountry(Locale.of("ms", "MY")));
        locales.add(CountryService.toCountry(Locale.of("no", "NO")));
        locales.add(CountryService.toCountry(Locale.of("pl", "PL")));
        locales.add(CountryService.toCountry(Locale.of("ro", "RO")));
        locales.add(CountryService.toCountry(Locale.of("ru", "RU")));
        locales.add(CountryService.toCountry(Locale.of("sk", "SK")));
        locales.add(CountryService.toCountry(Locale.of("sl", "SI")));
        locales.add(CountryService.toCountry(Locale.of("sr", "BA")));
        locales.add(CountryService.toCountry(Locale.of("sr", "RS")));
        locales.add(CountryService.toCountry(Locale.of("sv", "SE")));
        locales.add(CountryService.toCountry(Locale.of("th", "TH")));
        locales.add(CountryService.toCountry(Locale.of("tr", "TR")));
        locales.add(CountryService.toCountry(Locale.of("uk", "UA")));
        locales.add(CountryService.toCountry(Locale.of("uz", "UZ")));
        locales.add(CountryService.toCountry(Locale.of("vi", "VN")));
        locales.add(CountryService.toCountry(Locale.SIMPLIFIED_CHINESE));
        locales.add(CountryService.toCountry(Locale.TRADITIONAL_CHINESE));
    }

    public List<Country> getCountries() {
        return new ArrayList<>(countries);
    }

    public Map<Integer, Country> getCountriesAsMap() {
        if (countriesAsMap == null) {
            countriesAsMap = getCountries().stream().collect(Collectors.toMap(Country::getId, country -> country));
        }
        return countriesAsMap;
    }

    public List<Country> getLocales() {
        return new ArrayList<>(locales);
    }

    public Map<Integer, Country> getLocalesAsMap() {
        if (localesAsMap == null) {
            localesAsMap = getLocales().stream().collect(Collectors.toMap(Country::getId, country -> country));
        }
        return localesAsMap;
    }
}
