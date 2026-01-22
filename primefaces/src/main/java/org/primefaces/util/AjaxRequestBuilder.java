/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.ClientBehaviorRenderingMode;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.expression.SearchExpressionUtils;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.faces.application.ProjectStage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIForm;
import jakarta.faces.component.UIParameter;
import jakarta.faces.component.search.SearchExpressionHint;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.facelets.FaceletException;

/**
 * Helper to generate javascript code of an ajax call
 */
public class AjaxRequestBuilder {

    private static final Logger LOG = Logger.getLogger(AjaxRequestBuilder.class.getName());

    private static final Set<SearchExpressionHint> HINTS_UPDATE = Collections.unmodifiableSet(EnumSet.of(
            SearchExpressionHint.RESOLVE_CLIENT_SIDE));
    private static final Set<SearchExpressionHint> HINTS_UPDATE_IGNORE_NO_RESULT = Collections.unmodifiableSet(EnumSet.of(
            SearchExpressionHint.RESOLVE_CLIENT_SIDE,
            SearchExpressionHint.IGNORE_NO_RESULT));

    private static final Set<SearchExpressionHint> HINTS_PROCESS = Collections.unmodifiableSet(EnumSet.of(
            SearchExpressionHint.RESOLVE_CLIENT_SIDE));

    private static final Set<SearchExpressionHint> HINTS_PROCESS_IGNORE_NO_RESULT = Collections.unmodifiableSet(EnumSet.of(
            SearchExpressionHint.RESOLVE_CLIENT_SIDE,
            SearchExpressionHint.IGNORE_NO_RESULT));

    protected StringBuilder buffer;
    protected FacesContext context;

    private boolean preventDefault;

    public AjaxRequestBuilder(FacesContext context) {
        this.context = context;
        this.buffer = new StringBuilder();
    }

    public AjaxRequestBuilder init() {
        buffer.append("PrimeFaces.ab({");
        return this;
    }

    public AjaxRequestBuilder source(String source) {
        if (source != null) {
            buffer.append("s:").append("\"").append(source).append("\"");
        }
        else {
            buffer.append("s:").append("this");
        }

        return this;
    }

    public AjaxRequestBuilder form(AjaxSource source, UIComponent component, UIForm formComponent) {
        String result = null;

        String form = source.getForm();
        if (LangUtils.isBlank(form)) {
            if (formComponent == null) {
                formComponent = ComponentTraversalUtils.closestForm(component);
            }

            if (formComponent == null) {
                if (context.isProjectStage(ProjectStage.Development)) {
                    String message = "Component '" + component.getClientId(context)
                            + "' should be inside a form or should reference a form via its form attribute."
                            + " We will try to find a fallback form on the client side.";
                    LOG.info(message);
                }
            }
            else {
                result = formComponent.getClientId(context);
            }
        }
        else {
            result = SearchExpressionUtils.resolveClientId(context, component, source.getForm());
        }

        if (result != null) {
            buffer.append(",f:\"").append(result).append("\"");
        }

        return this;
    }

    public AjaxRequestBuilder form(AjaxSource source, UIComponent component) {
        return form(source, component, null);
    }

    //Don't deprecate or remove this method. It's required for themes' backward compatibility.
    public AjaxRequestBuilder form(String form) {
        if (form != null) {
            buffer.append(",f:\"").append(form).append("\"");
        }

        return this;
    }

    public AjaxRequestBuilder process(UIComponent component, String expressions) {
        return process(component, expressions, false);
    }

    public AjaxRequestBuilder process(UIComponent component, String expressions, boolean ignoreNoResult) {
        addExpressions(component, expressions, "p", ignoreNoResult ? HINTS_PROCESS_IGNORE_NO_RESULT : HINTS_PROCESS);

        return this;
    }

    public AjaxRequestBuilder update(UIComponent component, String expressions) {
        return update(component, expressions, false);
    }

    public AjaxRequestBuilder update(UIComponent component, String expressions, boolean ignoreNoResult) {
        addExpressions(component, expressions, "u", ignoreNoResult ? HINTS_UPDATE_IGNORE_NO_RESULT : HINTS_UPDATE);

        return this;
    }

    private AjaxRequestBuilder addExpressions(UIComponent component, String expressions, String key, Set<SearchExpressionHint> hints) {
        if (LangUtils.isNotBlank(expressions)) {
            String resolvedExpressions = SearchExpressionUtils.resolveClientIdsAsString(context, component, expressions, hints,
                    null);
            buffer.append(",").append(key).append(":\"").append(resolvedExpressions).append("\"");
        }

        return this;
    }

    public AjaxRequestBuilder event(String event) {
        buffer.append(",e:\"").append(event).append("\"");

        return this;
    }

    public AjaxRequestBuilder async(boolean async) {
        if (async) {
            buffer.append(",a:true");
        }

        return this;
    }

    public AjaxRequestBuilder skipChildren(boolean skipChildren) {
        if (!skipChildren) {
            buffer.append(",sc:false");
        }

        return this;
    }

    public AjaxRequestBuilder global(boolean global) {
        if (!global) {
            buffer.append(",g:false");
        }

        return this;
    }

    public AjaxRequestBuilder delay(String delay) {
        if (LangUtils.isNotBlank(delay) && !"none".equals(delay)) {
            buffer.append(",d:").append(delay);

            if (context.isProjectStage(ProjectStage.Development)) {
                try {
                    Integer.parseInt(delay);
                }
                catch (NumberFormatException e) {
                    throw new FaceletException("Delay attribute should only take numbers or \"none\"");
                }
            }
        }

        return this;
    }

    public AjaxRequestBuilder timeout(int timeout) {
        if (timeout > 0) {
            buffer.append(",t:").append(timeout);
        }

        return this;
    }

    public AjaxRequestBuilder ignoreAutoUpdate(boolean ignoreAutoUpdate) {
        if (ignoreAutoUpdate) {
            buffer.append(",iau:true");
        }

        return this;
    }

    public AjaxRequestBuilder partialSubmit(boolean value, boolean partialSubmitSet, String partialSubmitFilter) {
        // when set on the component/ajax behavior, always pass the argument
        // otherwise the global setting is passed by the HeadRenderer (PrimeFaces.settings.partialSubmit)
        if (partialSubmitSet) {
            buffer.append(",ps:").append(value);

            if (value) {
                if (partialSubmitFilter != null) {
                    buffer.append(",psf:\"").append(partialSubmitFilter).append("\"");
                }
            }
        }

        return this;
    }

    public AjaxRequestBuilder resetValues(boolean value, boolean resetValuesSet) {
        PrimeConfiguration config = PrimeApplicationContext.getCurrentInstance(context).getConfig();

        //component can override global setting
        boolean resetValues = resetValuesSet ? value : config.isResetValuesEnabled();

        if (resetValues) {
            buffer.append(",rv:true");
        }

        return this;
    }

    public AjaxRequestBuilder onstart(String onstart) {
        if (onstart != null) {
            buffer.append(",onst:function(cfg){").append(onstart).append(";}");
        }

        return this;
    }

    public AjaxRequestBuilder onerror(String onerror) {
        if (onerror != null) {
            buffer.append(",oner:function(xhr,status,error){").append(onerror).append(";}");
        }

        return this;
    }

    public AjaxRequestBuilder onsuccess(String onsuccess) {
        if (onsuccess != null) {
            buffer.append(",onsu:function(data,status,xhr){").append(onsuccess).append(";}");
        }

        return this;
    }

    public AjaxRequestBuilder oncomplete(String oncomplete) {
        if (oncomplete != null) {
            buffer.append(",onco:function(xhr,status,args,data){").append(oncomplete).append(";}");
        }

        return this;
    }

    public AjaxRequestBuilder params(UIComponent component) {
        boolean paramWritten = false;

        for (int i = 0; i < component.getChildCount(); i++) {
            UIComponent child = component.getChildren().get(i);
            if (child instanceof UIParameter) {
                UIParameter parameter = (UIParameter) child;
                Object paramValue = parameter.getValue();

                if (paramValue == null) {
                    continue;
                }

                if (!paramWritten) {
                    paramWritten = true;
                    buffer.append(",pa:[");
                }
                else {
                    buffer.append(",");
                }

                buffer.append("{name:").append("\"").append(EscapeUtils.forJavaScript(parameter.getName())).append("\",value:\"")
                    .append(EscapeUtils.forJavaScript(paramValue.toString())).append("\"}");
            }
        }

        if (paramWritten) {
            buffer.append("]");
        }

        return this;
    }

    public AjaxRequestBuilder params(Map<String, List<String>> params) {
        if (params != null && !params.isEmpty()) {
            buffer.append(",pa:[");

            for (Iterator<Map.Entry<String, List<String>>> it = params.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, List<String>> entry = it.next();
                String name = entry.getKey();
                List<String> paramValues = entry.getValue();

                int size = paramValues.size();
                for (int i = 0; i < size; i++) {
                    String paramValue = paramValues.get(i);
                    if (paramValue == null) {
                        paramValue = "";
                    }
                    buffer.append("{name:").append("\"").append(EscapeUtils.forJavaScript(name)).append("\",value:\"")
                        .append(EscapeUtils.forJavaScript(paramValue)).append("\"}");

                    if (i < (size - 1)) {
                        buffer.append(",");
                    }
                }

                if (it.hasNext()) {
                    buffer.append(",");
                }
            }

            buffer.append("]");
        }

        return this;
    }

    /**
     * Passes parameters to the ajax request. If the first argument is a plain object, it will be converted to an array of name-value pairs.
     * Otherwise, the argument will be passed as-is.
     *
     * @return The current instance of AjaxRequestBuilder
     */
    public AjaxRequestBuilder passParams() {
        buffer.append(",pa:");
        buffer.append("(typeof arguments[0] === 'object' && !Array.isArray(arguments[0]) ");
        buffer.append("? Object.entries(arguments[0]).map(([key, value]) => ({ name: key, value })) ");
        buffer.append(": arguments[0])");
        return this;
    }

    public AjaxRequestBuilder preventDefault() {
        this.preventDefault = true;

        return this;
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public String build() {
        addFragmentConfig();

        buffer.append("});");

        if (preventDefault) {
            buffer.append("return false;");
        }

        String request = buffer.toString();

        reset();

        return request;
    }

    public String buildBehavior(ClientBehaviorRenderingMode mode) {
        addFragmentConfig();

        if (mode == ClientBehaviorRenderingMode.UNOBTRUSIVE) {
            buffer.append("},ext);");
        }
        else if (mode == ClientBehaviorRenderingMode.OBTRUSIVE) {
            buffer.append("});");
        }

        if (preventDefault) {
            buffer.append("return false;");
        }

        String request = buffer.toString();

        reset();

        return request;
    }

    public void reset() {
        buffer.setLength(0);
        preventDefault = false;
    }

    private void addFragmentConfig() {
        Map<Object, Object> attrs = context.getAttributes();
        Object fragmentProcess = attrs.get(Constants.FRAGMENT_PROCESS);
        if (fragmentProcess != null) {
            buffer.append(",fp:\"").append(fragmentProcess).append("\"");
        }
        Object fragmentUpdate = attrs.get(Constants.FRAGMENT_UPDATE);
        if (fragmentUpdate != null) {
            buffer.append(",fu:\"").append(fragmentUpdate).append("\"");
        }
    }
}
