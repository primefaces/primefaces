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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;

public class SearchExpressionUtils {

    private SearchExpressionUtils() {
    }

    public static VisitContext createVisitContext(FacesContext context, int hints) {
        if (isHintSet(hints, SearchExpressionHint.SKIP_UNRENDERED)) {
            return VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
        }

        return VisitContext.createVisitContext(context);
    }

    public static boolean isHintSet(int hints, int hint) {
        return (hints & hint) != 0;
    }

     // used by p:resolveClientId
    public static String resolveClientId(String expression, UIComponent source) {
        return SearchExpressionFacade.resolveClientId(
                FacesContext.getCurrentInstance(),
                source,
                expression);
    }

    // used by p:resolveComponent
    public static UIComponent resolveComponent(String expression, UIComponent source) {
        return SearchExpressionFacade.resolveComponent(
                FacesContext.getCurrentInstance(),
                source,
                expression);
    }

    // used by p:resolveClientIds
    public static String resolveClientIds(String expressions, UIComponent source) {
        return SearchExpressionFacade.resolveClientIds(
                FacesContext.getCurrentInstance(),
                source,
                expressions);
    }

    // used by p:resolveWidgetVar
    public static String resolveWidgetVar(String expression, UIComponent component) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIComponent resolvedComponent = SearchExpressionFacade.resolveComponent(
                context,
                component,
                expression);

        if (resolvedComponent instanceof Widget) {
            return "PF('" + ((Widget) resolvedComponent).resolveWidgetVar(context) + "')";
        }
        else {
            throw new FacesException("Component with clientId " + resolvedComponent.getClientId() + " is not a Widget");
        }
    }
}
