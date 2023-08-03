/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.search.SearchExpressionContext;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;

public class SearchExpressionUtils {

    public static final Set<SearchExpressionHint> SET_NONE = Collections.unmodifiableSet(EnumSet.noneOf(SearchExpressionHint.class));
    public static final Set<SearchExpressionHint> SET_SKIP_UNRENDERED = Collections.unmodifiableSet(EnumSet.of(SearchExpressionHint.SKIP_UNRENDERED));
    public static final Set<SearchExpressionHint> SET_RESOLVE_CLIENT_SIDE = Collections.unmodifiableSet(EnumSet.of(SearchExpressionHint.RESOLVE_CLIENT_SIDE));
    public static final Set<SearchExpressionHint> SET_PARENT_FALLBACK = Collections.unmodifiableSet(EnumSet.of(SearchExpressionHint.PARENT_FALLBACK));
    public static final Set<SearchExpressionHint> SET_IGNORE_NO_RESULT = Collections.unmodifiableSet(EnumSet.of(SearchExpressionHint.IGNORE_NO_RESULT));
    public static final Set<SearchExpressionHint> SET_VALIDATE_RENDERER = Collections.unmodifiableSet(EnumSet.of(SearchExpressionHint.VALIDATE_RENDERER));

    private static final Set<javax.faces.component.search.SearchExpressionHint> SET_RESOLVE_CLIENT_SIDE2 =
            Collections.unmodifiableSet(EnumSet.of(javax.faces.component.search.SearchExpressionHint.RESOLVE_CLIENT_SIDE));
    private static final Set<javax.faces.component.search.SearchExpressionHint> SET_RESOLVE_CLIENT_SIDE_OPTIONAL =
            Collections.unmodifiableSet(EnumSet.of(javax.faces.component.search.SearchExpressionHint.RESOLVE_CLIENT_SIDE,
                    javax.faces.component.search.SearchExpressionHint.IGNORE_NO_RESULT));

    private SearchExpressionUtils() {
    }

    public static Set<javax.faces.component.search.SearchExpressionHint> hintsIgnoreNoResult() {
        return EnumSet.of(javax.faces.component.search.SearchExpressionHint.IGNORE_NO_RESULT);
    }

    public static Set<javax.faces.component.search.SearchExpressionHint> hintsIgnoreNoResultClientSide() {
        return EnumSet.of(javax.faces.component.search.SearchExpressionHint.IGNORE_NO_RESULT,
                javax.faces.component.search.SearchExpressionHint.RESOLVE_CLIENT_SIDE);
    }

    public static Set<javax.faces.component.search.SearchExpressionHint> hintsClientSide() {
        return EnumSet.of(javax.faces.component.search.SearchExpressionHint.RESOLVE_CLIENT_SIDE);
    }

    public static VisitContext createVisitContext(FacesContext context, Set<SearchExpressionHint> hints) {
        if (hints.contains(SearchExpressionHint.SKIP_UNRENDERED)) {
            return VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
        }

        return VisitContext.createVisitContext(context);
    }

    public static UIComponent contextlessOptionalResolveComponent(FacesContext context, UIComponent component, String expression) {
        return contextlessResolveComponent(context, component, expression, hintsIgnoreNoResult());
    }

    public static UIComponent contextlessResolveComponent(FacesContext context, UIComponent component, String expression) {
        return contextlessResolveComponent(context, component, expression, EnumSet.noneOf(javax.faces.component.search.SearchExpressionHint.class));
    }

    // NOTE: JSF impls require a modifiable hints set for #resolveComponent and #resolveClientId
    public static UIComponent contextlessResolveComponent(FacesContext context, UIComponent component, String expression,
            Set<javax.faces.component.search.SearchExpressionHint> hints) {

        if (LangUtils.isBlank(expression)) {
            return null;
        }

        AtomicReference<UIComponent> result = new AtomicReference<>();

        context.getApplication().getSearchExpressionHandler().resolveComponent(
            SearchExpressionContext.createSearchExpressionContext(context, component, hints, null),
            expression,
            (ctx, target) -> {
                result.set(target);
            });

        return result.get();
    }

    public static List<UIComponent> contextlessResolveComponents(FacesContext context, UIComponent component, String expression) {
        return contextlessResolveComponents(context, component, expression, EnumSet.noneOf(javax.faces.component.search.SearchExpressionHint.class));
    }

    public static List<UIComponent> contextlessResolveComponents(FacesContext context, UIComponent component, String expression,
            Set<javax.faces.component.search.SearchExpressionHint> hints) {
        List<UIComponent> result = new ArrayList<>();

        context.getApplication().getSearchExpressionHandler().resolveComponents(
            SearchExpressionContext.createSearchExpressionContext(context, component, hints, null),
            expression,
            (ctx, target) -> {
                result.add(target);
            });

        return result;
    }

    public static String resolveClientIdsAsString(FacesContext context, UIComponent component, String expression) {
        return resolveClientIdsAsString(context, component, expression,
                null,
                null);
    }

    public static String resolveClientIdsAsString(FacesContext context, UIComponent component, String expression,
            Set<javax.faces.component.search.SearchExpressionHint> hints, Set<VisitHint> visitHints) {

        if (LangUtils.isBlank(expression)) {
            return null;
        }

        List<String> clientIds = context.getApplication().getSearchExpressionHandler().resolveClientIds(
                SearchExpressionContext.createSearchExpressionContext(context, component, hints, visitHints),
                expression);

        return String.join(",", clientIds);
    }

    public static String resolveClientIdsForClientSide(FacesContext context, UIComponent component, String expression) {
        return resolveClientIdsAsString(context, component, expression,
                SET_RESOLVE_CLIENT_SIDE2,
                null);
    }

    public static String resolveOptionalClientIdsForClientSide(FacesContext context, UIComponent component, String expression) {
        return resolveClientIdsAsString(context, component, expression,
                SET_RESOLVE_CLIENT_SIDE_OPTIONAL,
                null);
    }

    public static String resolveClientId(FacesContext context, UIComponent component, String expression) {
        return context.getApplication().getSearchExpressionHandler().resolveClientId(
                SearchExpressionContext.createSearchExpressionContext(context, component),
                expression);
    }

    public static String resolveOptionalClientIdForClientSide(FacesContext context, UIComponent component, String expression) {
        if (LangUtils.isBlank(expression)) {
            return null;
        }

        return context.getApplication().getSearchExpressionHandler().resolveClientId(
                SearchExpressionContext.createSearchExpressionContext(context, component, hintsIgnoreNoResultClientSide(), null),
                expression);
    }

    public static String resolveClientIdForClientSide(FacesContext context, UIComponent component, String expression) {
        if (LangUtils.isBlank(expression)) {
            return null;
        }

        return context.getApplication().getSearchExpressionHandler().resolveClientId(
                SearchExpressionContext.createSearchExpressionContext(context, component, hintsClientSide(), null),
                expression);
    }

    // used by p:resolveClientId
    public static String resolveClientId(String expression, UIComponent source) {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication().getSearchExpressionHandler().resolveClientId(
                SearchExpressionContext.createSearchExpressionContext(context, source),
                expression);
    }

    // used by p:resolveComponent
    public static UIComponent resolveComponent(String expression, UIComponent source) {
        return contextlessResolveComponent(FacesContext.getCurrentInstance(), source, expression);
    }

    // used by p:resolveClientIds
    public static String resolveClientIds(String expressions, UIComponent source) {
        return resolveClientIdsAsString(FacesContext.getCurrentInstance(), source, expressions);
    }

    // used by p:resolveWidgetVar
    public static String resolveWidgetVar(String expression, UIComponent component) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIComponent resolvedComponent = contextlessResolveComponent(FacesContext.getCurrentInstance(), component, expression);

        if (resolvedComponent instanceof Widget) {
            return ((Widget) resolvedComponent).resolveWidgetVar(context);
        }
        else {
            throw new FacesException("Component with clientId " + resolvedComponent.getClientId() + " is not a Widget");
        }
    }

    // used by p:closestWidgetVar
    public static String closestWidgetVar(UIComponent component) {
        Widget widget = ComponentTraversalUtils.closest(Widget.class, component, true);
        if (widget != null) {
            return widget.resolveWidgetVar(FacesContext.getCurrentInstance());
        }
        else {
            throw new FacesException("Component with clientId " + component.getClientId() + " has no Widget as parent");
        }
    }
}
