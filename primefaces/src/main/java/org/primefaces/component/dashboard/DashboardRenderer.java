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
package org.primefaces.component.dashboard;

import org.primefaces.component.panel.Panel;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.model.dashboard.DashboardModel;
import org.primefaces.model.dashboard.DashboardWidget;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.GridLayoutUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

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

    protected void encodeMarkup(FacesContext context, Dashboard dashboard) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dashboard.getClientId(context);
        boolean responsive = dashboard.isResponsive();
        String var = dashboard.getVar();

        writer.startElement("div", dashboard);
        writer.writeAttribute("id", clientId, "id");
        String styleClass = getStyleClassBuilder(context)
                .add(Dashboard.CONTAINER_CLASS)
                .add(dashboard.getStyleClass())
                .add(dashboard.isDisabled(), "ui-state-disabled")
                .add(responsive, GridLayoutUtils.getFlexGridClass(true))
                .build();
        writer.writeAttribute("class", styleClass, "styleClass");
        if (dashboard.getStyle() != null) {
            writer.writeAttribute("style", dashboard.getStyle(), "style");
        }

        DashboardModel model = dashboard.getModel();
        if (model != null) {
            List<DashboardWidget> widgets = model.getWidgets();
            for (int i = 0; i < widgets.size(); i++) {
                DashboardWidget column = widgets.get(i);
                String columnStyle = column.getStyle();
                String columnStyleClass = getStyleClassBuilder(context)
                        .add(!responsive, Dashboard.COLUMN_CLASS)
                        .add(responsive, Dashboard.PANEL_CLASS)
                        .add(column.getStyleClass())
                        .build();

                writer.startElement("div", null);
                writer.writeAttribute("class", columnStyleClass, null);
                if (columnStyle != null) {
                    writer.writeAttribute("style", columnStyle, null);
                }

                for (String widgetId : column.getWidgets()) {
                    Panel widget = (Panel) SearchExpressionUtils.contextlessResolveComponent(context, dashboard, widgetId);
                    if (widget != null) {
                        ComponentUtils.executeInRequestScope(context, var, column.getValue(), () -> {
                            try {
                                return renderChild(context, widget);
                            }
                            catch (IOException e) {
                                throw new FacesException(e);
                            }
                        });
                    }
                }

                writer.endElement("div");
            }
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Dashboard dashboard) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Dashboard", dashboard)
                .attr("responsive", dashboard.isResponsive(), false)
                .attr("disabled", !dashboard.isReordering(), false)
                .attr("scope", dashboard.getScope());

        encodeClientBehaviors(context, dashboard);

        wb.finish();
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
