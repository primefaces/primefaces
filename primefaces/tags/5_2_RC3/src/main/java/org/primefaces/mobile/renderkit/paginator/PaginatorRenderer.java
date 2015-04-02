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
package org.primefaces.mobile.renderkit.paginator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.UIData;
import org.primefaces.component.paginator.PaginatorElementRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class PaginatorRenderer extends CoreRenderer {
    
    private static Map<String,PaginatorElementRenderer> ELEMENTS;
    
    static {
        ELEMENTS = new HashMap<String, PaginatorElementRenderer>();
        ELEMENTS.put("{FirstPageLink}", new FirstPageLinkRenderer());
        ELEMENTS.put("{PreviousPageLink}", new PrevPageLinkRenderer());
        ELEMENTS.put("{NextPageLink}", new NextPageLinkRenderer());
        ELEMENTS.put("{LastPageLink}", new LastPageLinkRenderer());
        ELEMENTS.put("{PageLinks}", new PageLinksRenderer());
    }

    public void encodeMarkup(FacesContext context, UIData uidata, String position) throws IOException {
        if(!uidata.isPaginatorAlwaysVisible() && uidata.getPageCount() <= 1) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        String id = uidata.getClientId(context) + "_paginator_" + position; 
                
        writer.startElement("div", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("class", "ui-bar-a ui-paginator", null);
        writer.writeAttribute("role", "navigation", null);
        
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-controlgroup ui-controlgroup-horizontal ui-corner-all", null);
        
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-controlgroup-controls", null);
        
        String[] elements = uidata.getPaginatorTemplate().split(" ");
        for(String element : elements) {            
            PaginatorElementRenderer renderer = ELEMENTS.get(element);
            if(renderer != null) {
                renderer.render(context, uidata);
            } 
            else {
                UIComponent elementFacet = uidata.getFacet(element);
                if(elementFacet != null)
                    elementFacet.encodeAll(context);
                //else
                    //writer.write(element + " ");
            }
        }
        
        writer.endElement("div");
        
        writer.endElement("div");

        writer.endElement("div");
    }

    public void encodeScript(FacesContext context, UIData uidata, WidgetBuilder wb) throws IOException {
        String clientId = uidata.getClientId(context);
        String paginatorPosition = uidata.getPaginatorPosition();
        String paginatorContainers = null;
        String currentPageTemplate = uidata.getCurrentPageReportTemplate();
        
        if(paginatorPosition.equalsIgnoreCase("both"))
            paginatorContainers = "'" + clientId + "_paginator_top','" + clientId + "_paginator_bottom'";
        else
            paginatorContainers = "'" + clientId + "_paginator_" + paginatorPosition + "'";

        wb.append(",paginator:{")
            .append("id:[").append(paginatorContainers).append("]")
            .append(",rows:").append(uidata.getRows())
            .append(",rowCount:").append(uidata.getRowCount())
            .append(",page:").append(uidata.getPage());
        
        if(currentPageTemplate != null)
            wb.append(",currentPageTemplate:'").append(currentPageTemplate).append("'");

        if(uidata.getPageLinks() != 10) 
            wb.append(",pageLinks:").append(uidata.getPageLinks());
        
        if(!uidata.isPaginatorAlwaysVisible()) 
            wb.append(",alwaysVisible:false");

        wb.append("}");
    }
}
