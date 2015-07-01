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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.datagrid.DataGrid;
import org.primefaces.mobile.renderkit.paginator.PaginatorRenderer;
import org.primefaces.mobile.util.MobileUtils;
import org.primefaces.util.WidgetBuilder;

public class DataGridRenderer extends org.primefaces.component.datagrid.DataGridRenderer {
    
    @Override
    protected void encodeScript(FacesContext context, DataGrid grid) throws IOException{
		String clientId = grid.getClientId(context);        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataGrid", grid.resolveWidgetVar(), clientId);
                        
        if(grid.isPaginator()) {
            PaginatorRenderer paginatorRenderer = getPaginatorRenderer(context);
            paginatorRenderer.encodeScript(context, grid, wb);
        }
        
        encodeClientBehaviors(context, grid);

        wb.finish();
	}
    
    @Override
    protected void encodeMarkup(FacesContext context, DataGrid grid) throws IOException {
        if(grid.isLazy()) {
            grid.loadLazyData();
        }
        
        ResponseWriter writer = context.getResponseWriter();
        String clientId = grid.getClientId(context);
        boolean hasPaginator = grid.isPaginator();
        boolean empty = grid.getRowCount() == 0;
        String paginatorPosition = grid.getPaginatorPosition();
        String style = grid.getStyle();
        String styleClass = grid.getStyleClass() == null ? DataGrid.MOBILE_DATAGRID_CLASS : DataGrid.MOBILE_DATAGRID_CLASS + " " + grid.getStyleClass();
        String contentClass;
        
        PaginatorRenderer paginatorRenderer = getPaginatorRenderer(context);
        
        if(empty) {
            contentClass = DataGrid.MOBILE_EMPTY_CONTENT_CLASS;
        }
        else {
            contentClass = DataGrid.MOBILE_CONTENT_CLASS;
            int columns = grid.getColumns();
            if(columns == 0) {
                columns = 1;
            }
            String gridClass = MobileUtils.GRID_MAP.get(columns);
            contentClass = contentClass + " " + gridClass;
        }

        if(hasPaginator) {
            grid.calculateFirst();
        }
        
        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        renderDynamicPassThruAttributes(context, grid);    
        encodeFacet(context, grid, "header", DataGrid.MOBILE_HEADER_CLASS);

        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            paginatorRenderer.encodeMarkup(context, grid, "top");
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", contentClass, null);

        if(empty)
            writer.write(grid.getEmptyMessage());
        else
            encodeContent(context, grid);
        
        writer.endElement("div");

        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            paginatorRenderer.encodeMarkup(context, grid, "bottom");
        }
        
        encodeFacet(context, grid, "footer", DataGrid.MOBILE_FOOTER_CLASS);

        writer.endElement("div");
    }
    
    @Override
    protected void encodeContent(FacesContext context, DataGrid grid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int columns = grid.getColumns();
        int first = grid.getFirst();
        int rows = grid.getRows();
        int last = (rows != 0) ? (first + rows) : grid.getRowCount();
        
        for(int i = first; i < last; i++) {
            grid.setRowIndex(i);
            if(!grid.isRowAvailable()) {
                break;
            }
            
            for(UIComponent child : grid.getChildren()) {
                if(child.isRendered()) {
                    int blockKey = (i % columns);
                    String blockClass = MobileUtils.BLOCK_MAP.get(blockKey);
                    writer.startElement("div", null);
                    writer.writeAttribute("class", blockClass, null);
                    child.encodeAll(context);
                    writer.endElement("div");                
                }
            }
        }

        grid.setRowIndex(-1);	//cleanup
    }
    
    private PaginatorRenderer getPaginatorRenderer(FacesContext context) {
        return (PaginatorRenderer) context.getRenderKit().getRenderer("org.primefaces.component", "org.primefaces.component.PaginatorRenderer");
    }
}

