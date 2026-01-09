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
package org.primefaces.util;

import org.primefaces.component.export.ExcelOptions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ExcelStylesManagerTest {

    private static String usDollar;
    private static String ukPound;
    private static String customCurrencySymbol;
    private static DecimalFormat uk = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.UK);
    private static DecimalFormat us = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.US);
    private static DecimalFormat customNumber = new DecimalFormat("#,##0.##", new DecimalFormatSymbols(new Locale("es", "US")));
    private static DecimalFormat customCurrency = new DecimalFormat("¤#,##0.##", new DecimalFormatSymbols(new Locale("es", "US")));

    @BeforeAll
    protected static void setUp() {
        usDollar = us.getDecimalFormatSymbols().getCurrencySymbol();
        ukPound = uk.getDecimalFormatSymbols().getCurrencySymbol();
        customCurrencySymbol = customCurrency.getDecimalFormatSymbols().getCurrencySymbol();
    }

    @ParameterizedTest
    @MethodSource("testLocales")
    void formattedDecimalExcelFormatWithLocaleOnly(Locale locale) {
        ExcelStylesManager sut = new ExcelStylesManager(null, locale, null);

        assertEquals("#,##0.###", sut.getFormattedDecimalExcelFormat(), locale.toString());
    }

    @ParameterizedTest
    @MethodSource("testNumberDecimalFormats")
    void decimalExcelFormatWithCustomFormat(DecimalFormat numberFormat, String expectedFormat) {
        ExcelOptions options = new ExcelOptions();
        options.setNumberFormat(numberFormat);
        ExcelStylesManager sut = new ExcelStylesManager(null, Locale.getDefault(), options);

        assertEquals(expectedFormat, sut.getFormattedDecimalExcelFormat(), "custom currency format");
    }

    @ParameterizedTest
    @MethodSource("testLocales")
    void currencyExcelFormatWithLocaleOnly(Locale locale) {
        ExcelStylesManager sut = new ExcelStylesManager(null, locale, null);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);

        assertEquals("\"" + symbols.getCurrencySymbol() + "\"#,##0.00", sut.getCurrencyExcelFormat(), locale.toString());
    }

    @ParameterizedTest
    @MethodSource("testCurrencyDecimalFormats")
    void currencyExcelFormatWithCustomFormat(DecimalFormat currencyFormat, String expectedFormat) {
        ExcelOptions options = new ExcelOptions();
        options.setCurrencyFormat(currencyFormat);
        ExcelStylesManager sut = new ExcelStylesManager(null, Locale.getDefault(), options);

        assertEquals(expectedFormat, sut.getCurrencyExcelFormat(), "custom currency format");
    }

    public static List<Locale> testLocales() {
        return Arrays.asList(Locale.US, Locale.UK, new Locale("es", "US"));
    }

    public static List<Arguments> testNumberDecimalFormats() {
        return Arrays.asList(
                arguments(DecimalFormat.getInstance(Locale.US), "#,##0.###"),
                arguments(DecimalFormat.getInstance(Locale.UK), "#,##0.###"),
                arguments(customNumber, "#,##0.##" )
        );
    }

    public static List<Arguments> testCurrencyDecimalFormats() {
        return Arrays.asList(
                arguments(us, "\"" + usDollar + "\"#,##0.00"),
                arguments(uk, "\"" + ukPound + "\"#,##0.00"),
                arguments(customCurrency, "\"" + customCurrencySymbol + "\"#,##0.##" )
        );
    }

}
