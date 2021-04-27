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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.*;
import java.util.Locale;

/**
 * <p>
 * <b>Currency Validation</b> and Conversion routines (<code>java.math.BigDecimal</code>).
 * </p>
 * <p>
 * This is one implementation of a currency validator that has the following features:
 * </p>
 * <ul>
 * <li>It is <i>lenient</i> about the presence of the <i>currency symbol</i></li>
 * <li>It converts the currency to a <code>java.math.BigDecimal</code></li>
 * </ul>
 * @implNote Modified from Commons Validator to fit PF needs
 * @see <a href="https://github.com/apache/commons-validator">Commons Validator</a>
 */
public class CurrencyValidator implements Serializable {

    private static final long serialVersionUID = -4201640771171486514L;

    private static final CurrencyValidator VALIDATOR = new CurrencyValidator();

    /** DecimalFormat's currency symbol */
    private static final char CURRENCY_SYMBOL = '\u00A4';
    private static final String CURRENCY_SYMBOL_STR = Character.toString(CURRENCY_SYMBOL);
    /** Space hack to fix Brazilian Real and maybe others */
    private static final char NON_BREAKING_SPACE = '\u00A0';
    private static final String NON_BREAKING_SPACE_STR = Character.toString(NON_BREAKING_SPACE);

    /**
     * Return a singleton instance of this validator.
     *
     * @return A singleton instance of the CurrencyValidator.
     */
    public static CurrencyValidator getInstance() {
        return VALIDATOR;
    }

    /**
     * <p>
     * Validate/convert a <code>BigDecimal</code> using the specified <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the number format, system default if null.
     * @return The parsed <code>BigDecimal</code> if valid or <code>null</code> if invalid.
     */
    public BigDecimal validate(String value, Locale locale) {
        return (BigDecimal) parse(value, locale);
    }

    /**
     * <p>
     * Parse the value with the specified <code>Format</code>.
     * </p>
     * <p>
     * This implementation is lenient whether the currency symbol is present or not. The default <code>NumberFormat</code> behavior is for the parsing to "fail"
     * if the currency symbol is missing. This method re-parses with a format without the currency symbol if it fails initially.
     * </p>
     *
     * @param value The value to be parsed.
     * @param formatter The Format to parse the value with.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object parse(String value, DecimalFormat formatter) {
        // check and replace currency symbol
        if (value.indexOf(CURRENCY_SYMBOL) >= 0) {
            value = value.replace(CURRENCY_SYMBOL_STR, formatter.getDecimalFormatSymbols().getCurrencySymbol());
        }

        // between JDK8 and 11 some space characters became non breaking space '\u00A0'
        if (formatter.getPositivePrefix().indexOf(NON_BREAKING_SPACE) >= 0) {
            value = value.replaceAll(Constants.SPACE, NON_BREAKING_SPACE_STR);
        }

        // Initial parse of the value
        Object parsedValue = parseFormat(value, formatter);
        if (parsedValue != null || !(formatter instanceof DecimalFormat)) {
            return parsedValue;
        }

        // Re-parse using a pattern without the currency symbol
        String pattern = formatter.toPattern();
        if (pattern.indexOf(CURRENCY_SYMBOL) >= 0) {
            StringBuilder buffer = new StringBuilder(pattern.length());
            for (int i = 0; i < pattern.length(); i++) {
                if (pattern.charAt(i) != CURRENCY_SYMBOL) {
                    buffer.append(pattern.charAt(i));
                }
            }
            formatter.applyPattern(buffer.toString());
            parsedValue = parseFormat(value, formatter);
        }
        return parsedValue;
    }

    /**
     * <p>
     * Parse the value with the specified <code>Format</code>.
     * </p>
     *
     * @param value The value to be parsed.
     * @param formatter The Format to parse the value with.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object parseFormat(String value, DecimalFormat formatter) {
        ParsePosition pos = new ParsePosition(0);
        Object parsedValue = formatter.parse(value, pos);
        if (pos.getErrorIndex() > -1) {
            return null;
        }

        if (pos.getIndex() < value.length()) {
            return null;
        }

        if (parsedValue != null) {
            parsedValue = processParsedValue(parsedValue, formatter);
        }

        return parsedValue;

    }

    /**
     * Convert the parsed value to a <code>BigDecimal</code>.
     *
     * @param value The parsed <code>Number</code> object created.
     * @param formatter The Format used to parse the value with.
     * @return The parsed <code>Number</code> converted to a <code>BigDecimal</code>.
     */
    protected Object processParsedValue(Object value, DecimalFormat formatter) {
        BigDecimal decimal = null;
        if (value instanceof Long) {
            decimal = BigDecimal.valueOf(((Long) value).longValue());
        }
        else {
            decimal = new BigDecimal(value.toString());
        }

        int scale = determineScale(formatter);
        if (scale >= 0) {
            decimal = decimal.setScale(scale, RoundingMode.DOWN);
        }

        return decimal;
    }

    /**
     * <p>
     * Returns the <i>multiplier</i> of the <code>NumberFormat</code>.
     * </p>
     *
     * @param format The <code>NumberFormat</code> to determine the multiplier of.
     * @return The multiplying factor for the format..
     */
    protected int determineScale(NumberFormat format) {
        int minimumFraction = format.getMinimumFractionDigits();
        int maximumFraction = format.getMaximumFractionDigits();
        if (minimumFraction != maximumFraction) {
            return -1;
        }
        int scale = minimumFraction;
        if (format instanceof DecimalFormat) {
            int multiplier = ((DecimalFormat) format).getMultiplier();
            if (multiplier == 100) { // CHECKSTYLE IGNORE MagicNumber
                scale += 2; // CHECKSTYLE IGNORE MagicNumber
            }
            else if (multiplier == 1000) { // CHECKSTYLE IGNORE MagicNumber
                scale += 3; // CHECKSTYLE IGNORE MagicNumber
            }
        }
        return scale;
    }

    /**
     * <p>
     * Returns a <code>NumberFormat</code> for the specified Locale.
     * </p>
     *
     * @param locale The locale a <code>NumberFormat</code> is required for, system default if null.
     * @return The <code>NumberFormat</code> to created.
     */
    public DecimalFormat getFormat(Locale locale) {
        return (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
    }

    /**
     * <p>
     * Returns a <code>String</code> representing the pattern for this currency.
     * </p>
     *
     * @param locale The locale a <code>NumberFormat</code> is required for, system default if null.
     * @return The <code>String</code> pattern.
     */
    public String getPattern(Locale locale) {
        DecimalFormat format = getFormat(locale);
        String pattern = format.toLocalizedPattern();
        pattern =  pattern.replace(CURRENCY_SYMBOL_STR, format.getDecimalFormatSymbols().getCurrencySymbol());
        String[] patterns = pattern.split(";");
        return patterns[0];
    }

    /**
     * <p>
     * Parse the value using the specified pattern.
     * </p>
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, system default if null.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object parse(String value, Locale locale) {
        value = (value == null ? null : value.trim());
        if (value == null || value.isEmpty()) {
            return null;
        }
        DecimalFormat formatter = getFormat(locale);
        return parse(value, formatter);

    }

}
