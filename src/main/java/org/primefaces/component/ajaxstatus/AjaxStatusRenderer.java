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
package org.primefaces.component.ajaxstatus;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class AjaxStatusRenderer extends CoreRenderer {

    public static final String START = "start";
    public static final String SUCCESS = "success";
    public static final String COMPLETE = "complete";
    public static final String ERROR = "error";
    public static final String DEFAULT = "default";
    public static final String CALLBACK_SIGNATURE = "function()";
    public static final String[] EVENTS = {START, SUCCESS, COMPLETE, ERROR};

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        AjaxStatus status = (AjaxStatus) component;

        encodeMarkup(context, status);
        encodeScript(context, status);
    }

    protected void encodeScript(FacesContext context, AjaxStatus status) throws IOException {
        String clientId = status.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("AjaxStatus", status.resolveWidgetVar(), clientId);

        wb.callback(START, CALLBACK_SIGNATURE, status.getOnstart())
                .callback(ERROR, CALLBACK_SIGNATURE, status.getOnerror())
                .callback(SUCCESS, CALLBACK_SIGNATURE, status.getOnsuccess())
                .callback(COMPLETE, CALLBACK_SIGNATURE, status.getOncomplete());

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, AjaxStatus status) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = status.getClientId(context);

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);

        if (status.getStyle() != null) {
            writer.writeAttribute("style", status.getStyle(), "style");
        }
        if (status.getStyleClass() != null) {
            writer.writeAttribute("class", status.getStyleClass(), "styleClass");
        }

        for (String event : EVENTS) {
            UIComponent facet = status.getFacet(event);

            if (facet != null) {
                encodeFacet(context, clientId, facet, event, true);
            }
        }

        UIComponent defaultFacet = status.getFacet(DEFAULT);
        if (defaultFacet != null) {
            encodeFacet(context, clientId, defaultFacet, DEFAULT, false);
        }

        writer.endElement("div");
    }

    protected void encodeFacet(FacesContext context, String clientId, UIComponent facet, String facetName, boolean hidden) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_" + facetName, null);
        if (hidden) {
            writer.writeAttribute("style", "display:none", null);
        }

        renderChild(context, facet);

        writer.endElement("div");
    }
}
