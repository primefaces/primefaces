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
package org.primefaces.mobile.component.inputslider;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.mobile.component.rangeslider.RangeSlider;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class InputSliderRenderer extends InputRenderer {
    
    @Override
	public void decode(FacesContext context, UIComponent component) {
        if (!shouldDecode(component)) {
            return;
        }

        InputSlider inputSlider = (InputSlider) component;
        String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(inputSlider.getClientId(context));

        if (submittedValue != null) {
            inputSlider.setSubmittedValue(submittedValue);
        }
	}
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        InputSlider inputSlider = (InputSlider) component;
        
        encodeMarkup(context, inputSlider);
        
        if(!(inputSlider.getParent() instanceof RangeSlider)) {
            encodeScript(context, inputSlider);
        }
    }

    protected void encodeMarkup(FacesContext context, InputSlider inputSlider) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = inputSlider.getClientId(context);
        String style = inputSlider.getStyle();
        String styleClass = inputSlider.getStyleClass();
        String valueToRender = ComponentUtils.getValueToRender(context, inputSlider);
        
        writer.startElement("input", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", "number", null);
        writer.writeAttribute("min", inputSlider.getMinValue(), null);
        writer.writeAttribute("max", inputSlider.getMaxValue(), null);
        writer.writeAttribute("step", inputSlider.getStep(), null);
        writer.writeAttribute("data-role", "none", null);
        
        if (style != null) writer.writeAttribute("style", style, null);  
        if (styleClass != null) writer.writeAttribute("class", styleClass, null); 
        if (inputSlider.isHighlight()) writer.writeAttribute("data-highlight", "true", null);
        if (inputSlider.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        if (valueToRender != null) writer.writeAttribute("value", valueToRender , null);

        renderDynamicPassThruAttributes(context, inputSlider);

        writer.endElement("input");
    }

    protected void encodeScript(FacesContext context, InputSlider inputSlider) throws IOException {
        String clientId = inputSlider.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("InputSlider", inputSlider.resolveWidgetVar(), clientId).finish();
    }
}