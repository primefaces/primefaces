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
package org.primefaces.renderkit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.Pageable;
import org.primefaces.component.api.UIData;
import org.primefaces.component.paginator.CurrentPageReportRenderer;
import org.primefaces.component.paginator.FirstPageLinkRenderer;
import org.primefaces.component.paginator.JumpToPageDropdownRenderer;
import org.primefaces.component.paginator.LastPageLinkRenderer;
import org.primefaces.component.paginator.NextPageLinkRenderer;
import org.primefaces.component.paginator.PageLinksRenderer;
import org.primefaces.component.paginator.PaginatorElementRenderer;
import org.primefaces.component.paginator.PrevPageLinkRenderer;
import org.primefaces.component.paginator.RowsPerPageDropdownRenderer;
import org.primefaces.util.MessageFactory;
import org.primefaces.util.WidgetBuilder;

public class DataRenderer extends CoreRenderer {
    
    private static Map<String,PaginatorElementRenderer> PAGINATOR_ELEMENTS;
    
    static {
        PAGINATOR_ELEMENTS = new HashMap<String, PaginatorElementRenderer>();
        PAGINATOR_ELEMENTS.put("{CurrentPageReport}", new CurrentPageReportRenderer());
        PAGINATOR_ELEMENTS.put("{FirstPageLink}", new FirstPageLinkRenderer());
        PAGINATOR_ELEMENTS.put("{PreviousPageLink}", new PrevPageLinkRenderer());
        PAGINATOR_ELEMENTS.put("{NextPageLink}", new NextPageLinkRenderer());
        PAGINATOR_ELEMENTS.put("{LastPageLink}", new LastPageLinkRenderer());
        PAGINATOR_ELEMENTS.put("{PageLinks}", new PageLinksRenderer());
        PAGINATOR_ELEMENTS.put("{RowsPerPageDropdown}", new RowsPerPageDropdownRenderer());
        PAGINATOR_ELEMENTS.put("{JumpToPageDropdown}", new JumpToPageDropdownRenderer());
    }

    public static void addPaginatorElement(String element, PaginatorElementRenderer renderer) {
        PAGINATOR_ELEMENTS.put(element, renderer);
    }
    
    public static PaginatorElementRenderer removePaginatorElement(String element) {
        return PAGINATOR_ELEMENTS.remove(element);
    }
    
    protected void encodePaginatorMarkup(FacesContext context, Pageable pageable, String position) throws IOException {
        if(!pageable.isPaginatorAlwaysVisible() && pageable.getPageCount() <= 1) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        boolean isTop = position.equals("top");
        
        String styleClass = isTop ? UIData.PAGINATOR_TOP_CONTAINER_CLASS : UIData.PAGINATOR_BOTTOM_CONTAINER_CLASS;
        String id = pageable.getClientId(context) + "_paginator_" + position; 
        
        //add corners
        if(!isTop && pageable.getFooter() == null) {
            styleClass = styleClass + " ui-corner-bottom";
        }
        else if(isTop && pageable.getHeader() == null) {
            styleClass = styleClass + " ui-corner-top";
        }
        
        String ariaMessage = MessageFactory.getMessage(UIData.ARIA_HEADER_LABEL, new Object[]{});
        
        writer.startElement("div", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "navigation", null);
        writer.writeAttribute("aria-label", ariaMessage, null);
        
        String[] elements = pageable.getPaginatorTemplate().split(" ");
        for(String element : elements) {            
            PaginatorElementRenderer renderer = PAGINATOR_ELEMENTS.get(element);
            if(renderer != null) {
                renderer.render(context, pageable);
            } 
            else {
                UIComponent elementFacet = pageable.getFacet(element);
                if(elementFacet != null)
                    elementFacet.encodeAll(context);
                else
                    writer.write(element + " ");
            }
        }

        writer.endElement("div");
    }

    protected void encodePaginatorConfig(FacesContext context, Pageable pageable, WidgetBuilder wb) throws IOException {
        String clientId = pageable.getClientId(context);
        String paginatorPosition = pageable.getPaginatorPosition();
        String paginatorContainers = null;
        String currentPageTemplate = pageable.getCurrentPageReportTemplate();
        
        if(paginatorPosition.equalsIgnoreCase("both"))
            paginatorContainers = "'" + clientId + "_paginator_top','" + clientId + "_paginator_bottom'";
        else
            paginatorContainers = "'" + clientId + "_paginator_" + paginatorPosition + "'";

        wb.append(",paginator:{")
            .append("id:[").append(paginatorContainers).append("]")
            .append(",rows:").append(pageable.getRows())
            .append(",rowCount:").append(pageable.getRowCount())
            .append(",page:").append(pageable.getPage());

        if(currentPageTemplate != null) {
            String currentPageTemplateTmp = currentPageTemplate.replace("'", "\\'");
            wb.append(",currentPageTemplate:'").append(currentPageTemplateTmp).append("'");
        }

        if(pageable.getPageLinks() != 10)
            wb.append(",pageLinks:").append(pageable.getPageLinks());

        if(!pageable.isPaginatorAlwaysVisible()) 
            wb.append(",alwaysVisible:false");

        wb.append("}");
    }
    
    public void encodeFacet(FacesContext context, UIData data, String facet, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent component = data.getFacet(facet);
        
        if(component != null && component.isRendered()) {
            writer.startElement("div", null);
            writer.writeAttribute("class", styleClass, null);
            component.encodeAll(context);
            writer.endElement("div");
        }
    }
}
