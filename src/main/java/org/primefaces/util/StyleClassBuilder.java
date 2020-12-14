/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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

import javax.faces.context.FacesContext;

public class StyleClassBuilder {

    private static final String KEY = StyleClassBuilder.class.getName() + "#styleClass";

    private final StringBuilder sb;

    public StyleClassBuilder(FacesContext context) {
        sb = SharedStringBuilder.get(context, KEY);
    }

    public StyleClassBuilder add(boolean condition, String styleClass) {
        if (condition) {
            if (sb.length() != 0) {
                sb.append(Constants.SPACE);
            }
            sb.append(styleClass);
        }
        return this;
    }

    public StyleClassBuilder add(boolean condition, String styleClass, String notStyleClass) {
        add(condition, styleClass).add(!condition, notStyleClass);
        return this;
    }

    public StyleClassBuilder add(String styleClass) {
        add(LangUtils.isNotBlank(styleClass), styleClass);
        return this;
    }

    public StyleClassBuilder add(String defaultStyleClass, String userStyleClass) {
        add(defaultStyleClass).add(userStyleClass);
        return this;
    }

    public String build() {
        String styleClass = sb.toString();

        sb.setLength(0);

        return styleClass;
    }
}
