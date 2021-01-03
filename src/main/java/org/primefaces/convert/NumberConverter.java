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
package org.primefaces.convert;

import java.util.HashMap;
import java.util.Map;

import org.primefaces.util.HTML;

public class NumberConverter extends javax.faces.convert.NumberConverter implements ClientConverter {

    @Override
    public Map<String, Object> getMetadata() {
        String type = this.getType();
        int maxIntegerDigits = this.getMaxIntegerDigits();
        int minFractionDigits = this.getMinFractionDigits();
        boolean integerOnly = this.isIntegerOnly();

        Map<String, Object> metadata = new HashMap<>();

        metadata.put(HTML.ValidationMetadata.NUMBER_TYPE, type);

        if (maxIntegerDigits != 0) metadata.put(HTML.ValidationMetadata.MAX_INTEGER_DIGITS, maxIntegerDigits);
        if (minFractionDigits != 0) metadata.put(HTML.ValidationMetadata.MIN_FRACTION_DIGITS, minFractionDigits);
        if (integerOnly) metadata.put(HTML.ValidationMetadata.INTEGER_ONLY, "true");

        if ("currency".equals(type)) {
            String currencySymbol = this.getCurrencySymbol();

            if (currencySymbol != null) {
                metadata.put(HTML.ValidationMetadata.CURRENCY_SYMBOL, currencySymbol);
            }
        }

        return metadata;
    }

    @Override
    public String getConverterId() {
        return NumberConverter.CONVERTER_ID;
    }
}
