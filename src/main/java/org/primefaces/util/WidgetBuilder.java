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

import org.primefaces.config.PrimeConfiguration;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

/**
 * Helper to generate scripts for widgets.
 */
public class WidgetBuilder {

    protected boolean endFunction = false;
    protected FacesContext context;
    protected PrimeConfiguration configuration;

    public WidgetBuilder(FacesContext context, PrimeConfiguration configuration) {
        this.context = context;
        this.configuration = configuration;
    }

    /**
     *
     * @param widgetClass   Constructor name of the widget
     * @param widgetVar     Name of the client side widget
     * @param id            Client id of the component
     * @param endFunction   If the init script is wrapped by a method and if the endFunction parentheses should be rendered.
     * @throws IOException
     * @return              The current instance.
     */
    protected WidgetBuilder init(String widgetClass, String widgetVar, String id, boolean endFunction) throws IOException {
        this.endFunction = endFunction;

        ResponseWriter rw = context.getResponseWriter();
        rw.write("PrimeFaces.cw(\"");
        rw.write(widgetClass);
        rw.write("\",\"");
        rw.write(widgetVar);
        rw.write("\",{id:\"");
        rw.write(id);
        rw.write("\"");

        return this;
    }

    public WidgetBuilder init(String widgetClass, String widgetVar, String id) throws IOException {
        this.renderScriptBlock(id);

        // AJAX case: since jQuery 3 document ready ($(function() {})) are executed async
        //            this would mean that our oncomplete handlers are probably called before the scripts in the update nodes
        // or
        // we can also skip it when MOVE_SCRIPTS_TO_BOTTOM is enabled as the scripts are already executed when everything is ready
        if ((context.isPostback() && context.getPartialViewContext().isAjaxRequest()) || configuration.isMoveScriptsToBottom()) {
            this.init(widgetClass, widgetVar, id, false);
        }
        else {
            context.getResponseWriter().write("$(function(){");
            this.init(widgetClass, widgetVar, id, true);
        }

        return this;
    }

    @Deprecated
    public WidgetBuilder initWithDomReady(String widgetClass, String widgetVar, String id) throws IOException {
        return init(widgetClass, widgetVar, id);
    }

    public WidgetBuilder initWithWindowLoad(String widgetClass, String widgetVar, String id) throws IOException {

        this.renderScriptBlock(id);
        context.getResponseWriter().write("$(window).on(\"load\",function(){");
        this.init(widgetClass, widgetVar, id, true);

        return this;
    }

    public WidgetBuilder initWithComponentLoad(String widgetClass, String widgetVar, String id, String targetId) throws IOException {

        this.renderScriptBlock(id);
        context.getResponseWriter().write("$(PrimeFaces.escapeClientId(\"" + targetId + "\")).on(\"load\",function(){");
        this.init(widgetClass, widgetVar, id, true);

        return this;
    }

    protected void renderScriptBlock(String id) throws IOException {
        ResponseWriter rw = context.getResponseWriter();
        rw.startElement("script", null);
        rw.writeAttribute("id", id + "_s", null);
        rw.writeAttribute("type", "text/javascript", null);
    }

    /**
     * This should only be used internally if the selector is directly used by jQuery on the client.
     * If PFS is used and specified by the user, {@link #attr(java.lang.String, java.lang.String)} should be used
     * as the users have to escape colons like @(myForm\:myId).
     *
     * @param name
     * @param value
     * @return
     * @throws IOException
     */
    public WidgetBuilder selectorAttr(String name, String value) throws IOException {
        if (value != null) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":\"");
            rw.write(ComponentUtils.escapeSelector(value));
            rw.write("\"");
        }

        return this;
    }

    public WidgetBuilder attr(String name, String value) throws IOException {
        if (value != null) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":\"");
            rw.write(EscapeUtils.forJavaScript(value));
            rw.write("\"");
        }

        return this;
    }

    public WidgetBuilder nativeAttr(String name, String value) throws IOException {
        if (value != null) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":");
            rw.write(value);
        }

        return this;
    }

    public WidgetBuilder nativeAttr(String name, String value, String defaultValue) throws IOException {
        if (value != null && !value.equals(defaultValue)) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":");
            rw.write(value);
        }

        return this;
    }

    public WidgetBuilder attr(String name, Boolean value) throws IOException {
        if (value != null) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":");
            rw.write(Boolean.toString(value));
        }

        return this;
    }

    public WidgetBuilder attr(String name, Number value) throws IOException {
        if (value != null) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":");
            rw.write(value.toString());
        }

        return this;
    }

    public WidgetBuilder attr(String name, String value, String defaultValue) throws IOException {
        if (value != null && !value.equals(defaultValue)) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":\"");
            rw.write(EscapeUtils.forJavaScript(value));
            rw.write("\"");
        }

        return this;
    }

    public WidgetBuilder attr(String name, double value, double defaultValue) throws IOException {
        if (value != defaultValue) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":");
            rw.write(Double.toString(value));
        }

        return this;
    }

    public WidgetBuilder attr(String name, int value, int defaultValue) throws IOException {
        if (value != defaultValue) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":");
            rw.write(Integer.toString(value));
        }

        return this;
    }

    public WidgetBuilder attr(String name, boolean value, boolean defaultValue) throws IOException {
        if (value != defaultValue) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":");
            rw.write(Boolean.toString(value));
        }

        return this;
    }

    public WidgetBuilder callback(String name, String signature, String callback) throws IOException {
        if (callback != null) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":");
            rw.write(signature);
            rw.write("{");
            rw.write(callback);
            rw.write("}");
        }

        return this;
    }

    public WidgetBuilder returnCallback(String name, String signature, String callback) throws IOException {
        if (callback != null) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":");
            rw.write(signature);
            rw.write("{return ");
            rw.write(callback);
            rw.write("}");
        }

        return this;
    }

    public WidgetBuilder callback(String name, String callback) throws IOException {
        if (callback != null) {
            ResponseWriter rw = context.getResponseWriter();
            rw.write(",");
            rw.write(name);
            rw.write(":");
            rw.write(callback);
        }

        return this;
    }

    public WidgetBuilder append(String str) throws IOException {
        context.getResponseWriter().write(str);

        return this;
    }

    public WidgetBuilder append(char chr) throws IOException {
        context.getResponseWriter().write(chr);

        return this;
    }

    public WidgetBuilder append(Number number) throws IOException {
        context.getResponseWriter().write(number.toString());

        return this;
    }

    public void finish() throws IOException {
        ResponseWriter rw = context.getResponseWriter();
        rw.write("});");

        if (endFunction) {
            rw.write("});");
        }

        rw.endElement("script");
    }
}
