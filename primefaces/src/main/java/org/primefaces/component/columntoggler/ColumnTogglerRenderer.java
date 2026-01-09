/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.component.columntoggler;

import org.primefaces.component.api.UITable;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = ColumnToggler.DEFAULT_RENDERER, componentFamily = ColumnToggler.COMPONENT_FAMILY)
public class ColumnTogglerRenderer extends CoreRenderer<ColumnToggler> {

    @Override
    public void decode(FacesContext context, ColumnToggler component) {
        UIComponent dataSource = component.getDataSourceComponent();

        if (dataSource instanceof UITable) {
            UITable<?> table = (UITable<?>) dataSource;
            table.decodeColumnTogglerState(context);
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, ColumnToggler component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, ColumnToggler component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ColumnToggler component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("ColumnToggler", component);
        wb.attr("trigger", SearchExpressionUtils.resolveOptionalClientIdsForClientSide(context, component, component.getTrigger()));
        wb.attr("datasource", SearchExpressionUtils.resolveOptionalClientIdsForClientSide(context, component, component.getDatasource()));
        wb.attr("showSelectAll", component.isShowSelectAll(), true);

        encodeClientBehaviors(context, component);

        wb.finish();
    }
}
