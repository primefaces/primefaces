package org.primefaces.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;

class LocaleUtilsTest {

    @Test
    void toJavascriptLocale() {
        Locale locale = LocaleUtils.toLocale("pt_BR");
        String js = LocaleUtils.toJavascriptLocale(locale);
        assertEquals("pt-br", js);
    }


    @Test
    void calculateLanguage_truncate() {
        Locale locale = LocaleUtils.toLocale("pt_BR");
        String lang = LocaleUtils.calculateLanguage(locale);
        assertEquals("pt", lang);
    }

    @Test
    void calculateLanguage() {
        Locale locale = LocaleUtils.toLocale("en");
        String lang = LocaleUtils.calculateLanguage(locale);
        assertEquals("en", lang);
    }
}
