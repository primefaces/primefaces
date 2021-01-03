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
package org.primefaces.validate.bean;

import java.util.Map;

import org.primefaces.util.HTML;

public class NegativeClientValidationConstraint extends AbstractClientValidationConstraint {

    public static final String CONSTRAINT_ID = "Negative";
    public static final String MESSAGE_METADATA = "data-p-negative-msg";
    public static final String CONSTRAINT_CLASS_NAME = CONSTRAINT_PACKAGE  + "." + CONSTRAINT_ID;
    public static final String MESSAGE_ID = "{" + CONSTRAINT_CLASS_NAME + ".message}";
    public static final String MAX_VALUE = "-0.0000001";

    public NegativeClientValidationConstraint() {
        super(MESSAGE_ID, MESSAGE_METADATA);
    }

    @Override
    protected void processMetadata(Map<String, Object> metadata, Map<String, Object> attrs) {
        metadata.put(HTML.ValidationMetadata.MAX_VALUE, -Double.MIN_VALUE);
    }

    @Override
    public String getValidatorId() {
        return CONSTRAINT_ID ;
    }
}
