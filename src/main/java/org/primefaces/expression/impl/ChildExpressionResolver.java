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
package org.primefaces.expression.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for the "@child" keyword.
 */
public class ChildExpressionResolver implements SearchExpressionResolver {

    private static final Pattern PATTERN = Pattern.compile("@child\\((\\d+)\\)");

    @Override
    public UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression, int options) {

        Matcher matcher = PATTERN.matcher(expression);

        if (matcher.matches()) {
            int childNumber = Integer.parseInt(matcher.group(1));

            if (childNumber + 1 > last.getChildCount()) {
                throw new FacesException("Component with clientId \""
                        + last.getClientId(context) + "\" has fewer children as \"" + childNumber + "\". Expression: \"" + expression + "\"");
            }

            List<UIComponent> list = last.getChildren();
            int count = 0;
            for (int i = 0; i < last.getChildCount(); i++) {

                String className = list.get(i).getClass().getName();
                if (!className.contains("UIInstructions") && !className.contains("UILeaf")) {
                    count++;
                }
                if (count == childNumber + 1) {
                    return last.getChildren().get(childNumber);
                }
            }
            if (count < childNumber) {
                throw new FacesException("Component with clientId \""
                        + last.getClientId(context) + "\" has fewer children as \"" + childNumber + "\". Expression: \"" + expression + "\"");
            }
        }
        else {
            throw new FacesException("Expression does not match following pattern @child(n). Expression: \"" + expression + "\"");
        }

        return null;
    }
}
