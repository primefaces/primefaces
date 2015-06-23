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
package org.primefaces.mobile.component.rangeslider;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class RangeSliderRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        RangeSlider rangeSlider = (RangeSlider) component;
        
        encodeMarkup(context, rangeSlider);
        encodeScript(context, rangeSlider);
    }
    
    public void encodeMarkup(FacesContext context, RangeSlider rangeSlider) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = rangeSlider.getClientId(context);
        String style = rangeSlider.getStyle();
        String styleClass = rangeSlider.getStyleClass();
        
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");

        if (style != null) writer.writeAttribute("style", style, null);  
        if (styleClass != null) writer.writeAttribute("class", styleClass, null); 
        if (!rangeSlider.isHighlight()) writer.writeAttribute("data-highlight", "false", null);
        
        renderDynamicPassThruAttributes(context, rangeSlider);
        
        renderChildren(context, rangeSlider);
        
        writer.endElement("div");
    }
    
    public void encodeScript(FacesContext context, RangeSlider rangeSlider) throws IOException {
        String clientId = rangeSlider.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("RangeSlider", rangeSlider.resolveWidgetVar(), clientId).finish();
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}