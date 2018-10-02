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

/**
 * Helper to generate javascript code of an ajax call
 */
public class AjaxRequestBuilder {

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

    public AjaxRequestBuilder form(String form) {
        if (form != null) {
            buffer.append(",f:\"").append(form).append("\"");
        }

        return this;
    }

    public AjaxRequestBuilder process(UIComponent component, String expressions) {
        addExpressions(component, expressions, "p", SearchExpressionHint.NONE);

        return this;
    }

    public AjaxRequestBuilder update(UIComponent component, String expressions) {
        addExpressions(component, expressions, "u", SearchExpressionHint.VALIDATE_RENDERER | SearchExpressionHint.SKIP_UNRENDERED);

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
        PrimeConfiguration config = PrimeApplicationContext.getCurrentInstance(context).getConfig();

        //component can override global setting
        boolean partialSubmit = partialSubmitSet ? value : config.isPartialSubmitEnabled();

        if (partialSubmit) {
            buffer.append(",ps:true");

            if (partialSubmitFilter != null) {
                buffer.append(",psf:\"").append(partialSubmitFilter).append("\"");
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
            buffer.append(",onco:function(xhr,status,args){").append(oncomplete).append(";}");
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
