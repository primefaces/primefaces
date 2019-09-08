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
package org.primefaces.component.columntoggler;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.TableState;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ColumnTogglerRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        ColumnToggler columnToggler = (ColumnToggler) component;
        UIComponent datasource = columnToggler.getDataSourceComponent();

        if (datasource instanceof DataTable) {
            DataTable table = ((DataTable) datasource);
            boolean isMultiViewState = table.isMultiViewState();
            if (isMultiViewState) {
                Map<String, String> params = context.getExternalContext().getRequestParameterMap();
                String columnTogglerParam = params.get(table.getClientId(context) + "_columnTogglerState");
                TableState ts = table.getTableState(true);
                ts.setTogglableColumnsAsString(columnTogglerParam);
            }
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ColumnToggler columnToggler = (ColumnToggler) component;

        encodeMarkup(context, columnToggler);
        encodeScript(context, columnToggler);
    }

    protected void encodeMarkup(FacesContext context, ColumnToggler columnToggler) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = columnToggler.getClientId(context);

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ColumnToggler columnToggler) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("ColumnToggler", columnToggler.resolveWidgetVar(context), columnToggler.getClientId(context));
        wb.attr("trigger",
                SearchExpressionFacade.resolveClientIds(context, columnToggler, columnToggler.getTrigger(), SearchExpressionHint.RESOLVE_CLIENT_SIDE));
        wb.attr("datasource",
                SearchExpressionFacade.resolveClientIds(context, columnToggler, columnToggler.getDatasource(), SearchExpressionHint.RESOLVE_CLIENT_SIDE));

        encodeClientBehaviors(context, columnToggler);

        wb.finish();
    }
}
