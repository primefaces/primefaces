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
package org.primefaces.validate;

import org.primefaces.util.HTML;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LengthValidator extends javax.faces.validator.LengthValidator implements ClientValidator {

    private boolean minimumSet;
    private boolean maximumSet;

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        int min = this.getMinimum();
        int max = this.getMaximum();

        if (minimumSet) {
            metadata.put(HTML.ValidationMetadata.MIN_LENGTH, min);
        }

        if (maximumSet) {
            metadata.put(HTML.ValidationMetadata.MAX_LENGTH, max);
        }

        return metadata;
    }

    @Override
    public String getValidatorId() {
        return LengthValidator.VALIDATOR_ID;
    }

    @Override
    public void setMaximum(int maximum) {
        super.setMaximum(maximum);
        this.maximumSet = true;
    }

    @Override
    public void setMinimum(int minimum) {
        super.setMinimum(minimum);
        this.minimumSet = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LengthValidator that = (LengthValidator) o;
        return minimumSet == that.minimumSet &&
                maximumSet == that.maximumSet;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minimumSet, maximumSet);
    }
}
