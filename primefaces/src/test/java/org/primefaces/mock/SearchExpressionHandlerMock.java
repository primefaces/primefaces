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
package org.primefaces.mock;

import org.primefaces.util.LangUtils;

import java.util.ArrayList;
import java.util.List;

import jakarta.faces.component.ContextCallback;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.search.SearchExpressionContext;
import jakarta.faces.component.search.SearchExpressionHandler;
import jakarta.faces.context.FacesContext;

public class SearchExpressionHandlerMock extends SearchExpressionHandler {

    @Override
    public String resolveClientId(SearchExpressionContext context, String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> resolveClientIds(SearchExpressionContext context, String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resolveComponent(SearchExpressionContext context, String expression, ContextCallback arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resolveComponents(SearchExpressionContext context, String expression, ContextCallback arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void invokeOnComponent(SearchExpressionContext context, UIComponent arg1, String expression, ContextCallback arg3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] splitExpressions(FacesContext facesContext, String value) {
        if (LangUtils.isBlank(value)) {
            return null;
        }

        List<String> tokens = new ArrayList<>(5);
        StringBuilder buffer = new StringBuilder();

        int parenthesesCounter = 0;

        int length = value.length();
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c == '(') {
                parenthesesCounter++;
            }

            if (c == ')') {
                parenthesesCounter--;
            }

            if (parenthesesCounter == 0) {
                boolean isSeparator = false;
                for (char separator : new char[]{',', ' '}) {
                    if (c == separator) {
                        isSeparator = true;
                    }
                }

                if (isSeparator) {
                    // lets add token inside buffer to our tokens
                    tokens.add(buffer.toString());
                    // now we need to clear buffer
                    buffer.delete(0, buffer.length());
                }
                else {
                    buffer.append(c);
                }
            }
            else {
                buffer.append(c);
            }
        }

        // lets not forget about part after the separator
        tokens.add(buffer.toString());

        return tokens.toArray(new String[tokens.size()]);
    }

    @Override
    public boolean isPassthroughExpression(SearchExpressionContext context, String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isValidExpression(SearchExpressionContext context, String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
