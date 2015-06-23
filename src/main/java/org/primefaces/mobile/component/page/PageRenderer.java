/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.mobile.component.page;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class PageRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Page page = (Page) component;
        
        writer.startElement("div", null);
        writer.writeAttribute("id", page.getClientId(context), "id");
        
        renderDynamicPassThruAttributes(context, component);
        
        if(page.isLazyloadRequest(context)) {
            encodeContent(context, page);
        }
        else {
            if(page.isLazy())
                writer.writeAttribute("class", "ui-lazypage", null);
            else
                encodeContent(context, page);
        }
        
        writer.endElement("div");
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //do nothing
    }
    
    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
    private void encodeContent(FacesContext context, Page page) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String swatch = page.getSwatch();
        String title = page.getTitle();
        String style = page.getStyle();
        String styleClass = page.getStyleClass();

        writer.writeAttribute("data-role", "page", null);

        if(swatch != null) writer.writeAttribute("data-theme", swatch, null);
        if(title != null) writer.writeAttribute("data-title", title, null);
        if(style != null) writer.writeAttribute("style", style, null);
        if(styleClass != null) writer.writeAttribute("class", styleClass, null);

        renderChildren(context, page);
    }
}