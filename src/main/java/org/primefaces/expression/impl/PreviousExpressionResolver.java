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
package org.primefaces.expression.impl;

import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.expression.SearchExpressionHint;

import org.primefaces.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for the "@previous" keyword.
 */
public class PreviousExpressionResolver implements SearchExpressionResolver {

    @Override
    public UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression,
            Set<SearchExpressionHint> hints) {
        UIComponent parent = last.getParent();

        if (parent.getChildCount() > 1) {
            List<UIComponent> children = parent.getChildren();
            int index = children.indexOf(last);

            if (index > 0) {
                int nextIndex = -1;
                do {
                    index--;

                    String className = children.get(index).getClass().getName();
                    if (!className.contains("UIInstructions") && !className.contains("UILeaf")) {
                        nextIndex = index;
                    }
                }
                while (nextIndex == -1 && index > 0);

                if (nextIndex != -1) {
                    return children.get(nextIndex);
                }
            }
        }

        return null;
    }

}
