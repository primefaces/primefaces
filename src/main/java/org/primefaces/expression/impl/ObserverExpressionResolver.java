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
package org.primefaces.expression.impl;

import java.util.Set;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.expression.ClientIdSearchExpressionResolver;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for the event/observer handling. This is only supported in the AJAX 'update' attribute.
 */
public class ObserverExpressionResolver implements SearchExpressionResolver, ClientIdSearchExpressionResolver {

    @Override
    public UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression,
            Set<SearchExpressionHint> hints) {
        throw new FacesException("@obs is not supported on the server side... expression \"" + expression
                + "\" referenced from \"" + source.getClientId(context) + "\".");
    }

    @Override
    public String resolveClientIds(FacesContext context, UIComponent source, UIComponent last, String expression,
            Set<SearchExpressionHint> hints) {
        // it's just a passthrough
        return expression;
    }
}
