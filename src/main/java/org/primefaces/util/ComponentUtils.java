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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.FacesWrapper;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.component.*;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.render.Renderer;

import org.primefaces.component.api.*;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;

public class ComponentUtils {

    public static final Set<VisitHint> VISIT_HINTS_SKIP_UNRENDERED = Collections.unmodifiableSet(
            EnumSet.of(VisitHint.SKIP_UNRENDERED));

    public static final String SKIP_ITERATION_HINT = "javax.faces.visit.SKIP_ITERATION";

    // marker for a undefined value when a null check is not reliable enough
    private static final Object UNDEFINED_VALUE = new Object();

    private ComponentUtils() {
    }

    public static String getValueToRender(FacesContext context, UIComponent component) {
        return getValueToRender(context, component, UNDEFINED_VALUE);
    }

    /**
     * Algorithm works as follows;
     * - If it's an input component, submitted value is checked first since it'd be the value to be used in case validation errors
     * terminates jsf lifecycle
     * - Finally the value of the component is retrieved from backing bean and if there's a converter, converted value is returned
     *
     * @param context       FacesContext instance
     * @param component     UIComponent instance whose value will be returned
     * @param value         The value of UIComponent if already evaluated outside. E.g. in the renderer.
     * @return              End text
     */
    public static String getValueToRender(FacesContext context, UIComponent component, Object value) {
        if (component instanceof ValueHolder) {

            if (component instanceof EditableValueHolder) {
                EditableValueHolder input = (EditableValueHolder) component;
                Object submittedValue = input.getSubmittedValue();
                PrimeConfiguration config = PrimeApplicationContext.getCurrentInstance(context).getConfig();

                if (config.isInterpretEmptyStringAsNull()
                        && submittedValue == null
                        && !input.isLocalValueSet()
                        && context.isValidationFailed()
                        && !input.isValid()) {
                    return null;
                }
                else if (submittedValue != null) {
                    return submittedValue.toString();
                }
            }

            ValueHolder valueHolder = (ValueHolder) component;
            if (value == UNDEFINED_VALUE) {
                value = valueHolder.getValue();
            }

            //format the value as string
            if (value != null) {
                Converter converter = valueHolder.getConverter();
                if (converter == null) {
                    Class<?> valueType = value.getClass();
                    if (valueType == String.class
                            && !PrimeApplicationContext.getCurrentInstance(context).getConfig().isStringConverterAvailable()) {
                        return (String) value;
                    }

                    converter = context.getApplication().createConverter(valueType);
                }

                if (converter != null) {
                    return converter.getAsString(context, component, value);
                }
                else {
                    return value.toString();    //Use toString as a fallback if there is no explicit or implicit converter
                }
            }
            else {
                //component is a value holder but has no value
                return null;
            }
        }

        //component it not a value holder
        return null;
    }

    /**
     * Finds appropriate converter for a given value holder
     *
     * @param context   FacesContext instance
     * @param component ValueHolder instance to look converter for
     * @return          Converter
     */
    public static Converter getConverter(FacesContext context, UIComponent component) {
        if (!(component instanceof ValueHolder)) {
            return null;
        }

        Converter converter = ((ValueHolder) component).getConverter();
        if (converter != null) {
            return converter;
        }

        ValueExpression valueExpression = component.getValueExpression("value");
        if (valueExpression == null) {
            return null;
        }

        Class<?> converterType = valueExpression.getType(context.getELContext());
        if (converterType == null || converterType == Object.class) {
            // no conversion is needed
            return null;
        }

        if (converterType == String.class
                && !PrimeApplicationContext.getCurrentInstance(context).getConfig().isStringConverterAvailable()) {
            return null;
        }

        return context.getApplication().createConverter(converterType);
    }

    public static Object getConvertedValue(FacesContext context, UIComponent component, Object value) {
        String submittedValue = Objects.toString(value, null);
        if (LangUtils.isValueBlank(submittedValue)) {
            submittedValue = null;
        }

        Converter<?> converter = getConverter(context, component);
        if (converter != null) {
            return converter.getAsObject(context, component, submittedValue);
        }

        return value;
    }

    public static void decodeBehaviors(FacesContext context, UIComponent component) {
        if (!(component instanceof ClientBehaviorHolder)) {
            return;
        }

        Map<String, List<ClientBehavior>> behaviors = ((ClientBehaviorHolder) component).getClientBehaviors();
        if (behaviors.isEmpty()) {
            return;
        }

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String behaviorEvent = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if (null != behaviorEvent) {
            List<ClientBehavior> behaviorsForEvent = behaviors.get(behaviorEvent);

            if (behaviorsForEvent != null && !behaviorsForEvent.isEmpty()) {
                String behaviorSource = params.get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);
                String clientId = component.getClientId(context);

                if (behaviorSource != null && clientId.equals(behaviorSource)) {
                    for (ClientBehavior behavior : behaviorsForEvent) {
                        behavior.decode(context, component);
                    }
                }
            }
        }
    }

    public static String escapeSelector(String selector) {
        return selector.replace(":", "\\\\:");
    }

    public static boolean isRTL(FacesContext context, RTLAware component) {
        return component.isRTL() || PrimeRequestContext.getCurrentInstance(context).isRTL();
    }

    public static boolean isTouchable(FacesContext context, TouchAware component) {
        return component.isTouchable() || PrimeRequestContext.getCurrentInstance(context).isTouchable();
    }

    public static boolean isFlex(FacesContext context, FlexAware component) {
        return component.isFlex() || PrimeRequestContext.getCurrentInstance(context).isFlex();
    }

    public static void processDecodesOfFacetsAndChilds(UIComponent component, FacesContext context) {
        if (component.getFacetCount() > 0) {
            for (UIComponent facet : component.getFacets().values()) {
                facet.processDecodes(context);
            }
        }

        if (component.getChildCount() > 0) {
            for (int i = 0, childCount = component.getChildCount(); i < childCount; i++) {
                UIComponent child = component.getChildren().get(i);
                child.processDecodes(context);
            }
        }
    }

    public static void processValidatorsOfFacetsAndChilds(UIComponent component, FacesContext context) {
        if (component.getFacetCount() > 0) {
            for (UIComponent facet : component.getFacets().values()) {
                facet.processValidators(context);
            }
        }

        if (component.getChildCount() > 0) {
            for (int i = 0, childCount = component.getChildCount(); i < childCount; i++) {
                UIComponent child = component.getChildren().get(i);
                child.processValidators(context);
            }
        }
    }

    public static void processUpdatesOfFacetsAndChilds(UIComponent component, FacesContext context) {
        if (component.getFacetCount() > 0) {
            for (UIComponent facet : component.getFacets().values()) {
                facet.processUpdates(context);
            }
        }

        if (component.getChildCount() > 0) {
            for (int i = 0, childCount = component.getChildCount(); i < childCount; i++) {
                UIComponent child = component.getChildren().get(i);
                child.processUpdates(context);
            }
        }
    }

    public static NavigationCase findNavigationCase(FacesContext context, String outcome) {
        ConfigurableNavigationHandler navHandler = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();
        String outcomeValue = (outcome == null) ? context.getViewRoot().getViewId() : outcome;

        return navHandler.getNavigationCase(context, null, outcomeValue);
    }

    public static Map<String, List<String>> getUIParams(UIComponent component) {
        Map<String, List<String>> params = null;

        for (int i = 0; i < component.getChildCount(); i++) {
            UIComponent child = component.getChildren().get(i);
            if (child.isRendered() && (child instanceof UIParameter)) {
                UIParameter uiParam = (UIParameter) child;

                if (!uiParam.isDisable()) {
                    if (params == null) {
                        params = new LinkedHashMap<>(5);
                    }

                    List<String> paramValues = params.get(uiParam.getName());
                    if (paramValues == null) {
                        paramValues = new ArrayList<>(2);
                        params.put(uiParam.getName(), paramValues);
                    }

                    paramValues.add(String.valueOf(uiParam.getValue()));
                }
            }
        }

        return params;
    }

    public static boolean isSkipIteration(VisitContext visitContext, FacesContext context) {
        if (PrimeApplicationContext.getCurrentInstance(context).getEnvironment().isAtLeastJsf21()) {
            return visitContext.getHints().contains(VisitHint.SKIP_ITERATION);
        }
        else {
            Boolean skipIterationHint = (Boolean) visitContext.getFacesContext().getAttributes().get(SKIP_ITERATION_HINT);
            return skipIterationHint != null && skipIterationHint;
        }
    }

    public static <T extends Renderer> T getUnwrappedRenderer(FacesContext context, String family, String rendererType) {
        Renderer renderer = context.getRenderKit().getRenderer(family, rendererType);

        while (renderer instanceof FacesWrapper) {
            renderer = (Renderer) ((FacesWrapper) renderer).getWrapped();
        }

        return (T) renderer;
    }

    /**
     * Calculates the current viewId - we can't get it from the ViewRoot if it's not available.
     *
     * @param context The {@link FacesContext}.
     * @return The current viewId.
     */
    public static String calculateViewId(FacesContext context) {
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        String viewId = (String) requestMap.get("javax.servlet.include.path_info");

        if (viewId == null) {
            viewId = context.getExternalContext().getRequestPathInfo();
        }

        if (viewId == null) {
            viewId = (String) requestMap.get("javax.servlet.include.servlet_path");
        }

        if (viewId == null) {
            viewId = context.getExternalContext().getRequestServletPath();
        }

        return viewId;
    }

    /**
     * Duplicate code from OmniFacew project under apache license:
     * https://github.com/omnifaces/omnifaces/blob/develop/license.txt
     * <p>
     * URI-encode the given string using UTF-8. URIs (paths and filenames) have different encoding rules as compared to
     * URL query string parameters. {@link URLEncoder} is actually only for www (HTML) form based query string parameter
     * values (as used when a webbrowser submits a HTML form). URI encoding has a lot in common with URL encoding, but
     * the space has to be %20 and some chars doesn't necessarily need to be encoded.
     * @param string The string to be URI-encoded using UTF-8.
     * @return The given string, URI-encoded using UTF-8, or <code>null</code> if <code>null</code> was given.
     * @throws UnsupportedEncodingException if UTF-8 is not supported
     */
    public static String encodeURI(String string) throws UnsupportedEncodingException {
        if (string == null) {
            return null;
        }

        return URLEncoder.encode(string, "UTF-8")
                .replace("+", "%20")
                .replace("%21", "!")
                .replace("%27", "'")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%7E", "~");
    }

    /**
     * Creates an RFC 6266 Content-Dispostion header following all UTF-8 conventions.
     * <p>
     * @param value e.g. "attachment"
     * @param filename the name of the file
     * @return a valid Content-Disposition header in UTF-8 format
     */
    public static String createContentDisposition(String value, String filename) {
        try {
            return String.format("%s;filename=\"%2$s\"; filename*=UTF-8''%2$s", value, encodeURI(filename));
        }
        catch (UnsupportedEncodingException e) {
            throw new FacesException(e);
        }
    }

    public static boolean isRequestSource(UIComponent component, FacesContext context) {
        String partialSource = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);
        return component.getClientId(context).equals(partialSource);
    }

    public static boolean isRequestSource(UIComponent component, FacesContext context, String event) {
        ExternalContext externalContext = context.getExternalContext();
        String partialSource = externalContext.getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);
        String partialEvent = externalContext.getRequestParameterMap().get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        return component.getClientId(context).equals(partialSource) && partialEvent.equals(event);
    }

    public static Object getLabel(FacesContext facesContext, UIComponent component) {
        String label = (String) component.getAttributes().get("label");

        if (label == null) {
            label = component.getClientId(facesContext);
        }

        return label;
    }

    /**
     * Checks if the facet and one of the first level children is rendered.
     * @param facet The Facet component to check
     * @param ignoreChildren flag to ignore children and only check the facet itself
     * @return true if the facet should be rendered, false if not
     */
    public static boolean shouldRenderFacet(UIComponent facet, boolean ignoreChildren) {
        if (facet == null || !facet.isRendered()) {
            // For any future version of JSF where the f:facet gets a rendered attribute (https://github.com/javaserverfaces/mojarra/issues/4299)
            // or there is only 1 child.
            return false;
        }

        // Facet has no child but is rendered
        if (ignoreChildren || facet.getChildCount() == 0) {
            return true;
        }

        return shouldRenderChildren(facet);
    }

    /**
     * Checks if the facet and one of the first level children is rendered.
     * @param facet The Facet component to check
     * @return true when facet and one of the first level children is rendered.
     */
    public static boolean shouldRenderFacet(UIComponent facet) {
        return shouldRenderFacet(facet, false);
    }

    /**
     * Checks if the component's children are rendered
     * @param component The component to check
     * @return true if one of the first level child's is rendered.
     */
    public static boolean shouldRenderChildren(UIComponent component) {
        for (int i = 0; i < component.getChildCount(); i++) {
            if (component.getChildren().get(i).isRendered()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Tries to retrieve value from stateHelper by key first. If the value is not present (or is null),
     * then it is retrieved from defaultValueSupplier.
     *
     * Should be removed when {@link StateHelper} is extended with similar functionality.
     * (see https://github.com/eclipse-ee4j/mojarra/issues/4568 for details)
     * @param stateHelper The stateHelper to try to retrieve value from
     * @param key The key under which value is stored in the stateHelper
     * @param defaultValueSupplier The object, from which default value is retrieved
     * @param <T> the expected type of returned value
     * @return value from stateHelper or defaultValueSupplier
     */
    public static <T> T eval(StateHelper stateHelper, Serializable key, Supplier<T> defaultValueSupplier) {
        T value = (T) stateHelper.eval(key, null);
        if (value == null) {
            value = defaultValueSupplier.get();
        }
        return value;
    }

    /**
     * Tries to retrieve value from stateHelper by key first. If the value is not present (or is null),
     * then it is retrieved from defaultValueSupplier.
     *
     * Should be removed when {@link StateHelper} is extended with similar functionality.
     * (see https://github.com/eclipse-ee4j/mojarra/issues/4568 for details)
     * @param stateHelper The stateHelper to try to retrieve value from
     * @param key The key under which value is stored in the stateHelper
     * @param defaultValueSupplier The object, from which default value is retrieved
     * @param <T> the expected type of returned value
     * @return value from stateHelper or defaultValueSupplier
     */
    public static <T> T computeIfAbsent(StateHelper stateHelper, Serializable key, Supplier<T> defaultValueSupplier) {
        T value = (T) stateHelper.get(key);
        if (value == null) {
            value = defaultValueSupplier.get();
            stateHelper.put(key, value);
        }
        return value;
    }

    public static boolean isNestedWithinIterator(UIComponent component) {
        return invokeOnClosestIteratorParent(component, p -> { }, false);
    }

    public static boolean invokeOnClosestIteratorParent(UIComponent component, Consumer<UIComponent> function, boolean includeSelf) {
        Predicate<UIComponent> isIteratorComponent = p -> p instanceof javax.faces.component.UIData
                || p.getClass().getName().endsWith("UIRepeat")
                || (p instanceof UITabPanel && ((UITabPanel) p).isRepeating());

        UIComponent parent = component;
        while (null != (parent = parent.getParent())) {
            if (isIteratorComponent.test(parent)) {
                function.accept(parent);
                return true;
            }
        }

        if (includeSelf && isIteratorComponent.test(component)) {
            function.accept(component);
            return true;
        }

        return false;
    }

    public static ViewPoolingResetMode isViewPooling(FacesContext context) {
        if (context.getViewRoot() != null) {
            Object mode = context.getViewRoot().getAttributes().get("oam.view.resetSaveStateMode");

            if (Objects.equals(mode, 1)) {
                return ViewPoolingResetMode.SOFT;
            }
            if (Objects.equals(mode, 2)) {
                return ViewPoolingResetMode.HARD;
            }
        }
        return ViewPoolingResetMode.OFF;
    }

    // See MyFaces ViewPoolProcessor
    public enum ViewPoolingResetMode {
        OFF,
        SOFT,
        HARD;
    }
}
