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

import javax.faces.context.FacesContext;

public class StyleBuilder {

    private static final String KEY = StyleBuilder.class.getName() + "#style";

    private final StringBuilder sb;

    public StyleBuilder(FacesContext context) {
        sb = SharedStringBuilder.get(context, KEY);
    }

    public StyleBuilder add(boolean condition, String style) {
        if (condition) {
            if (sb.length() != 0) {
                sb.append(Constants.SEMICOLON);
            }
            sb.append(style);
        }
        return this;
    }

    public StyleBuilder add(boolean condition, String attribute, String value) {
        if (condition) {
            if (sb.length() != 0) {
                sb.append(Constants.SEMICOLON);
            }
            sb.append(attribute).append(Constants.COLON).append(value);
        }
        return this;
    }

    public StyleBuilder add(boolean condition, String attribute, String trueValue, String falseValue) {
        add(condition, attribute, trueValue).add(!condition, attribute, falseValue);
        return this;
    }

    public StyleBuilder add(String style) {
        add(LangUtils.isNotBlank(style), style);
        return this;
    }

    public StyleBuilder add(String defaultStyle, String userStyle) {
        add(defaultStyle).add(userStyle);
        return this;
    }

    public String build() {
        String style = sb.toString();
        sb.setLength(0);
        return style;
    }
}
