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
package org.primefaces.component.dashboard;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.panel.Panel;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class DashboardRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Dashboard dashboard = (Dashboard) component;

        encodeMarkup(context, dashboard);
        encodeScript(context, dashboard);
    }

    protected void encodeMarkup(FacesContext contextr, Dashboard dashboard) throws IOException {
        ResponseWriter writer = contextr.getResponseWriter();
        String clientId = dashboard.getClientId(contextr);

        writer.startElement("div", dashboard);
        writer.writeAttribute("id", clientId, "id");
        String styleClass = dashboard.getStyleClass() != null ? Dashboard.CONTAINER_CLASS + " " + dashboard.getStyleClass() : Dashboard.CONTAINER_CLASS;
        writer.writeAttribute("class", styleClass, "styleClass");
        if (dashboard.getStyle() != null) {
            writer.writeAttribute("style", dashboard.getStyle(), "style");
        }

        DashboardModel model = dashboard.getModel();
        if (model != null) {
            for (DashboardColumn column : model.getColumns()) {
                String columnStyle = column.getStyle();
                String columnStyleClass = column.getStyleClass();
                columnStyleClass = (columnStyleClass == null) ? Dashboard.COLUMN_CLASS : Dashboard.COLUMN_CLASS + " " + columnStyleClass;

                writer.startElement("div", null);
                writer.writeAttribute("class", columnStyleClass, null);
                if (columnStyle != null) {
                    writer.writeAttribute("style", columnStyle, null);
                }

                for (String widgetId : column.getWidgets()) {
                    Panel widget = findWidget(widgetId, dashboard);

                    if (widget != null) {
                        renderChild(contextr, widget);
                    }
                }

                writer.endElement("div");
            }
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Dashboard dashboard) throws IOException {
        String clientId = dashboard.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Dashboard", dashboard.resolveWidgetVar(context), clientId)
                .attr("disabled", dashboard.isDisabled(), false);

        encodeClientBehaviors(context, dashboard);

        wb.finish();
    }

    protected Panel findWidget(String id, Dashboard dashboard) {
        for (UIComponent child : dashboard.getChildren()) {
            Panel panel = (Panel) child;

            if (panel.getId().equals(id)) {
                return panel;
            }
        }

        return null;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
