/*
 * Copyright 2009-2011 Prime Technology.
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
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.UIData;

public class DataRenderer extends CoreRenderer {

    protected void encodePaginatorMarkup(FacesContext context, UIData uidata, String position) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int pageCount = (int) Math.ceil(uidata.getRowCount() * 1d / uidata.getRows());
        boolean isTop = position.equals("top");
        
        String styleClass = isTop ? UIData.PAGINATOR_TOP_CONTAINER_CLASS : UIData.PAGINATOR_BOTTOM_CONTAINER_CLASS;
        String id = uidata.getClientId(context) + "_paginator_" + position; 
        
        //add corners
        if(!isTop && uidata.getFooter() == null) {
            styleClass = styleClass + " ui-corner-bl ui-corner-br";
        }
        else if(isTop && uidata.getHeader() == null) {
            styleClass = styleClass + " ui-corner-tl ui-corner-tr";
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("class", styleClass, null);
        
        encodeCurrentPageReport(context, uidata, pageCount);
        
        boolean prevDisabled = uidata.getPage() == 0;
        encodeFirstPageLink(context, uidata, prevDisabled);
        encodePrevPageLink(context, uidata, prevDisabled);
        
        encodePageLinks(context, uidata, pageCount);
        
        boolean nextDisabled = uidata.getPage() == pageCount - 1;
        encodeNextPageLink(context, uidata, nextDisabled);
        encodeEndPageLink(context, uidata, nextDisabled);
        
        encodeRowsPerPageDropDown(context, uidata);
        
        encodeJumpToPageDropDown(context, uidata, pageCount);

        writer.endElement("div");
    }
    
    protected void encodePageLinks(FacesContext context, UIData uidata, int pageCount) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int currentPage = uidata.getPage();
        int pageLinks = uidata.getPageLinks();
        int pageLinkCountToRender = (pageCount > pageLinks) ? pageLinks : pageCount;
        
        writer.startElement("span", null);
        writer.writeAttribute("class", UIData.PAGINATOR_PAGES_CLASS, null);
        
        for(int i = currentPage; i < pageLinkCountToRender; i++){
            writer.startElement("span", null);
            writer.writeAttribute("class", UIData.PAGINATOR_PAGE_CLASS + (currentPage == i ? " ui-state-active" : ""), null);
            writer.writeText((i + 1), null);
            writer.endElement("span");
        }
            
        writer.endElement("span");
    }
    
    protected void encodeCurrentPageReport(FacesContext context, UIData uidata, int pageCount) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String text = uidata.getCurrentPageReportTemplate();
        if(text == null){
            text = "({currentPage} of {totalPage})";
        }
        
        text = text.replaceAll("[{]currentPage[}]", Integer.toString(uidata.getPage() + 1));
        text = text.replaceAll("[{]totalPage[}]", Integer.toString(pageCount));

        writer.startElement("span", null);
        writer.writeAttribute("class", UIData.PAGINATOR_CURRENT_CLASS, null);
            writer.writeText(text, null);
        
        writer.endElement("span");
    }
    
    protected void encodeFirstPageLink(FacesContext context, UIData uidata, boolean disabled) throws IOException {
        encodePaginatorLink(context, uidata, UIData.PAGINATOR_FIRST_PAGE_LINK_CLASS, UIData.PAGINATOR_FIRST_PAGE_ICON_CLASS, disabled);
    }
    
    protected void encodePrevPageLink(FacesContext context, UIData uidata, boolean disabled) throws IOException {
        encodePaginatorLink(context, uidata, UIData.PAGINATOR_PREV_PAGE_LINK_CLASS, UIData.PAGINATOR_PREV_PAGE_ICON_CLASS, disabled);
    }
        
    protected void encodeNextPageLink(FacesContext context, UIData uidata, boolean disabled) throws IOException {
        encodePaginatorLink(context, uidata, UIData.PAGINATOR_NEXT_PAGE_LINK_CLASS, UIData.PAGINATOR_NEXT_PAGE_ICON_CLASS, disabled);
    }
            
    protected void encodeEndPageLink(FacesContext context, UIData uidata, boolean disabled) throws IOException {
        encodePaginatorLink(context, uidata, UIData.PAGINATOR_END_PAGE_LINK_CLASS, UIData.PAGINATOR_END_PAGE_ICON_CLASS, disabled);
    }
    
    protected void encodePaginatorLink(FacesContext context, UIData uidata, String linkClass, String iconClass, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = disabled ? linkClass + " ui-state-disabled" : linkClass;
        
        writer.startElement("span", null);
        writer.writeAttribute("class", styleClass, null);
        
        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.writeText("p", null);
        writer.endElement("span");
        
        writer.endElement("span");
    }
    
    protected void encodeRowsPerPageDropDown(FacesContext context, UIData uidata) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int actualRows = uidata.getRows();
        
        writer.startElement("select", null);
        writer.writeAttribute("class", UIData.PAGINATOR_RPP_OPTIONS_CLASS, null);
        writer.writeAttribute("value", uidata.getRows(), null);
        
        String[] options;
        String template = uidata.getRowsPerPageTemplate();
        if(template == null){
            options = new String[]{ "10" };
        }
        else{
            options = uidata.getRowsPerPageTemplate().split("[,\\s]+");
        }
        
        for( String option : options){
            int rows = Integer.parseInt(option);
            writer.startElement("option", null);
            writer.writeAttribute("value", rows, null);
            
            if(actualRows == rows){
                writer.writeAttribute("selected", "selected", null);
            }
            
            writer.writeText(option, null);
            writer.endElement("option");
        }
        
        writer.endElement("select");
    }
    
    protected void encodeJumpToPageDropDown(FacesContext context, UIData uidata, int pageCount) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int currentPage = uidata.getPage();
        
        writer.startElement("select", null);
        writer.writeAttribute("class", UIData.PAGINATOR_JTP_CLASS, null);
        writer.writeAttribute("value", uidata.getPage(), null);
        
        for(int i = 0; i < pageCount; i++){
            writer.startElement("option", null);
            writer.writeAttribute("value", i, null);
            
            if(i == currentPage){
                writer.writeAttribute("selected", "selected", null);
            }
            
            writer.writeText((i+1), null);
            writer.endElement("option");
        }
        
        writer.endElement("select");
    }
    
    protected void encodePaginatorConfig(FacesContext context, UIData uidata) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = uidata.getClientId(context);
        String paginatorPosition = uidata.getPaginatorPosition();
        String paginatorContainers = null;
        String currentPageTemplate = uidata.getCurrentPageReportTemplate();
        
        if(paginatorPosition.equalsIgnoreCase("both"))
            paginatorContainers = "'" + clientId + "_paginator_top','" + clientId + "_paginator_bottom'";
        else
            paginatorContainers = "'" + clientId + "_paginator_" + paginatorPosition + "'";

        writer.write(",paginator:{");
        writer.write("id:[" + paginatorContainers + "]");
        writer.write(",rows:" + uidata.getRows());
        writer.write(",rowCount:" + uidata.getRowCount());
        writer.write(",page:" + uidata.getPage());
        
        if(currentPageTemplate != null)
            writer.write(",currentPageTemplate:'" + currentPageTemplate + "'");

        if(uidata.getPageLinks() != 10) writer.write(",pageLinks:" + uidata.getPageLinks());
        if(!uidata.isPaginatorAlwaysVisible()) writer.write(",alwaysVisible:false");

        writer.write("}");
    }
}
