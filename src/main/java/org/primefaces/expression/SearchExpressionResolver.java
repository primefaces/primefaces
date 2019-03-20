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
package org.primefaces.expression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * Interface for resolvers, to resolve a {@link UIComponent} by a expression.
 */
public interface SearchExpressionResolver {

    /**
     * Resolves a {@link UIComponent} for the last or source {@link UIComponent} and for the given expression string.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param last The last resolved component in the chain. If it's not a nested expression, it's the same as the source component.
     * @param expression The search expression.
     * @param options The options.
     *
     * @return The resolved {@link UIComponent} or <code>null</code>.
     */
    UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression, int options);
}
