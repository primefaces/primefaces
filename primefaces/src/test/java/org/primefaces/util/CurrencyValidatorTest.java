/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Test Case for CurrencyValidator.
 */
public class CurrencyValidatorTest {

    private static String usDollar;
    private static String ukPound;
    private static String brazilReal;
    private static String customCurrencySymbol;
    private static Locale brazil = new Locale("pt", "BR");
    private static Locale custom = new Locale("es", "US");

    @BeforeAll
    protected static void setUp() throws Exception {
        usDollar = (new DecimalFormatSymbols(Locale.US)).getCurrencySymbol();
        ukPound = (new DecimalFormatSymbols(Locale.UK)).getCurrencySymbol();
        brazilReal = (new DecimalFormatSymbols(brazil)).getCurrencySymbol();
        customCurrencySymbol = (new DecimalFormatSymbols(custom)).getCurrencySymbol();
    }

    private static int getVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        }
        else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }

    /**
     * Test Valid currency values
     */
    @Test
    public void testValid() {
        CurrencyValidator validator = CurrencyValidator.getInstance();
        BigDecimal expected = new BigDecimal("1234.56");
        BigDecimal negative = new BigDecimal("-1234.56");
        BigDecimal noDecimal = new BigDecimal("1234.00");
        BigDecimal oneDecimal = new BigDecimal("1234.50");

        assertEquals(expected, validator.validate(ukPound + "1,234.56", Locale.UK), "UK locale");
        assertEquals(negative, validator.validate("-" + ukPound + "1,234.56", Locale.UK), "UK negative");
        assertEquals(noDecimal, validator.validate(ukPound + "1,234", Locale.UK), "UK no decimal");
        assertEquals(oneDecimal, validator.validate(ukPound + "1,234.5", Locale.UK), "UK 1 decimal");
        assertEquals(expected, validator.validate(ukPound + "1,234.567", Locale.UK), "UK 3 decimal");
        assertEquals(expected, validator.validate("1,234.56", Locale.UK), "UK no symbol");

        assertEquals(expected, validator.validate(usDollar + "1,234.56", Locale.US), "US locale");
        assertEquals(noDecimal, validator.validate(usDollar + "1,234", Locale.US), "US no decimal");
        assertEquals(oneDecimal, validator.validate(usDollar + "1,234.5", Locale.US), "US 1 decimal");
        assertEquals(expected, validator.validate(usDollar + "1,234.567", Locale.US), "US 3 decimal");
        assertEquals(expected, validator.validate("1,234.56", Locale.US), "US no symbol");
        if (getVersion() > 8) {
            assertEquals(negative, validator.validate("-" + usDollar + "1,234.56", Locale.US), "US negative");
        }
        else {
            assertEquals(negative, validator.validate("(" + usDollar + "1,234.56)", Locale.US), "US negative");
        }
    }

    /**
     * Test Valid integer (non-decimal) currency values
     */
    @Test
    public void testIntegerValid() {
        CurrencyValidator validator = CurrencyValidator.getInstance();
        BigDecimal expected = new BigDecimal("1234.00");
        BigDecimal negative = new BigDecimal("-1234.00");

        assertEquals(expected, validator.validate(ukPound + "1,234", Locale.UK), "UK locale");
        assertEquals(negative, validator.validate("-" + ukPound + "1,234", Locale.UK), "UK negative");

        assertEquals(expected, validator.validate(usDollar + "1,234", Locale.US), "US locale");
        if (getVersion() > 8) {
            assertEquals(negative, validator.validate("-" + usDollar + "1,234", Locale.US), "US negative");
        }
        else {
            assertEquals(negative, validator.validate("(" + usDollar + "1,234)", Locale.US), "US negative");
        }
    }

    /**
     * Test when using <f:convertNumber type="currency" />
     */
    @Test
    public void testFromCurrency() {
        CurrencyValidator validator = CurrencyValidator.getInstance();
        BigDecimal expected = new BigDecimal("20.00");

        assertEquals(expected, validator.validate("¤20.00", Locale.US), "US locale");
        assertEquals(expected, validator.validate("¤20.00", Locale.UK), "UK locale");
    }

    /**
     * Test Patterns
     */
    @Test
    public void testExcelPatterns() {
        CurrencyValidator validator = CurrencyValidator.getInstance();

        assertEquals("\"" + usDollar + "\"" + "#,##0.00", validator.getExcelPattern(Locale.US), "US");
        assertEquals("\"" + ukPound + "\"" +  "#,##0.00", validator.getExcelPattern(Locale.UK), "UK");
        assertEquals("\"" + customCurrencySymbol + "\"#,##0.00", validator.getExcelPattern(custom), "Custom");
    }

    /**
     * Test Infinity is not parsed
     */
    @Test
    public void testWeirdPatternIsNotParsed() {
        CurrencyValidator validator = CurrencyValidator.getInstance();

        Number result = validator.validate("74E12341", Locale.UK);
        assertNull(result);
    }

    @Test
    public void testBrazilianReal() {
        // Arrange
        CurrencyValidator validator = CurrencyValidator.getInstance();
        BigDecimal expected = new BigDecimal("1234.56");

        // Act
        Number result = validator.validate(brazilReal + " 1.234,56", brazil);

        // Assert
        assertEquals(expected, result, "Brazilian locale");
    }

}
