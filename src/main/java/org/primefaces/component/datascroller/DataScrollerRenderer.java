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
package org.primefaces.component.datascroller;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class DataScrollerRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DataScroller ds = (DataScroller) component;
        
        encodeMarkup(context, ds);
        encodeScript(context, ds);
    }
    
    protected void encodeMarkup(FacesContext context, DataScroller ds) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ds.getClientId(context);
        String style = ds.getStyle();
        String userStyleClass = ds.getStyleClass();
        String styleClass = (userStyleClass == null) ? DataScroller.CONTAINER_CLASS : DataScroller.CONTAINER_CLASS + " " + userStyleClass;
        
        writer.startElement("div", ds);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", styleClass, null);
        }
        
        writer.endElement("div");
    }
    
    protected void encodeScript(FacesContext context, DataScroller ds) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ds.getClientId(context);
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataScroller", ds.resolveWidgetVar(), clientId).finish();
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
