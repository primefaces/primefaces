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
package org.primefaces.component.outputpanel;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class OutputPanelRenderer extends CoreRenderer {

    private static final String BLOCK = "div";
    private static final String INLINE = "span";

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        OutputPanel panel = (OutputPanel) component;

        if (panel.isContentLoadRequest(context)) {
            renderChildren(context, panel);
        }
        else {
            encodeMarkup(context, panel);
            if (panel.isDeferred()) {
                encodeScript(context, panel);
            }
        }
    }

    public void encodeMarkup(FacesContext context, OutputPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String tag = panel.getLayout().equals("block") ? BLOCK : INLINE;
        String clientId = panel.getClientId(context);
        String style = panel.getStyle();
        String styleClass = panel.getStyleClass();
        styleClass = (styleClass == null) ? OutputPanel.CONTAINER_CLASS : OutputPanel.CONTAINER_CLASS + " " + styleClass;

        writer.startElement(tag, panel);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", panel.getStyle(), "style");
        }

        if (panel.isDeferred()) {
            renderLoading(context, panel);
        }
        else {
            renderChildren(context, panel);
        }

        writer.endElement(tag);
    }

    protected void encodeScript(FacesContext context, OutputPanel panel) throws IOException {
        String clientId = panel.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("OutputPanel", panel.resolveWidgetVar(), clientId);

        if (panel.isDeferred()) {
            wb.attr("deferred", true)
                    .attr("deferredMode", panel.getDeferredMode());
        }

        encodeClientBehaviors(context, panel);

        wb.finish();
    }

    protected void renderLoading(FacesContext context, OutputPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", OutputPanel.LOADING_CLASS, null);
        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
