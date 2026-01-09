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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test Case for CurrencyValidator.
 */
class BigDecimalValidatorTest {

    private static final DecimalFormat US = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
    private static final DecimalFormat EC = (DecimalFormat) DecimalFormat.getInstance(new Locale("es", "EC"));
    private static final DecimalFormat CUSTOM = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("es", "US")));

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
    void valid() {
        BigDecimalValidator validator = BigDecimalValidator.getInstance();
        BigDecimal expected = new BigDecimal("1234.56");
        BigDecimal negative = new BigDecimal("-1234.56");
        BigDecimal noDecimal = new BigDecimal("1234");
        BigDecimal oneDecimal = new BigDecimal("1234.5");
        BigDecimal threeDecimals = new BigDecimal("1234.567");

        assertEquals(expected, validator.validate("1.234,56", EC), "EC locale");
        assertEquals(negative, validator.validate("-1.234,56", EC), "EC negative");
        assertEquals(noDecimal, validator.validate("1.234", EC), "EC no decimal");
        assertEquals(oneDecimal, validator.validate("1.234,5", EC), "EC 1 decimal");
        assertEquals(threeDecimals, validator.validate("1.234,567", EC), "EC 3 decimals");

        assertEquals(expected, validator.validate("1,234.56", US), "US locale");
        assertEquals(noDecimal, validator.validate("1,234", US), "US no decimal");
        assertEquals(oneDecimal, validator.validate("1,234.5", US), "US 1 decimal");
        assertEquals(threeDecimals, validator.validate("1,234.567", US), "US 3 decimals");
    }

    /**
     * Test Valid integer (non-decimal) currency values
     */
    @Test
    void integerValid() {
        BigDecimalValidator validator = BigDecimalValidator.getInstance();
        BigDecimal expected = new BigDecimal("1234");
        BigDecimal negative = new BigDecimal("-1234");

        assertEquals(expected, validator.validate("1.234", EC), "EC locale");
        assertEquals(negative, validator.validate("-1.234", EC), "EC negative");

        assertEquals(expected, validator.validate("1,234", US), "US locale");
        assertEquals(negative, validator.validate("-1,234", US), "US negative");
    }

    /**
     * Test Infinity is not parsed
     */
    @Test
    void weirdPatternIsNotParsed() {
        BigDecimalValidator validator = BigDecimalValidator.getInstance();

        Number result = validator.validate("74E12341", US);
        assertNull(result);
    }

}
