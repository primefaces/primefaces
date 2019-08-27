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
package org.primefaces.util;

import org.primefaces.component.api.ClientBehaviorRenderingMode;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;

import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.component.UIForm;
import org.primefaces.component.api.AjaxSource;

/**
 * Helper to generate javascript code of an ajax call
 */
public class AjaxRequestBuilder {

    private static final Logger LOG = Logger.getLogger(AjaxRequestBuilder.class.getName());

    protected StringBuilder buffer;
    protected FacesContext context;

    private boolean preventDefault = false;

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
        if (LangUtils.isValueBlank(form)) {
            if (formComponent == null) {
                formComponent = ComponentTraversalUtils.closestForm(context, component);
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
            result = SearchExpressionFacade.resolveClientId(context, component, source.getForm());
        }

        if (result != null) {
            buffer.append(",f:\"").append(result).append("\"");
        }

        return this;
    }

    public AjaxRequestBuilder form(AjaxSource source, UIComponent component) {
        return form(source, component, null);
    }

    @Deprecated
    public AjaxRequestBuilder form(String form) {
        if (form != null) {
            buffer.append(",f:\"").append(form).append("\"");
        }

        return this;
    }

    public AjaxRequestBuilder process(UIComponent component, String expressions) {
        addExpressions(component, expressions, "p", SearchExpressionHint.RESOLVE_CLIENT_SIDE);

        return this;
    }

    public AjaxRequestBuilder update(UIComponent component, String expressions) {
        addExpressions(component, expressions, "u",
                SearchExpressionHint.VALIDATE_RENDERER | SearchExpressionHint.SKIP_UNRENDERED | SearchExpressionHint.RESOLVE_CLIENT_SIDE);

        return this;
    }

    private AjaxRequestBuilder addExpressions(UIComponent component, String expressions, String key, int options) {
        if (!LangUtils.isValueBlank(expressions)) {
            String resolvedExpressions = SearchExpressionFacade.resolveClientIds(context, component, expressions, options);
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
        if (!LangUtils.isValueBlank(delay) && !delay.equals("none")) {
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

            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                List<String> paramValues = params.get(name);
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

    public AjaxRequestBuilder passParams() {
        buffer.append(",pa:arguments[0]");

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

        if (mode.equals(ClientBehaviorRenderingMode.UNOBSTRUSIVE)) {
            buffer.append("},ext);");
        }
        else if (mode.equals(ClientBehaviorRenderingMode.OBSTRUSIVE)) {
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
        Object fragmentId = attrs.get(Constants.FRAGMENT_ID);
        if (fragmentId != null) {
            buffer.append(",fi:\"").append(fragmentId).append("\"");
        }
    }
}
