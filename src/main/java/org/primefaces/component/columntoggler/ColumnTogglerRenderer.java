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
package org.primefaces.component.columntoggler;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.TableState;
import org.primefaces.expression.SearchExpressionFacade;
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

        wb.init("ColumnToggler", columnToggler.resolveWidgetVar(), columnToggler.getClientId(context));
        wb.attr("trigger", SearchExpressionFacade.resolveClientIds(context, columnToggler, columnToggler.getTrigger()))
                .attr("datasource", SearchExpressionFacade.resolveClientIds(context, columnToggler, columnToggler.getDatasource()));

        encodeClientBehaviors(context, columnToggler);

        wb.finish();
    }
}
