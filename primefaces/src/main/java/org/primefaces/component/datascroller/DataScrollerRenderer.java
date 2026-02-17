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
package org.primefaces.component.datascroller;

import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = DataScroller.DEFAULT_RENDERER, componentFamily = DataScroller.COMPONENT_FAMILY)
public class DataScrollerRenderer extends CoreRenderer<DataScroller> {

    @Override
    public void encodeEnd(FacesContext context, DataScroller component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = component.getClientId(context);
        int chunkSize = component.getChunkSize();

        if (component.isLoadRequest()) {
            int offset = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get(clientId + "_offset"));

            loadChunk(context, component, offset, component.getChunkSize());
        }
        else if (component.isVirtualScrollingRequest(context)) {
            int offset = Integer.parseInt(params.get(clientId + "_first"));
            int rowCount = component.getRowCount();
            int virtualScrollRows = (chunkSize * 2);
            int scrollRows = (offset + virtualScrollRows) > rowCount ? (rowCount - offset) : virtualScrollRows;

            loadChunk(context, component, offset, scrollRows);
        }
        else {
            if (chunkSize == 0) {
                chunkSize = component.getRowCount();
            }

            encodeMarkup(context, component, chunkSize);
            encodeScript(context, component, chunkSize);
        }
    }

    protected void encodeMarkup(FacesContext context, DataScroller component, int chunkSize) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        boolean inline = component.getMode().equals("inline");
        boolean isLazy = component.isLazy();
        UIComponent header = component.getHeaderFacet();
        UIComponent loader = component.getLoaderFacet();
        UIComponent loading = component.getLoadingFacet();
        String containerClass = inline ? DataScroller.INLINE_CONTAINER_CLASS : DataScroller.CONTAINER_CLASS;

        String style = component.getStyle();
        String userStyleClass = component.getStyleClass();
        String styleClass = (userStyleClass == null) ? containerClass : containerClass + " " + userStyleClass;

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (FacetUtils.shouldRenderFacet(header)) {
            writer.startElement("div", component);
            writer.writeAttribute("class", DataScroller.HEADER_CLASS, null);
            header.encodeAll(context);
            writer.endElement("div");

        }

        writer.startElement("div", component);
        writer.writeAttribute("class", DataScroller.CONTENT_CLASS, null);
        if (inline) {
            writer.writeAttribute("style", "height:" + component.getScrollHeight() + "px", null);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", DataScroller.LOADING_CLASS, null);
        if (FacetUtils.shouldRenderFacet(loading)) {
            loading.encodeAll(context);
        }
        else {
            writer.startElement("div", null);
            writer.writeAttribute("class", DataScroller.LOADING_CLASS + "-default", null);
            writer.endElement("div");
        }
        writer.endElement("div");

        int rowCount = component.getRowCount();
        int start = 0;

        if (inline && component.isVirtualScroll()) {
            int virtualScrollRowCount = (chunkSize * 2);
            int rowCountToRender = (isLazy && rowCount == 0)
                    ? virtualScrollRowCount
                    : Math.min(virtualScrollRowCount, rowCount);

            if (component.isStartAtBottom()) {
                int totalPage = (int) Math.ceil(rowCount * 1d / chunkSize);
                start = Math.max((totalPage - 2) * chunkSize, 0);
            }

            encodeVirtualScrollList(context, component, start, rowCountToRender);
        }
        else {
            if (component.isStartAtBottom()) {
                start = rowCount > chunkSize ? rowCount - chunkSize : 0;
            }

            encodeList(context, component, start, chunkSize);

            if (component.isLazy()) {
                rowCount = component.getRowCount();
            }

            writer.startElement("div", null);
            writer.writeAttribute("class", DataScroller.LOADER_CLASS, null);
            if (rowCount > chunkSize && FacetUtils.shouldRenderFacet(loader)) {
                loader.encodeAll(context);
            }
            writer.endElement("div");
        }

        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, DataScroller component, int start, int chunkSize) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("ul", component);
        writer.writeAttribute("class", DataScroller.LIST_CLASS, null);
        loadChunk(context, component, start, chunkSize);
        component.setRowIndex(-1);
        writer.endElement("ul");
    }

    protected void encodeVirtualScrollList(FacesContext context, DataScroller component, int start, int chunkSize) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", DataScroller.VIRTUALSCROLL_WRAPPER_CLASS, null);

        encodeList(context, component, start, chunkSize);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, DataScroller component, int chunkSize) throws IOException {
        String loadEvent = component.getLoaderFacet() == null ? "scroll" : "manual";

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataScroller", component)
                .attr("chunkSize", chunkSize)
                .attr("totalSize", getTotalSize(component))
                .attr("loadEvent", loadEvent)
                .attr("mode", component.getMode(), "document")
                .attr("buffer", component.getBuffer())
                .attr("virtualScroll", component.isVirtualScroll())
                .attr("startAtBottom", component.isStartAtBottom());

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected int getTotalSize(DataScroller component) {
        if (component.isLazy()) {
            LazyDataModel<?> lazyModel = (LazyDataModel<?>) component.getValue();
            if (lazyModel != null) {
                return lazyModel.count(Collections.emptyMap());
            }
        }
        return component.getRowCount();
    }

    protected void loadChunk(FacesContext context, DataScroller component, int start, int size) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean isLazy = component.isLazy();
        int _start = start < 0 ? 0 : start;

        if (isLazy) {
            loadLazyData(context, component, _start, size);
        }

        String rowIndexVar = component.getRowIndexVar();
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

        int lastIndex = (_start + size);

        lastIndex = start < 0 ? lastIndex + start : lastIndex;

        for (int i = _start; i < lastIndex; i++) {
            component.setRowIndex(i);

            if (rowIndexVar != null) {
                requestMap.put(rowIndexVar, i);
            }

            if (!component.isRowAvailable()) {
                break;
            }

            writer.startElement("li", null);
            writer.writeAttribute("class", DataScroller.ITEM_CLASS, null);
            renderChildren(context, component);
            writer.endElement("li");
        }
        component.setRowIndex(-1);

        if (rowIndexVar != null) {
            requestMap.remove(rowIndexVar);
        }
    }

    protected void loadLazyData(FacesContext context, DataScroller component, int start, int size) {
        LazyDataModel<?> lazyModel = (LazyDataModel<?>) component.getValue();

        if (lazyModel != null) {
            List<?> data = lazyModel.load(start, size, Collections.emptyMap(), Collections.emptyMap());
            lazyModel.setPageSize(size);
            lazyModel.setWrappedData(data);

            //Update virtualscoller for callback
            if (ComponentUtils.isRequestSource(component, context) && component.isVirtualScroll()) {
                PrimeFaces.current().ajax().addCallbackParam("totalSize", lazyModel.getRowCount());
            }
        }
    }

    @Override
    public void encodeChildren(FacesContext context, DataScroller component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
