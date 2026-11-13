/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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

import org.primefaces.cdk.api.FacesRendererInfo;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.model.dashboard.DashboardColumn;
import org.primefaces.model.dashboard.DashboardItem;
import org.primefaces.model.dashboard.DashboardModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentScriptBuilder;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.GridLayoutUtils;

import java.io.IOException;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Dashboard.DEFAULT_RENDERER, componentFamily = Dashboard.COMPONENT_FAMILY)
@FacesRendererInfo
public class DashboardRenderer extends CoreRenderer<Dashboard> {

    @Override
    public void decode(FacesContext context, Dashboard component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Dashboard component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, Dashboard component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        boolean responsive = component.isResponsive();
        String var = component.getVar();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        String styleClass = getStyleClassBuilder(context)
                .add(Dashboard.CONTAINER_CLASS)
                .add(component.getStyleClass())
                .add(component.isDisabled(), "p-state-disabled")
                .add(responsive, GridLayoutUtils.getFlexGridClass(true))
                .build();
        writer.writeAttribute("class", styleClass, "styleClass");
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        DashboardModel model = component.getModel();
        if (model != null) {
            for (DashboardColumn column : model.getColumns()) {
                if (responsive) {
                    // the items of all columns are the cells of the grid
                    for (DashboardItem item : column.getItems()) {
                        encodeContainer(context, Dashboard.PANEL_CLASS, item.getStyle(), item.getStyleClass());
                        encodeItem(context, component, var, item);
                        writer.endElement("div");
                    }
                }
                else {
                    encodeContainer(context, Dashboard.COLUMN_CLASS, column.getStyle(), column.getStyleClass());
                    for (DashboardItem item : column.getItems()) {
                        encodeItem(context, component, var, item);
                    }
                    writer.endElement("div");
                }
            }
        }

        writer.endElement("div");
    }

    protected void encodeContainer(FacesContext context, String baseClass, String style, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", null);
        writer.writeAttribute("class", getStyleClassBuilder(context).add(baseClass).add(styleClass).build(), null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
    }

    protected void encodeItem(FacesContext context, Dashboard component, String var, DashboardItem item) {
        UIComponent panel = SearchExpressionUtils.contextlessResolveComponent(context, component, item.getPanelId());
        if (panel != null) {
            ComponentUtils.executeInRequestScope(context, var, item.getValue(), () -> {
                try {
                    return renderChild(context, panel);
                }
                catch (IOException e) {
                    throw new FacesException(e);
                }
            });
        }
    }

    protected void encodeScript(FacesContext context, Dashboard component) throws IOException {
        ComponentScriptBuilder wb = getComponentScriptBuilder(context);
        wb.init("Dashboard", component)
                .attr("responsive", component.isResponsive(), false)
                .attr("disabled", !component.isReordering(), false)
                .attr("scope", component.getScope());

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, Dashboard component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
