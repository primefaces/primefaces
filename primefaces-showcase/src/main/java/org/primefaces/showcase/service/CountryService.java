/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
                .map(isoCode -> new Locale("", isoCode))
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
        locales.add(CountryService.toCountry(new Locale("en", "AU")));
        locales.add(CountryService.toCountry(Locale.FRANCE));
        locales.add(CountryService.toCountry(Locale.GERMANY));
        locales.add(CountryService.toCountry(new Locale("de", "AT")));
        locales.add(CountryService.toCountry(new Locale("de", "CH")));
        locales.add(CountryService.toCountry(Locale.ITALY));
        locales.add(CountryService.toCountry(Locale.KOREA));
        locales.add(CountryService.toCountry(new Locale("es", "ES")));
        locales.add(CountryService.toCountry(new Locale("ca", "ES")));
        locales.add(CountryService.toCountry(new Locale("nl", "NL")));
        locales.add(CountryService.toCountry(new Locale("pt", "BR")));
        locales.add(CountryService.toCountry(new Locale("pt", "PT")));
        locales.add(CountryService.toCountry(new Locale("ar", "SA"), true));
        locales.add(CountryService.toCountry(new Locale("ar", "TN"), true));
        locales.add(CountryService.toCountry(new Locale("bg", "BG")));
        locales.add(CountryService.toCountry(new Locale("bn", "BD")));
        locales.add(CountryService.toCountry(new Locale("bs", "BA")));
        locales.add(CountryService.toCountry(new Locale("cs", "CZ")));
        locales.add(CountryService.toCountry(new Locale("el", "GR")));
        locales.add(CountryService.toCountry(new Locale("et", "EE")));
        locales.add(CountryService.toCountry(new Locale("fa", "IR"), true));
        locales.add(CountryService.toCountry(new Locale("fi", "FI")));
        locales.add(CountryService.toCountry(new Locale("da", "DK")));
        locales.add(CountryService.toCountry(new Locale("hi", "IN")));
        locales.add(CountryService.toCountry(new Locale("in", "ID")));
        locales.add(CountryService.toCountry(new Locale("is", "IS")));
        locales.add(CountryService.toCountry(new Locale("hr", "HR")));
        locales.add(CountryService.toCountry(new Locale("ja", "JP")));
        locales.add(CountryService.toCountry(new Locale("hu", "HU")));
        locales.add(CountryService.toCountry(new Locale("he", "IL"), true));
        locales.add(CountryService.toCountry(new Locale("ka", "GE")));
        locales.add(CountryService.toCountry(new Locale("ckb", "IQ"), true));
        locales.add(CountryService.toCountry(new Locale("km", "KH")));
        locales.add(CountryService.toCountry(new Locale("ky", "KG")));
        locales.add(CountryService.toCountry(new Locale("kk", "KZ")));
        locales.add(CountryService.toCountry(new Locale("lt", "LT")));
        locales.add(CountryService.toCountry(new Locale("lv", "LV")));
        locales.add(CountryService.toCountry(new Locale("ms", "MY")));
        locales.add(CountryService.toCountry(new Locale("no", "NO")));
        locales.add(CountryService.toCountry(new Locale("pl", "PL")));
        locales.add(CountryService.toCountry(new Locale("ro", "RO")));
        locales.add(CountryService.toCountry(new Locale("ru", "RU")));
        locales.add(CountryService.toCountry(new Locale("sk", "SK")));
        locales.add(CountryService.toCountry(new Locale("sl", "SI")));
        locales.add(CountryService.toCountry(new Locale("sr", "BA")));
        locales.add(CountryService.toCountry(new Locale("sr", "RS")));
        locales.add(CountryService.toCountry(new Locale("sv", "SE")));
        locales.add(CountryService.toCountry(new Locale("th", "TH")));
        locales.add(CountryService.toCountry(new Locale("tr", "TR")));
        locales.add(CountryService.toCountry(new Locale("uk", "UA")));
        locales.add(CountryService.toCountry(new Locale("uz", "UZ")));
        locales.add(CountryService.toCountry(new Locale("vi", "VN")));
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
