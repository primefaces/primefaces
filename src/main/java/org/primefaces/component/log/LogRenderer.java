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
package org.primefaces.component.log;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class LogRenderer extends CoreRenderer {

    public static final String CONTAINER_CLASS = "ui-log ui-widget ui-widget-content ui-corner-all";
    public static final String HEADER_CLASS = "ui-log-header ui-widget-header ui-helper-clearfix";
    public static final String CONTENT_CLASS = "ui-log-content";
    public static final String ITEMS_CLASS = "ui-log-items";
    public static final String CLEAR_BUTTON_CLASS = "ui-log-button ui-log-clear ui-corner-all";
    public static final String ALL_BUTTON_CLASS = "ui-log-button ui-log-all ui-corner-all";
    public static final String INFO_BUTTON_CLASS = "ui-log-button ui-log-info ui-corner-all";
    public static final String DEBUG_BUTTON_CLASS = "ui-log-button ui-log-debug ui-corner-all";
    public static final String WARN_BUTTON_CLASS = "ui-log-button ui-log-warn ui-corner-all";
    public static final String ERROR_BUTTON_CLASS = "ui-log-button ui-log-error ui-corner-all";

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Log log = (Log) component;

        encodeMarkup(context, log);
        encodeScript(context, log);
    }

    protected void encodeMarkup(FacesContext context, Log log) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        //container
        writer.startElement("div", log);
        writer.writeAttribute("id", log.getClientId(context), "id");
        writer.writeAttribute("class", CONTAINER_CLASS, null);

        //header
        writer.startElement("div", null);
        writer.writeAttribute("class", HEADER_CLASS, null);
        encodeIcon(context, CLEAR_BUTTON_CLASS, "ui-icon ui-icon-trash", "Clear");
        encodeIcon(context, ALL_BUTTON_CLASS, "ui-icon ui-icon-note", "All");
        encodeIcon(context, INFO_BUTTON_CLASS, "ui-icon ui-icon-info", "Info");
        encodeIcon(context, WARN_BUTTON_CLASS, "ui-icon ui-icon-notice", "Warn");
        encodeIcon(context, DEBUG_BUTTON_CLASS, "ui-icon ui-icon-search", "Debug");
        encodeIcon(context, ERROR_BUTTON_CLASS, "ui-icon ui-icon-alert", "Error");
        writer.endElement("div");

        //content
        writer.startElement("div", log);
        writer.writeAttribute("class", CONTENT_CLASS, null);
        writer.startElement("ul", null);
        writer.writeAttribute("class", ITEMS_CLASS, null);
        writer.endElement("ul");
        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeIcon(FacesContext context, String anchorClass, String iconClass, String title) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", anchorClass, null);
        writer.writeAttribute("title", title, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("a");
    }

    protected void encodeScript(FacesContext context, Log log) throws IOException {
        String clientId = log.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Log", log.resolveWidgetVar(), clientId);
        wb.finish();
    }
}
