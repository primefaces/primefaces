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
package org.primefaces.component.datascroller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.PrimeFaces;

import org.primefaces.model.LazyDataModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class DataScrollerRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DataScroller ds = (DataScroller) component;
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = ds.getClientId(context);
        int chunkSize = ds.getChunkSize();

        if (ds.isLoadRequest()) {
            int offset = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get(clientId + "_offset"));

            loadChunk(context, ds, offset, ds.getChunkSize());
        }
        else if (ds.isVirtualScrollingRequest(context)) {
            int offset = Integer.parseInt(params.get(clientId + "_first"));
            int rowCount = ds.getRowCount();
            int virtualScrollRows = (chunkSize * 2);
            int scrollRows = (offset + virtualScrollRows) > rowCount ? (rowCount - offset) : virtualScrollRows;

            loadChunk(context, ds, offset, scrollRows);
        }
        else {
            if (chunkSize == 0) {
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
        boolean isLazy = ds.isLazy();
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
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (header != null && header.isRendered()) {
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
        if (inline) {
            writer.writeAttribute("style", "height:" + ds.getScrollHeight() + "px", null);
        }

        if (inline && ds.isVirtualScroll()) {
            int rowCount = ds.getRowCount();
            int virtualScrollRowCount = (chunkSize * 2);
            int rowCountToRender = (isLazy && rowCount == 0) ? virtualScrollRowCount : ((virtualScrollRowCount > rowCount) ? rowCount : virtualScrollRowCount);
            int start = 0;

            if (ds.isStartAtBottom()) {
                int totalPage = (int) Math.ceil(rowCount * 1d / chunkSize);
                start = Math.max((totalPage - 2) * chunkSize, 0);
            }

            encodeVirtualScrollList(context, ds, start, rowCountToRender);
        }
        else {
            encodeList(context, ds, 0, chunkSize);

            writer.startElement("div", null);
            writer.writeAttribute("class", DataScroller.LOADER_CLASS, null);
            if (loader != null && loader.isRendered()) {
                loader.encodeAll(context);
            }
            writer.endElement("div");
        }

        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, DataScroller ds, int start, int chunkSize) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("ul", ds);
        writer.writeAttribute("class", DataScroller.LIST_CLASS, null);
        loadChunk(context, ds, start, chunkSize);
        ds.setRowIndex(-1);
        writer.endElement("ul");
    }

    protected void encodeVirtualScrollList(FacesContext context, DataScroller ds, int start, int chunkSize) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", DataScroller.VIRTUALSCROLL_WRAPPER_CLASS, null);

        encodeList(context, ds, start, chunkSize);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, DataScroller ds, int chunkSize) throws IOException {
        String clientId = ds.getClientId(context);
        String loadEvent = ds.getFacet("loader") == null ? "scroll" : "manual";

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataScroller", ds.resolveWidgetVar(context), clientId)
                .attr("chunkSize", chunkSize)
                .attr("totalSize", ds.getRowCount())
                .attr("loadEvent", loadEvent)
                .attr("mode", ds.getMode(), "document")
                .attr("buffer", ds.getBuffer())
                .attr("virtualScroll", ds.isVirtualScroll())
                .attr("startAtBottom", ds.isStartAtBottom())
                .finish();
    }

    protected void loadChunk(FacesContext context, DataScroller ds, int start, int size) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean isLazy = ds.isLazy();
        boolean isVirtualScroll = ds.isVirtualScroll();

        if (isLazy) {
            loadLazyData(context, ds, start, size);
        }

        String rowIndexVar = ds.getRowIndexVar();
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

        int firstIndex = (isLazy && isVirtualScroll) ? 0 : start;
        int lastIndex = (firstIndex + size);

        for (int i = firstIndex; i < lastIndex; i++) {
            ds.setRowIndex(i);

            if (rowIndexVar != null) {
                requestMap.put(rowIndexVar, (isLazy ? (start + i) : i));
            }

            if (!ds.isRowAvailable()) {
                break;
            }

            writer.startElement("li", null);
            writer.writeAttribute("class", DataScroller.ITEM_CLASS, null);
            renderChildren(context, ds);
            writer.endElement("li");
        }
        ds.setRowIndex(-1);

        if (rowIndexVar != null) {
            requestMap.remove(rowIndexVar);
        }
    }

    protected void loadLazyData(FacesContext context, DataScroller ds, int start, int size) {
        LazyDataModel lazyModel = (LazyDataModel) ds.getValue();

        if (lazyModel != null) {
            List<?> data = lazyModel.load(start, size, null, null, null);
            lazyModel.setPageSize(size);
            lazyModel.setWrappedData(data);

            //Update virtualscoller for callback
            if (ComponentUtils.isRequestSource(ds, context) && ds.isVirtualScroll()) {
                PrimeFaces.current().ajax().addCallbackParam("totalSize", lazyModel.getRowCount());
            }
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
