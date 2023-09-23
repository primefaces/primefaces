/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Test Case for CurrencyValidator.
 */
public class CurrencyValidatorTest {

    private static String usDollar;
    private static String ukPound;
    private static String brazilReal;
    private static String customCurrencySymbol;
    private static DecimalFormat uk = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.UK);
    private static DecimalFormat us = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.US);
    private static DecimalFormat brazil = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static DecimalFormat custom = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("es", "US"));

    @BeforeAll
    protected static void setUp() throws Exception {
        usDollar = us.getDecimalFormatSymbols().getCurrencySymbol();
        ukPound = uk.getDecimalFormatSymbols().getCurrencySymbol();
        brazilReal = brazil.getDecimalFormatSymbols().getCurrencySymbol();
        customCurrencySymbol = custom.getDecimalFormatSymbols().getCurrencySymbol();
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

        assertEquals(expected, validator.validate(ukPound + "1,234.56", uk), "UK locale");
        assertEquals(negative, validator.validate("-" + ukPound + "1,234.56", uk), "UK negative");
        assertEquals(noDecimal, validator.validate(ukPound + "1,234", uk), "UK no decimal");
        assertEquals(oneDecimal, validator.validate(ukPound + "1,234.5", uk), "UK 1 decimal");
        assertEquals(expected, validator.validate(ukPound + "1,234.567", uk), "UK 3 decimal");
        assertEquals(expected, validator.validate("1,234.56", uk), "UK no symbol");

        assertEquals(expected, validator.validate(usDollar + "1,234.56", us), "US locale");
        assertEquals(noDecimal, validator.validate(usDollar + "1,234", us), "US no decimal");
        assertEquals(oneDecimal, validator.validate(usDollar + "1,234.5", us), "US 1 decimal");
        assertEquals(expected, validator.validate(usDollar + "1,234.567", us), "US 3 decimal");
        assertEquals(expected, validator.validate("1,234.56", us), "US no symbol");
        assertEquals(negative, validator.validate("-" + usDollar + "1,234.56", us), "US negative");
    }

    /**
     * Test Valid integer (non-decimal) currency values
     */
    @Test
    public void testIntegerValid() {
        CurrencyValidator validator = CurrencyValidator.getInstance();
        BigDecimal expected = new BigDecimal("1234.00");
        BigDecimal negative = new BigDecimal("-1234.00");

        assertEquals(expected, validator.validate(ukPound + "1,234", uk), "UK locale");
        assertEquals(negative, validator.validate("-" + ukPound + "1,234", uk), "UK negative");

        assertEquals(expected, validator.validate(usDollar + "1,234", us), "US locale");
        assertEquals(negative, validator.validate("-" + usDollar + "1,234", us), "US negative");
    }

    /**
     * Test when using <f:convertNumber type="currency" />
     */
    @Test
    public void testFromCurrency() {
        CurrencyValidator validator = CurrencyValidator.getInstance();
        BigDecimal expected = new BigDecimal("20.00");

        assertEquals(expected, validator.validate("¤20.00", us), "US locale");
        assertEquals(expected, validator.validate("¤20.00", uk), "UK locale");
    }

    /**
     * Test Infinity is not parsed
     */
    @Test
    public void testWeirdPatternIsNotParsed() {
        CurrencyValidator validator = CurrencyValidator.getInstance();

        Number result = validator.validate("74E12341", uk);
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
