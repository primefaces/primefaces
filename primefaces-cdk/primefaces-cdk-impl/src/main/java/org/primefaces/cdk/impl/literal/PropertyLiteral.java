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
package org.primefaces.cdk.impl.literal;

import org.primefaces.cdk.api.PrimePropertyKeys;
import org.primefaces.cdk.api.Property;

import java.lang.annotation.Annotation;

public class PropertyLiteral implements Property {

    private final String description;
    private final boolean required;
    private final String defaultValue;
    private final String implicitDefaultValue;
    private final boolean callSuper;
    private final Class<?> type;
    private final boolean hide;

    public PropertyLiteral(String description, boolean required, String defaultValue, String implicitDefaultValue, boolean callSuper,
                           Class<?> type, boolean hide) {
        this.description = description;
        this.required = required;
        this.defaultValue = defaultValue;
        this.implicitDefaultValue = implicitDefaultValue;
        this.callSuper = callSuper;
        this.type = type;
        this.hide = hide;
    }

    public static PropertyLiteral fromPropertyKeys(PrimePropertyKeys key) {
        return new PropertyLiteral(key.getDescription(), key.isRequired(), key.getDefaultValue(),
                key.getImplicitDefaultValue(), false, key.getType(), key.isHidden());
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public boolean required() {
        return required;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }

    @Override
    public String implicitDefaultValue() {
        return implicitDefaultValue;
    }

    @Override
    public boolean callSuper() {
        return callSuper;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    @Override
    public boolean hide() {
        return hide;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Property.class;
    }
}
