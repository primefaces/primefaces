/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.UIData;
import org.primefaces.component.datalist.DataList;
import org.primefaces.mobile.renderkit.paginator.PaginatorRenderer;
import org.primefaces.util.WidgetBuilder;

public class DataListRenderer extends org.primefaces.component.datalist.DataListRenderer {
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);        
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DataList list = (DataList) component;

        if(list.isPaginationRequest(context)) {
            list.updatePaginationData(context, list);
            
            if(list.isLazy()) {
                list.loadLazyData();
            }
            
            encodeList(context, list);
        } 
        else {
            encodeMarkup(context, list);
            encodeScript(context, list);
        }
    }
    
    @Override
    protected void encodeScript(FacesContext context, DataList list) throws IOException {
        String clientId = list.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataList", list.resolveWidgetVar(), clientId);
        
        if(list.isPaginator()) {
            PaginatorRenderer paginatorRenderer = getPaginatorRenderer(context);
            paginatorRenderer.encodeScript(context, list, wb);
        }
        
        encodeClientBehaviors(context, list);

        wb.finish();
    }
    
    @Override
    public void encodeMarkup(FacesContext context, DataList list) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = list.getClientId(context);
        String style = list.getStyle();
        String styleClass = list.getStyleClass();
        styleClass = (styleClass == null) ? DataList.DATALIST_CLASS : DataList.DATALIST_CLASS + " " + styleClass;
        boolean hasPaginator = list.isPaginator();
        String paginatorPosition = list.getPaginatorPosition();
        PaginatorRenderer paginatorRenderer = getPaginatorRenderer(context);
        
        if(hasPaginator) {
            list.calculateFirst();
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        encodeFacet(context, list, "header");
                
        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            paginatorRenderer.encodeMarkup(context, list, "top");
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("class", DataList.MOBILE_CONTENT_CLASS, null);
        encodeList(context, list);
        writer.endElement("div");
        
        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            paginatorRenderer.encodeMarkup(context, list, "bottom");
        }
        
        encodeFacet(context, list, "footer");

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, DataList list) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int first = list.getFirst();
        int rows = list.getRows() == 0 ? list.getRowCount() : list.getRows();
        int pageSize = first + rows;
        int rowCount = list.getRowCount();
        String varStatus = list.getVarStatus();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        String listTag = list.getListTag();
        writer.startElement(listTag, null);
        writer.writeAttribute("id", list.getClientId(context) + "_list", "id");

        if(list.getItemType() != null) {
            writer.writeAttribute("type", list.getItemType(), null);
        }
        
        renderDynamicPassThruAttributes(context, list);

        for (int i = first; i < pageSize; i++) {
            if(varStatus != null) {
                requestMap.put(varStatus, new org.primefaces.component.datalist.DataListRenderer.VarStatus(first, (pageSize - 1), (i == 0), (i == (rowCount - 1)), i, (i % 2 == 0), (i % 2 == 1), 1));
            }
            
            list.setRowIndex(i);

            if (list.isRowAvailable()) {
                writer.startElement("li", null);
                renderChildren(context, list);
                writer.endElement("li");
            }
        }
        
        list.setRowIndex(-1);
        
        if(varStatus != null) {
            requestMap.remove(varStatus);
        }
        
        
        
        writer.endElement(listTag);
    }
    
    public void encodeFacet(FacesContext context, UIData data, String facet) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent component = data.getFacet(facet);
        
        if(component != null && component.isRendered()) {
            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-bar ui-bar-b", null);
            writer.writeAttribute("role", "heading", null);
            component.encodeAll(context);
            writer.endElement("div");
        }
    }
    
    private PaginatorRenderer getPaginatorRenderer(FacesContext context) {
        return (PaginatorRenderer) context.getRenderKit().getRenderer("org.primefaces.component", "org.primefaces.component.PaginatorRenderer");
    }
}