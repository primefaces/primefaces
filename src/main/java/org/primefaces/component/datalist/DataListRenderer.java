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
package org.primefaces.component.datalist;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.WidgetBuilder;

public class DataListRenderer extends DataRenderer {

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
        String clientId = list.getClientId(context);
        boolean hasPaginator = list.isPaginator();
        boolean empty = (list.getRowCount() == 0);
        String paginatorPosition = list.getPaginatorPosition();
        String styleClass = list.getStyleClass() == null ? DataList.DATALIST_CLASS : DataList.DATALIST_CLASS + " " + list.getStyleClass();
        String style = list.getStyle();
        
        if(hasPaginator) {
            list.calculateFirst();
        }
        
        writer.startElement("div", list);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        encodeFacet(context, list, "header", DataList.HEADER_CLASS);
        
        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorMarkup(context, list, "top");
        }

        writer.startElement("div", list);
        writer.writeAttribute("id", clientId + "_content", "id");
        writer.writeAttribute("class", DataList.CONTENT_CLASS, "styleClass");

        if(empty) {
            writer.startElement("div", list);
            writer.writeAttribute("class", DataList.DATALIST_EMPTYMESSAGE_CLASS,null);
            writer.write(list.getEmptyMessage());
            writer.endElement("div");
        } 
        else {
            if(list.getType().equals("none"))
                encodeFreeList(context, list);
            else
                encodeStrictList(context, list); 
        }

        writer.endElement("div");

        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorMarkup(context, list, "bottom");
        }
        
        encodeFacet(context, list, "footer", DataList.FOOTER_CLASS);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, DataList list) throws IOException {
        String clientId = list.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("DataList", list.resolveWidgetVar(), clientId);
        
        if(list.isPaginator()) {
            encodePaginatorConfig(context, list, wb);
        }
        
        encodeClientBehaviors(context, list);

        wb.finish();
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
        String clientId = list.getClientId(context);
        boolean isDefinition = list.isDefinition();
        UIComponent definitionFacet = list.getFacet("description");
        boolean renderDefinition = isDefinition && definitionFacet != null;
        String itemType = list.getItemType();
        String listClass = DataList.LIST_CLASS;
        if(itemType != null && itemType.equals("none")) {
            listClass = listClass + " " + DataList.NO_BULLETS_CLASS;
        }
        
        String listTag = list.getListTag();
        String listItemTag = isDefinition ? "dt" : "li";
        String varStatus = list.getVarStatus();

        int first = list.getFirst();
        int rows = list.getRows() == 0 ? list.getRowCount() : list.getRows();
        int pageSize = first + rows;
        int rowCount = list.getRowCount();

        String rowIndexVar = list.getRowIndexVar();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();

        writer.startElement(listTag, null);
        writer.writeAttribute("id", clientId + "_list", null);
        writer.writeAttribute("class", listClass, null);
        if(list.getItemType() != null) {
            writer.writeAttribute("type", list.getItemType(), null);
        }

        for(int i = first; i < pageSize; i++) {
            if(varStatus != null) {
                requestMap.put(varStatus, new VarStatus(first, (pageSize - 1), (i == 0), (i == (rowCount - 1)), i, (i % 2 == 0), (i % 2 == 1), 1));
            }
            
            list.setRowIndex(i);

            if(rowIndexVar != null) {
                requestMap.put(rowIndexVar, i);
            }

            if(list.isRowAvailable()) {
                String itemStyleClass = list.getItemStyleClass();
                itemStyleClass = (itemStyleClass == null) ? DataList.LIST_ITEM_CLASS: DataList.LIST_ITEM_CLASS + " " + itemStyleClass;
            
                writer.startElement(listItemTag, null);
                writer.writeAttribute("class", itemStyleClass, null);
                renderChildren(context, list);
                writer.endElement(listItemTag);

                if(renderDefinition) {
                    writer.startElement("dd", null);
                    definitionFacet.encodeAll(context);
                    writer.endElement("dd");
                }
            }
        }

        //cleanup
        list.setRowIndex(-1);	

        if(rowIndexVar != null) {
            requestMap.remove(rowIndexVar);
        }
        
        if(varStatus != null) {
            requestMap.remove(varStatus);
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
        int pageSize = first + rows;
        int rowCount = list.getRowCount();

        String rowIndexVar = list.getRowIndexVar();
        String varStatus = list.getVarStatus();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();

        for(int i = first; i < pageSize; i++) {
            if(varStatus != null) {
                requestMap.put(varStatus, new VarStatus(first, (pageSize - 1), (i == 0), (i == (rowCount - 1)), i, (i % 2 == 0), (i % 2 == 1), 1));
            }
            
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
        
        if(varStatus != null) {
            requestMap.remove(varStatus);
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
    
    public static class VarStatus {
        
        private int begin;
        private int end;
        private boolean first;
        private boolean last;
        private int index;
        private boolean even;
        private boolean odd;
        private int step;
        
        public VarStatus() {
            
        }

        public VarStatus(int begin, int end, boolean first, boolean last, int index, boolean even, boolean odd, int step) {
            this.begin = begin;
            this.end = end;
            this.first = first;
            this.last = last;
            this.index = index;
            this.even = even;
            this.odd = odd;
            this.step = step;
        }

        public int getBegin() {
            return begin;
        }

        public void setBegin(int begin) {
            this.begin = begin;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public boolean isEven() {
            return even;
        }

        public void setEven(boolean even) {
            this.even = even;
        }

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }

        public boolean isOdd() {
            return odd;
        }

        public void setOdd(boolean odd) {
            this.odd = odd;
        }
        
        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }
    }
}
