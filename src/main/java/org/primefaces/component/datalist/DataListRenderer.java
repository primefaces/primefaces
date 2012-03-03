/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import org.primefaces.renderkit.DataRenderer;

public class DataListRenderer extends DataRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        DataList list = (DataList) component;
        String clientId = list.getClientId();

        if(list.isPagingRequest(context)) {
            list.setFirst(Integer.valueOf(params.get(clientId + "_first")));
            list.setRows(Integer.valueOf(params.get(clientId + "_rows")));
            
            if(list.isLazy()) {
                list.loadLazyData();
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        DataList list = (DataList) component;
        String clientId = list.getClientId();
        boolean isAjaxPaging = params.containsKey(clientId + "_ajaxPaging");

        if(isAjaxPaging) {
            if(list.getType().equals("none"))
                encodeFreeList(context, list);
            else
                encodeStrictList(context, list); 
        } 
        else {
            encodeMarkup(context, list);
            encodeScript(context, list);
        }
    }

    protected void encodeMarkup(FacesContext context, DataList list) throws IOException {
        if(list.isLazy()) {
            list.loadLazyData();
        }
        
        ResponseWriter writer = context.getResponseWriter();
        String clientId = list.getClientId();
        boolean hasPaginator = list.isPaginator();
        String paginatorPosition = list.getPaginatorPosition();
        String styleClass = list.getStyleClass() == null ? DataList.DATALIST_CLASS : DataList.DATALIST_CLASS + " " + list.getStyleClass();
        UIComponent header = list.getHeader();
        
        if(hasPaginator) {
            list.calculatePage();
        }
        
        writer.startElement("div", list);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        
        if(header != null) {
            writer.startElement("div", list);
            writer.writeAttribute("class", DataList.HEADER_CLASS, "styleClass");
            header.encodeAll(context);
            writer.endElement("div");
        }

        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorMarkup(context, list, "top");
        }

        writer.startElement("div", list);
        writer.writeAttribute("id", clientId + "_content", "id");
        writer.writeAttribute("class", DataList.CONTENT_CLASS, "styleClass");

        if(list.getType().equals("none"))
            encodeFreeList(context, list);
        else
            encodeStrictList(context, list); 

        writer.endElement("div");

        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorMarkup(context, list, "bottom");
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, DataList list) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = list.getClientId();
        
        startScript(writer, clientId);

        writer.write("$(function() { ");

        writer.write("PrimeFaces.cw('DataList','" + list.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        
        //Pagination
        if(list.isPaginator()) {
            encodePaginatorConfig(context, list);
        }
        
        writer.write("});});");

        endScript(writer);
    }

    /**
     * Renders items with no strict markup
     * 
     * @param context FacesContext instance
     * @param list DataList component
     * @throws IOException 
     */
    protected void encodeStrictList(FacesContext context, DataList list) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = list.getClientId();
        boolean isDefinition = list.isDefinition();
        UIComponent definition = list.getFacet("description");
        String listTag = list.getListTag();
        String listItemTag = isDefinition ? "dt" : "li";

        int first = list.getFirst();
        int rows = list.getRows() == 0 ? list.getRowCount() : list.getRows();

        String rowIndexVar = list.getRowIndexVar();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();

        writer.startElement(listTag, null);
        writer.writeAttribute("id", clientId + "_list", null);
        writer.writeAttribute("class", DataList.LIST_CLASS, null);
        if(list.getItemType() != null) {
            writer.writeAttribute("type", list.getItemType(), null);
        }

        for(int i = first; i < (first + rows); i++) {
            list.setRowIndex(i);

            if(rowIndexVar != null) {
                requestMap.put(rowIndexVar, i);
            }

            if(list.isRowAvailable()) {
                writer.startElement(listItemTag, null);
                writer.writeAttribute("class", DataList.LIST_ITEM_CLASS, null);
                renderChildren(context, list);
                writer.endElement(listItemTag);

                if(isDefinition) {
                    writer.startElement("dd", null);
                    definition.encodeAll(context);
                    writer.endElement("dd");
                }
            }
        }

        list.setRowIndex(-1);	//cleanup

        if(rowIndexVar != null) {
            requestMap.remove(rowIndexVar);
        }

        writer.endElement(listTag);
    }
    
    /**
     * Renders items with no strict markup
     * 
     * @param context FacesContext instance
     * @param list DataList component
     * @throws IOException 
     */
    protected void encodeFreeList(FacesContext context, DataList list) throws IOException {
        int first = list.getFirst();
        int rows = list.getRows() == 0 ? list.getRowCount() : list.getRows();

        String rowIndexVar = list.getRowIndexVar();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();

        for(int i = first; i < (first + rows); i++) {
            list.setRowIndex(i);

            if(rowIndexVar != null) {
                requestMap.put(rowIndexVar, i);
            }

            if(list.isRowAvailable()) {
                renderChildren(context, list);
            }
        }

        //cleanup
        list.setRowIndex(-1);               

        if(rowIndexVar != null) {
            requestMap.remove(rowIndexVar);
        }
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
