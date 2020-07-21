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
package org.primefaces.expression;

import java.util.List;
import java.util.Set;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public interface MultiSearchExpressionResolver {

    /**
     * Resolves a list of {@link UIComponent} for the last or source {@link UIComponent} and for the given expression string.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param last The last resolved component in the chain.
     *             If it's not a nested expression, it's the same as the source component.
     * @param expression The search expression.
     * @param components The component list to add the resolved {@link UIComponent}s.
     * @param hints The hints.
     */
    void resolveComponents(FacesContext context, UIComponent source, UIComponent last, String expression, List<UIComponent> components,
            Set<SearchExpressionHint> hints);

}
