/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.validate;

import java.util.HashMap;
import java.util.Map;
import org.primefaces.util.HTML;

public class DoubleRangeValidator extends javax.faces.validator.DoubleRangeValidator implements ClientValidator {

    private Map<String, Object> metadata;
    private boolean minimumSet;
    private boolean maximumSet;

    @Override
    public Map<String, Object> getMetadata() {
        metadata = new HashMap<>();
        double min = this.getMinimum();
        double max = this.getMaximum();

        if (minimumSet) {
            metadata.put(HTML.VALIDATION_METADATA.MIN_VALUE, min);
        }

        if (maximumSet) {
            metadata.put(HTML.VALIDATION_METADATA.MAX_VALUE, max);
        }

        return metadata;
    }

    @Override
    public String getValidatorId() {
        return DoubleRangeValidator.VALIDATOR_ID;
    }

    @Override
    public void setMaximum(double maximum) {
        super.setMaximum(maximum);
        this.maximumSet = true;
    }

    @Override
    public void setMinimum(double minimum) {
        super.setMinimum(minimum);
        this.minimumSet = true;
    }
}
