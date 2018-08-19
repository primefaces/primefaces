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
package org.primefaces.component.sidebar;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class SidebarRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Sidebar bar = (Sidebar) component;

        encodeMarkup(context, bar);
        encodeScript(context, bar);
    }

    protected void encodeMarkup(FacesContext context, Sidebar bar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = bar.getStyle();
        String styleClass = bar.getStyleClass();
        styleClass = styleClass == null ? Sidebar.STYLE_CLASS : Sidebar.STYLE_CLASS + " " + styleClass;
        styleClass = bar.isFullScreen() ? styleClass + " " + Sidebar.FULL_BAR_CLASS : styleClass;
        styleClass += " ui-sidebar-" + bar.getPosition();

        writer.startElement("div", bar);
        writer.writeAttribute("id", bar.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeCloseIcon(context);

        renderChildren(context, bar);

        writer.endElement("div");
    }

    protected void encodeCloseIcon(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", Sidebar.TITLE_BAR_CLOSE_CLASS, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", Sidebar.CLOSE_ICON_CLASS, null);
        writer.endElement("span");

        writer.endElement("a");
    }

    private void encodeScript(FacesContext context, Sidebar bar) throws IOException {
        String clientId = bar.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Sidebar", bar.resolveWidgetVar(), clientId)
                .attr("visible", bar.isVisible(), false)
                .attr("blockScroll", bar.isBlockScroll(), false)
                .attr("baseZIndex", bar.getBaseZIndex(), 0)
                .attr("appendTo", SearchExpressionFacade.resolveClientId(context, bar, bar.getAppendTo()), null)
                .callback("onHide", "function()", bar.getOnHide())
                .callback("onShow", "function()", bar.getOnShow());

        wb.finish();
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
