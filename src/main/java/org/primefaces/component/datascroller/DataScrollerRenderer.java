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
package org.primefaces.component.datascroller;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class DataScrollerRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DataScroller ds = (DataScroller) component;

        if(ds.isLoadRequest()) {
            String clientId = ds.getClientId(context);
            int offset = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get(clientId + "_offset"));
            
            loadChunk(context, ds, offset, ds.getChunkSize());
        }
        else {
            int chunkSize = ds.getChunkSize();
            if(chunkSize == 0) {
                chunkSize = ds.getRowCount();
            }
        
            encodeMarkup(context, ds, chunkSize);
            encodeScript(context, ds, chunkSize);
        }
    }
    
    protected void encodeMarkup(FacesContext context, DataScroller ds, int chunkSize) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ds.getClientId(context);
        boolean inline = ds.getMode().equals("inline");
        UIComponent header = ds.getFacet("header");
        UIComponent loader = ds.getFacet("loader");
        String contentCornerClass = null;
        String containerClass = inline ? DataScroller.INLINE_CONTAINER_CLASS : DataScroller.CONTAINER_CLASS;
        
        String style = ds.getStyle();
        String userStyleClass = ds.getStyleClass();
        String styleClass = (userStyleClass == null) ? containerClass : containerClass + " " + userStyleClass;
        
        writer.startElement("div", ds);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", styleClass, null);
        }
        
        if(header != null && header.isRendered()) {
            writer.startElement("div", ds);
            writer.writeAttribute("class", DataScroller.HEADER_CLASS, null);
            header.encodeAll(context);
            writer.endElement("div");
            
            contentCornerClass = "ui-corner-bottom";
        }
        else {
            contentCornerClass = "ui-corner-all";
        }
        
        writer.startElement("div", ds);
        writer.writeAttribute("class", DataScroller.CONTENT_CLASS + " " + contentCornerClass, null);
        if(inline) {
            writer.writeAttribute("style", "height:" + ds.getScrollHeight() + "px", null);
        }
        
        writer.startElement("ul", ds);
        writer.writeAttribute("class", DataScroller.LIST_CLASS, null);
        loadChunk(context, ds, 0, chunkSize);
        ds.setRowIndex(-1);
        writer.endElement("ul");
        
        writer.startElement("div", null);
        writer.writeAttribute("class", DataScroller.LOADER_CLASS, null);
        if(loader != null && loader.isRendered()) {
            loader.encodeAll(context);
        }     
        writer.endElement("div");
        
        writer.endElement("div");
        
        writer.endElement("div");
    }
    
    protected void encodeScript(FacesContext context, DataScroller ds, int chunkSize) throws IOException {
        String clientId = ds.getClientId(context);
        String loadEvent = ds.getFacet("loader") == null ? "scroll" : "manual";
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataScroller", ds.resolveWidgetVar(), clientId)
            .attr("chunkSize", chunkSize)
            .attr("totalSize", ds.getRowCount())
            .attr("loadEvent", loadEvent)
            .attr("mode", ds.getMode(), "document")
            .attr("buffer", ds.getBuffer())
            .finish();
    }
    
    protected void loadChunk(FacesContext context, DataScroller ds, int start, int size) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        if(ds.isLazy()) {
            loadLazyData(ds, start, size);
        }
        
        for(int i = start; i < (start + size); i++) {
            ds.setRowIndex(i);
            if(!ds.isRowAvailable()) {
                break;
            }
            
            writer.startElement("li", null);
            writer.writeAttribute("class", DataScroller.ITEM_CLASS, null);
            renderChildren(context, ds);
            writer.endElement("li");
        }
        ds.setRowIndex(-1);
    }
    
    protected void loadLazyData(DataScroller ds, int start, int size) {
        LazyDataModel lazyModel = (LazyDataModel) ds.getValue();
        
        if(lazyModel != null) {      
            List<?> data = lazyModel.load(start, size, null, null, null);
            lazyModel.setPageSize(size);
            lazyModel.setWrappedData(data);
        }
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
