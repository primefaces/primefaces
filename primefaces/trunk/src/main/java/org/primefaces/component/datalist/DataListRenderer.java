/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.component.datalist;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class DataListRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext facesContext, UIComponent component) {
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        DataList list = (DataList) component;
        String clientId = list.getClientId();
        boolean isAjaxPaging = params.containsKey(clientId + "_ajaxPaging");

        if (isAjaxPaging) {
            list.setFirst(Integer.valueOf(params.get(clientId + "_first")));
            list.setRows(Integer.valueOf(params.get(clientId + "_rows")));
            list.setPage(Integer.valueOf(params.get(clientId + "_page")));
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        DataList list = (DataList) component;
        String clientId = list.getClientId();
        boolean isAjaxPaging = params.containsKey(clientId + "_ajaxPaging");

        if (isAjaxPaging) {
            encodeList(facesContext, list);
        } else {
            encodeMarkup(facesContext, list);
            encodeScript(facesContext, list);
        }
    }

    protected void encodeMarkup(FacesContext facesContext, DataList list) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = list.getClientId();
        boolean hasPaginator = list.isPaginator();
        String paginatorPosition = list.getPaginatorPosition();
        String styleClass = list.getStyleClass() == null ? DataList.DATALIST_CLASS : DataList.DATALIST_CLASS + " " + list.getStyleClass();

        writer.startElement("div", list);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorContainer(facesContext, clientId + "_paginatorTop");
        }

        writer.startElement("div", list);
        writer.writeAttribute("id", clientId + "_content", "id");
        writer.writeAttribute("class", DataList.CONTENT_CLASS, "styleClass");

        encodeList(facesContext, list);

        writer.endElement("div");

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorContainer(facesContext, clientId + "_paginatorBottom");
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext facesContext, DataList list) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = list.getClientId();
        
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(functio() { ");

        writer.write(list.resolveWidgetVar() + " = new PrimeFaces.widget.DataList('" + clientId + "',{");

        if(list.isPaginator()) {
            writer.write("paginator:new YAHOO.widget.Paginator({");
            writer.write("rowsPerPage:" + list.getRows());
            writer.write(",totalRecords:" + list.getRowCount());
            writer.write(",initialPage:" + list.getPage());

            if(list.getPageLinks() != 10) writer.write(",pageLinks:" + list.getPageLinks());
            if(list.getPaginatorTemplate() != null) writer.write(",template:'" + list.getPaginatorTemplate() + "'");
            if(list.getRowsPerPageTemplate() != null) writer.write(",rowsPerPageOptions : [" + list.getRowsPerPageTemplate() + "]");
            if(list.getCurrentPageReportTemplate() != null) writer.write(",pageReportTemplate:'" + list.getCurrentPageReportTemplate() + "'");
            if (!list.isPaginatorAlwaysVisible()) writer.write(",alwaysVisible:false");

            String paginatorPosition = list.getPaginatorPosition();
            String paginatorContainer = null;
            if(paginatorPosition.equals("both"))
                paginatorContainer = clientId + "_paginatorTop','" + clientId + "_paginatorBottom";
            else if (paginatorPosition.equals("top"))
                paginatorContainer = clientId + "_paginatorTop";
            else if (paginatorPosition.equals("bottom"))
                paginatorContainer = clientId + "_paginatorBottom";

            writer.write(",containers:['" + paginatorContainer + "']");

            writer.write("})");

            if(list.isEffect()) {
                writer.write(",effect:true");
                writer.write(",effectSpeed:'" + list.getEffectSpeed() + "'");
            }
        } else {
            writer.write("paginator:false");
        }

        writer.write("});});");

        writer.endElement("script");
    }

    protected void encodeList(FacesContext facesContext, DataList list) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = list.getClientId();
        boolean isDefinition = list.isDefinition();
        UIComponent definition = list.getFacet("description");
        String listTag = list.getListTag();
        String listItemTag = isDefinition ? "dt" : "li";

        int first = list.getFirst();
        int rows = list.getRows() == 0 ? list.getRowCount() : list.getRows();

        writer.startElement(listTag, null);
        writer.writeAttribute("id", clientId + "_list", null);
        writer.writeAttribute("class", DataList.LIST_CLASS, null);
        if (list.getItemType() != null) {
            writer.writeAttribute("type", list.getItemType(), null);
        }

        for (int i = first; i < (first + rows); i++) {
            list.setRowIndex(i);

            if (list.isRowAvailable()) {
                writer.startElement(listItemTag, null);
                writer.writeAttribute("class", DataList.LIST_ITEM_CLASS, null);
                renderChildren(facesContext, list);
                writer.endElement(listItemTag);

                if (isDefinition) {
                    writer.startElement("dd", null);
                    definition.encodeAll(facesContext);
                    writer.endElement("dd");
                }
            }
        }

        list.setRowIndex(-1);	//cleanup

        writer.endElement(listTag);
    }

    protected void encodePaginatorContainer(FacesContext facesContext, String id) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", id, "id");
        writer.writeAttribute("class", "ui-paginator ui-widget-header ui-corner-all", null);
        writer.endElement("div");
    }
    
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do Nothing
    }

    public boolean getRendersChildren() {
        return true;
    }
}
