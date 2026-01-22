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


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * <p><b>BigDecimal Validation</b> and Conversion routines (<code>java.math.BigDecimal</code>).</p>
 *
 * <p>This validator provides a number of methods for
 *    validating/converting a <code>String</code> value to
 *    a <code>BigDecimal</code> using a provided <code>java.text.DecimalFormat</code>.</p>
 *
 * <p>Fraction/decimal values are automatically trimmed to the appropriate length.</p>
 *
 * Modified from Commons Validator to fit PF needs
 * @see <a href="https://github.com/apache/commons-validator">Commons Validator</a>
 */
class BigDecimalValidator implements Serializable {

    private static final long serialVersionUID = -670320911490506772L;

    private static final BigDecimalValidator VALIDATOR = new BigDecimalValidator();

    /**
     * Return a singleton instance of this validator.
     *
     * @return A singleton instance of the BigDecimalValidator.
     */
    public static BigDecimalValidator getInstance() {
        return VALIDATOR;
    }

    /**
     * <p>
     * Validate/convert a <code>BigDecimal</code> using the specified <code>DecimalFormat</code>.
     *
     * @param value The value validation is being performed on.
     * @param format The format used to validate the value against.
     * @return The parsed <code>BigDecimal</code> if valid or <code>null</code> if invalid.
     */
    public BigDecimal validate(String value, DecimalFormat format) {
        return (BigDecimal) parse(value, format);
    }

    /**
     * <p>
     * Parse the value with the specified <code>DecimalFormat</code>.
     * </p>
     *
     * @param value The value to be parsed.
     * @param formatter The Format to parse the value with.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Number parse(String value, DecimalFormat formatter) {
        ParsePosition pos = new ParsePosition(0);
        Number parsedValue = formatter.parse(value, pos);
        if (pos.getErrorIndex() > -1) {
            return null;
        }

        if (pos.getIndex() < value.length()) {
            return null;
        }

        if (parsedValue != null) {
            if (Double.isInfinite(parsedValue.doubleValue())) {
                return null;
            }
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
    protected BigDecimal processParsedValue(Number value, DecimalFormat formatter) {
        BigDecimal decimal;
        if (value instanceof Long) {
            decimal = BigDecimal.valueOf((Long) value);
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
     * @return The multiplying factor for the format.
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
}
