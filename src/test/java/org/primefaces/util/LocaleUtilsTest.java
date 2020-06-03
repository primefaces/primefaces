package org.primefaces.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.Test;

class LocaleUtilsTest {

    @Test
    void toJavascriptLocale() {
        Locale locale = LocaleUtils.toLocale("pt_BR");
        String js = LocaleUtils.toJavascriptLocale(locale);
        assertEquals("pt-br", js);
    }

}
