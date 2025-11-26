/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.component.api.FlexAware;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.TouchAware;
import org.primefaces.component.api.UITabPanel;
import org.primefaces.component.api.UITable;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.csp.CspResponseWriter;
import org.primefaces.renderkit.RendererUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.FacesWrapper;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.StateHelper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIParameter;
import javax.faces.component.ValueHolder;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.html.HtmlOutputFormat;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

public class ComponentUtils {

    public static final Set<VisitHint> VISIT_HINTS_SKIP_UNRENDERED = Collections.unmodifiableSet(
            EnumSet.of(VisitHint.SKIP_UNRENDERED));

    public static final Lazy<Set<VisitHint>> VISIT_HINTS_SKIP_ITERATION = new Lazy<>(() ->
            Collections.unmodifiableSet(EnumSet.of(VisitHint.SKIP_ITERATION)));

    // marker for a undefined value when a null check is not reliable enough
    private static final Object UNDEFINED_VALUE = new Object();

    // regex for finding ID's
    private static final Pattern ID_PATTERN = Pattern.compile("\\sid=\"(.*?)\"");


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
                if (component instanceof HtmlOutputFormat) {
                    value = encodeComponent(component, context);
                }
                else {
                    value = valueHolder.getValue();
                }
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
     * @param component ValueHolder instance to look up converter for
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
        return getConverter(context, converterType);
    }

    public static Object getConvertedValue(FacesContext context, UIComponent component, Object value) {
        return getConvertedValue(context, component, getConverter(context, component), value);
    }

    public static Object getConvertedValue(FacesContext context, UIComponent component, Object converter, Object value) {
        Converter converterObject = toConverter(context, converter);
        if (converterObject != null) {
            String submittedValue = Objects.toString(value, null);
            if (LangUtils.isBlank(submittedValue)) {
                submittedValue = null;
            }
            return converterObject.getAsObject(context, component, submittedValue);
        }

        return value;
    }

    public static String getConvertedAsString(FacesContext context, UIComponent component, Object value) {
        return getConvertedAsString(context, component, null, value);
    }

    public static String getConvertedAsString(FacesContext context, UIComponent component, Object converter, Object value) {
        if (value != null) {
            Converter converterObject = toConverter(context, converter);
            if (converterObject == null) {
                converterObject = getConverter(context, value.getClass());
            }
            if (converterObject != null) {
                return converterObject.getAsString(context, component, value);
            }
        }

        return Objects.toString(value, null);
    }

    public static Converter getConverter(FacesContext context, Class<?> forClass) {
        if (forClass == null
                || forClass == Object.class
                || (forClass == String.class
                && !PrimeApplicationContext.getCurrentInstance(context).getConfig().isStringConverterAvailable())) {
            return null;
        }

        return context.getApplication().createConverter(forClass);
    }

    public static Converter toConverter(FacesContext context, Object converter) {
        if (converter == null) {
            return null;
        }
        if (converter instanceof Converter) {
            return (Converter) converter;
        }
        if (converter instanceof String) {
            return context.getApplication().createConverter((String) converter);
        }
        throw new FacesException("Unsupported type: " + converter.getClass());
    }

    public static void decodeBehaviors(FacesContext context, UIComponent component) {
        if (!(component instanceof ClientBehaviorHolder) || !isRequestSource(component, context)) {
            return;
        }

        Map<String, List<ClientBehavior>> behaviors = ((ClientBehaviorHolder) component).getClientBehaviors();
        if (behaviors.isEmpty()) {
            return;
        }

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String behaviorEvent = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        if (behaviorEvent != null) {
            List<ClientBehavior> behaviorsForEvent = behaviors.get(behaviorEvent);
            if (behaviorsForEvent != null && !behaviorsForEvent.isEmpty()) {
                for (ClientBehavior behavior : behaviorsForEvent) {
                    behavior.decode(context, component);
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
        Boolean local = component.isTouchable();
        if (local != null) {
            return local;
        }
        return PrimeRequestContext.getCurrentInstance(context).isTouchable();
    }

    public static boolean isFlex(FacesContext context, FlexAware component) {
        if (component.getFlex() == null) {
            return PrimeRequestContext.getCurrentInstance(context).isFlex();
        }
        return component.getFlex();
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
        return visitContext.getHints().contains(VisitHint.SKIP_ITERATION);
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
     * Duplicate code from OmniFaces project under apache license:
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

    /**
     * Hack for Mojarra as our UIData is copied from Mojarra.
     * This is required because the way how UIData is implemented in Mojarra requires to check for parent iterator-components.
     *
     * @param component
     * @return
     */
    public static boolean isNestedWithinIterator(UIComponent component) {
        UIComponent parent = component;
        while (null != (parent = parent.getParent())) {
            if (parent instanceof javax.faces.component.UIData || isUIRepeat(parent)
                    || (parent instanceof UITabPanel && ((UITabPanel) parent).isRepeating())) {
                return true;
            }
        }

        return false;
    }

    /**
     * GitHub #7763 Renders an in memory UIComponent and updates its ID with an index.
     * For example id="form:test" becomes id="form:test:0".
     *
     * @param context the {@link FacesContext}.
     * @param component the {@link UIComponent} to render.
     * @param index the index number to append to the ID
     * @throws IOException if any IO error occurs
     */
    public static void encodeIndexedId(FacesContext context, UIComponent component, int index) throws IOException {
        // swap writers
        ResponseWriter originalWriter = context.getResponseWriter();
        FastStringWriter fsw = new FastStringWriter();
        ResponseWriter clonedWriter = originalWriter.cloneWithWriter(fsw);
        context.setResponseWriter(clonedWriter);

        // encode the component
        component.encodeAll(context);

        // restore the original writer
        context.setResponseWriter(originalWriter);

        // append index to all id's
        char separator = UINamingContainer.getSeparatorChar(context);
        String encodedComponent = fsw.toString();

        if (clonedWriter instanceof CspResponseWriter) {
            CspResponseWriter cspWriter = (CspResponseWriter) clonedWriter;
            // find all id's to replace
            Matcher matcher = ID_PATTERN.matcher(encodedComponent);
            while (matcher.find()) {
                String oldId = matcher.group(1);
                String newId = oldId + separator + index;
                cspWriter.updateId(oldId, newId);
            }
        }

        // replace 'id=' and 'source:' values
        encodedComponent = encodedComponent.replaceAll("\\sid=\"(.*?)\"", " id=\"$1" + separator + index + "\"");
        encodedComponent = encodedComponent.replaceAll("source:&quot;(.*?)&quot;", " source:&quot;$1" + separator + index + "&quot;");

        originalWriter.write(encodedComponent);
    }


    public static Object getDynamicColumnValue(UIComponent component) {
        org.primefaces.component.api.UIColumn column =
                ComponentTraversalUtils.closest(org.primefaces.component.api.UIColumn.class, component);
        UITable table =
                ComponentTraversalUtils.closest(UITable.class, (UIComponent) column);

        return table.getFieldValue(FacesContext.getCurrentInstance(), column);
    }

    public static String getDynamicColumnValueAsString(UIComponent component) {
        org.primefaces.component.api.UIColumn column =
                ComponentTraversalUtils.closest(org.primefaces.component.api.UIColumn.class, component);
        UITable table =
                ComponentTraversalUtils.closest(UITable.class, (UIComponent) column);

        return table.getConvertedFieldValue(FacesContext.getCurrentInstance(), column);
    }

    /**
     * Encodes the given component locally as HTML.
     * @param component The component to capture HTML output for.
     * @param context The current FacesContext.
     * @return The encoded HTML output of the given component.
     * @throws UncheckedIOException Whenever something fails at I/O level. This would be quite unexpected as it happens locally.
     */
    public static String encodeComponent(UIComponent component, FacesContext context) {
        FastStringWriter output = new FastStringWriter();
        ResponseWriter originalWriter = context.getResponseWriter();

        if (originalWriter != null) {
            context.setResponseWriter(originalWriter.cloneWithWriter(output));
        }
        else {
            context.setResponseWriter(RendererUtils.getRenderKit(context).createResponseWriter(output, "text/html", "UTF-8"));
        }

        try {
            component.encodeAll(context);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        finally {
            if (originalWriter != null) {
                context.setResponseWriter(originalWriter);
            }
        }

        return output.toString();
    }

    /**
     * Executes a callback within the request scope, optionally storing and restoring a value in the FacesContext request map.
     * If no variable name is provided, the callback is executed without storing or restoring any value.
     *
     * @param context The FacesContext associated with the current request.
     * @param var The name of the variable to store in the FacesContext request map.
     *            If null or empty, the callback is executed without storing or restoring any value.
     * @param value The value to store in the FacesContext request map under the given variable name. If null, no value is stored.
     * @param callback A Supplier representing the callback to execute within the request scope.
     * @return The result of executing the callback.
     * @param <T> The type of result returned by the callback.
     */
    public static <T> T executeInRequestScope(FacesContext context, String var, Object value, Supplier<T> callback) {
        // if no var passed just execute the callback
        if (LangUtils.isBlank(var)) {
            return callback.get();
        }

        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        Object oldValue = requestMap.put(var, value);

        try {
            return callback.get();
        }
        finally {
            requestMap.remove(var);
            if (oldValue != null) {
                requestMap.put(var, oldValue);
            }
        }
    }

    /**
     * Execute a callback in a stateless scope by temporarily removing and restoring a variable from the FacesContext attributes.
     *
     * @param context The FacesContext used for accessing attributes
     * @param var The variable to be temporarily removed and restored in the context attributes
     * @param runnable The callback to be executed
     * @param <E> The type of Throwable that may be thrown
     * @throws E If the callback throws an exception of type E
     */
    public static <E extends Throwable> void runWithoutFacesContextVar(FacesContext context, String var, Callbacks.FailableRunnable<E> runnable) throws E {
        // if no var passed just execute the callback
        if (LangUtils.isBlank(var)) {
            runnable.run();
            return;
        }

        Map<Object, Object> stateMap = context.getAttributes();

        Object oldValue = stateMap.remove(var);
        try {
            runnable.run();
        }
        finally {
            if (oldValue != null) {
                stateMap.put(var, oldValue);
            }
        }
    }

    public static int getRenderedChildCount(UIComponent component) {
        int renderedChildCount = 0;

        for (int i = 0; i < component.getChildCount(); i++) {
            UIComponent child = component.getChildren().get(i);
            if (child.isRendered()) {
                renderedChildCount++;
            }
        }

        return renderedChildCount;
    }

    public static boolean isDisabledOrReadonly(UIInput component) {
        return Boolean.parseBoolean(String.valueOf(component.getAttributes().get("disabled")))
                || Boolean.parseBoolean(String.valueOf(component.getAttributes().get("readonly")));
    }

    public static boolean isUIRepeat(UIComponent component) {
        return component.getClass().getName().endsWith("UIRepeat");
    }

    public static Object convertToType(Object value, Class<?> valueType, Logger logger) {
        // skip null
        if (value == null) {
            return null;
        }

        // its already the same type
        if (valueType.isAssignableFrom(value.getClass())) {
            return value;
        }

        FacesContext context = FacesContext.getCurrentInstance();

        // primivites dont need complex conversion, try the ELContext first
        if (BeanUtils.isPrimitiveOrPrimitiveWrapper(valueType)) {
            try {
                return context.getELContext().convertToType(value, valueType);
            }
            catch (ELException e) {
                logger.log(Level.INFO, e, () -> "Could not convert '" + value + "' to " + valueType + " via ELContext!");
            }
        }

        Converter targetConverter = context.getApplication().createConverter(valueType);
        if (targetConverter == null) {
            logger.log(Level.FINE, () -> "Skip conversion as no converter was found for " + valueType
                    + "; Create a JSF Converter for it!");
            return value;
        }

        Converter sourceConverter = context.getApplication().createConverter(value.getClass());
        if (sourceConverter == null) {
            logger.log(Level.FINE, () -> "Skip conversion as no converter was found for " + value.getClass()
                    + "; Create a JSF Converter for it!");
        }

        // first convert the object to string
        String stringValue = sourceConverter == null
                ? value.toString()
                : sourceConverter.getAsString(context, UIComponent.getCurrentComponent(context), value);

        // now convert the string to the required target
        try {
            return targetConverter.getAsObject(context, UIComponent.getCurrentComponent(context), stringValue);
        }
        catch (ConverterException e) {
            logger.log(Level.INFO, e, () -> "Could not convert '" + stringValue + "' to " + valueType + " via " + targetConverter.getClass().getName());
            return value;
        }
    }
}
