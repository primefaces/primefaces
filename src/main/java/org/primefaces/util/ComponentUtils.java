/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.FacesWrapper;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIParameter;
import javax.faces.component.ValueHolder;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.render.Renderer;

import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.Widget;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.expression.SearchExpressionUtils;

public class ComponentUtils {

    public static final EnumSet<VisitHint> VISIT_HINTS_SKIP_UNRENDERED = EnumSet.of(VisitHint.SKIP_UNRENDERED);

    public static final String SKIP_ITERATION_HINT = "javax.faces.visit.SKIP_ITERATION";

    private static final String SB_ESCAPE = ComponentUtils.class.getName() + "#escape";

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
                    Class valueType = value.getClass();
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

    // used by p:component - don't remove!
    @Deprecated
    public static String findComponentClientId(String id) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent component = ComponentTraversalUtils.firstWithId(id, facesContext.getViewRoot());

        return component.getClientId(facesContext);
    }

    public static String escapeSelector(String selector) {
        return selector.replaceAll(":", "\\\\\\\\:");
    }

    @Deprecated
    public static String resolveWidgetVar(String expression) {
        return resolveWidgetVar(expression, FacesContext.getCurrentInstance().getViewRoot());
    }

    @Deprecated
    public static String resolveWidgetVar(String expression, UIComponent component) {
        return SearchExpressionUtils.resolveWidgetVar(expression, component);
    }

    public static boolean isRTL(FacesContext context, RTLAware component) {
        boolean globalValue = PrimeRequestContext.getCurrentInstance(context).isRTL();

        return globalValue || component.isRTL();
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
                        params = new LinkedHashMap<>();
                    }

                    List<String> paramValues = params.get(uiParam.getName());
                    if (paramValues == null) {
                        paramValues = new ArrayList<>();
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
            return skipIterationHint != null && skipIterationHint.booleanValue() == true;
        }
    }

    public static String resolveWidgetVar(FacesContext context, Widget widget) {
        UIComponent component = (UIComponent) widget;
        String userWidgetVar = (String) component.getAttributes().get("widgetVar");

        if (!LangUtils.isValueBlank(userWidgetVar)) {
            return userWidgetVar;
        }
        else {
            return "widget_" + component.getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
        }
    }

    /**
     * Duplicate code from json-simple project under apache license
     * http://code.google.com/p/json-simple/source/browse/trunk/src/org/json/simple/JSONValue.java
     * @deprecated Use {@link EscapeUtils}
     */
    @Deprecated
    public static String escapeText(String text) {
        if (text == null) {
            return null;
        }

        StringBuilder sb = SharedStringBuilder.get(SB_ESCAPE);

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    }
                    else {
                        sb.append(ch);
                    }
            }
        }

        return sb.toString();
    }

    /**
     * @deprecated Use {@link EscapeUtils}
     */
    @Deprecated
    public static String escapeEcmaScriptText(String text) {
        if (text == null) {
            return null;
        }

        StringBuilder sb = SharedStringBuilder.get(SB_ESCAPE);

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\'':
                    sb.append("\\'");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    sb.append(ch);
                    break;
            }
        }

        return sb.toString();
    }

    /**
     * Replace special characters with XML escapes:
     * <pre>
     * &amp; <small>(ampersand)</small> is replaced by &amp;amp;
     * &lt; <small>(less than)</small> is replaced by &amp;lt;
     * &gt; <small>(greater than)</small> is replaced by &amp;gt;
     * &quot; <small>(double quote)</small> is replaced by &amp;quot;
     * </pre>
     *
     * @param string The string to be escaped.
     * @return The escaped string.
     * @deprecated Use {@link EscapeUtils}
     */
    @Deprecated
    public static String escapeXml(String string) {
        StringBuilder sb = SharedStringBuilder.get(SB_ESCAPE, string.length());
        for (int i = 0, length = string.length(); i < length; i++) {
            char c = string.charAt(i);
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Use {@link ComponentTraversalUtils#closestForm(javax.faces.context.FacesContext, javax.faces.component.UIComponent)} instead.
     *
     * @param context
     * @param component
     * @return
     * @deprecated
     */
    @Deprecated
    public static UIComponent findParentForm(FacesContext context, UIComponent component) {
        return ComponentTraversalUtils.closestForm(context, component);
    }

    /**
     * Gets a {@link TimeZone} instance by the parameter "timeZone" which can be String or {@link TimeZone} or null.
     *
     * @param timeZone given time zone
     * @return resolved TimeZone
     */
    public static TimeZone resolveTimeZone(Object timeZone) {
        if (timeZone instanceof String) {
            return TimeZone.getTimeZone((String) timeZone);
        }
        else if (timeZone instanceof TimeZone) {
            return (TimeZone) timeZone;
        }
        else {
            return TimeZone.getDefault();
        }
    }

    public static <T extends Renderer> T getUnwrappedRenderer(FacesContext context, String family, String rendererType, Class<T> rendererClass) {
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
        return component.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }

    /**
     * Checks if the facet and one of the first level child's is rendered.
     * @param facet The Facet component to check
     * @return true when facet and one of the first level child's is rendered.
     */
    public static boolean shouldRenderFacet(UIComponent facet) {
        if (facet == null || !facet.isRendered()) {
            // For any future version of JSF where the f:facet gets a rendered attribute (https://github.com/javaserverfaces/mojarra/issues/4299)
            // or there is only 1 child.
            return false;
        }

        // Facet has no child but is rendered
        if (facet.getChildren().isEmpty()) {
            return true;
        }

        return shouldRenderChildren(facet);
    }

    /**
     * Checks if the component's children are rendered
     * @param component The component to check
     * @return true if one of the first level child's is rendered.
     */
    public static boolean shouldRenderChildren(UIComponent component) {
        for (int i = 0; i < component.getChildren().size(); i++) {
            if (component.getChildren().get(i).isRendered()) {
                return true;
            }
        }

        return false;
    }
}
