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
package org.primefaces.expression;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletException;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.ComponentUtils;

public class SearchExpressionUtils {

    public static final Set<SearchExpressionHint> SET_NONE = Collections.unmodifiableSet(EnumSet.noneOf(SearchExpressionHint.class));
    public static final Set<SearchExpressionHint> SET_SKIP_UNRENDERED = Collections.unmodifiableSet(EnumSet.of(SearchExpressionHint.SKIP_UNRENDERED));
    public static final Set<SearchExpressionHint> SET_RESOLVE_CLIENT_SIDE = Collections.unmodifiableSet(EnumSet.of(SearchExpressionHint.RESOLVE_CLIENT_SIDE));
    public static final Set<SearchExpressionHint> SET_PARENT_FALLBACK = Collections.unmodifiableSet(EnumSet.of(SearchExpressionHint.PARENT_FALLBACK));
    public static final Set<SearchExpressionHint> SET_IGNORE_NO_RESULT = Collections.unmodifiableSet(EnumSet.of(SearchExpressionHint.IGNORE_NO_RESULT));
    public static final Set<SearchExpressionHint> SET_VALIDATE_RENDERER = Collections.unmodifiableSet(EnumSet.of(SearchExpressionHint.VALIDATE_RENDERER));

    private SearchExpressionUtils() {
    }

    public static VisitContext createVisitContext(FacesContext context, Set<SearchExpressionHint> hints) {
        if (hints.contains(SearchExpressionHint.SKIP_UNRENDERED)) {
            return VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
        }

        return VisitContext.createVisitContext(context);
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
            return ((Widget) resolvedComponent).resolveWidgetVar(context);
        }
        else {
            throw new FacesException("Component with clientId " + resolvedComponent.getClientId() + " is not a Widget");
        }
    }

    // used by p:closestWidgetVar
    public static String closestWidgetVar(UIComponent component) {
        Widget widget = ComponentTraversalUtils.closest(Widget.class, component);
        if (widget != null) {
            return widget.resolveWidgetVar(FacesContext.getCurrentInstance());
        }
        else {
            throw new FaceletException("Component with clientId " + component.getClientId() + " has no Widget as parent");
        }
    }
}
