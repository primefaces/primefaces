/*
 * Copyright 2009-2013 PrimeTek.
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
import org.primefaces.util.WidgetBuilder;

public class DataListRenderer extends org.primefaces.component.datalist.DataListRenderer {
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);        
    }
    
    @Override
    protected void encodeScript(FacesContext context, DataList list) throws IOException {
        String clientId = list.getClientId();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("DataList", list.resolveWidgetVar(), clientId);
        
        encodeClientBehaviors(context, list);

        wb.finish();
    }
    
    @Override
    public void encodeMarkup(FacesContext context, DataList list) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = list.getClientId(context);
        String style = list.getStyle();
        String styleClass = list.getStyleClass();
        int first = list.getFirst();
        int rows = list.getRows() == 0 ? list.getRowCount() : list.getRows();
        int pageSize = first + rows;
        int rowCount = list.getRowCount();
        String varStatus = list.getVarStatus();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        String listTag = list.getListTag();
        
        writer.startElement(listTag, list);
        writer.writeAttribute("id", clientId, "id");
        if (style != null) writer.writeAttribute("style", style, "style");
        if (styleClass != null)  writer.writeAttribute("class", styleClass, "styleClass");
        if(list.getItemType() != null) writer.writeAttribute("type", list.getItemType(), null);
        
        encodeFacet(context, list, "header");
        
        for (int i = first; i < rowCount; i++) {
            if(varStatus != null) {
                requestMap.put(varStatus, new VarStatus(first, (pageSize - 1), (i == 0), (i == (rowCount - 1)), i, (i % 2 == 0), (i % 2 == 1), 1));
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
        
        encodeFacet(context, list, "footer");
        
        writer.endElement(listTag);
    }
    
    public void encodeFacet(FacesContext context, UIData data, String facet) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent component = data.getFacet(facet);
        
        if(component != null && component.isRendered()) {
            writer.startElement("li", null);
            writer.writeAttribute("data-role", "list-divider", null);
            writer.writeAttribute("role", "heading", null);
            component.encodeAll(context);
            writer.endElement("li");
        }
    }
}
