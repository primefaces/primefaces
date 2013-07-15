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
package org.primefaces.component.multiselectlistbox;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.component.selectonelistbox.SelectOneListbox;
import org.primefaces.renderkit.SelectOneRenderer;

public class MultiSelectListboxRenderer extends SelectOneRenderer {

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectOne", "javax.faces.Listbox").getConvertedValue(context, component, submittedValue);
	}
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        MultiSelectListbox listbox = (MultiSelectListbox) component;

        encodeMarkup(context, listbox);
        encodeScript(context, listbox);
    }
    
    protected void encodeMarkup(FacesContext context, MultiSelectListbox listbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = listbox.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, listbox);      
        String style = listbox.getStyle();
        String styleClass = listbox.getStyleClass();
        styleClass = styleClass == null ? SelectOneListbox.CONTAINER_CLASS : SelectOneListbox.CONTAINER_CLASS + " " + styleClass;
        styleClass = listbox.isDisabled() ? styleClass + " ui-state-disabled" : styleClass;
        styleClass = !listbox.isValid() ? styleClass + " ui-state-error" : styleClass;
        
        writer.startElement("div", listbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        encodeLists(context, listbox, selectItems);
        
        writer.endElement("div");
    }
    
    protected void encodeLists(FacesContext context, MultiSelectListbox listbox, List<SelectItem> items) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", listbox);
        writer.writeAttribute("class", MultiSelectListbox.LIST_CONTAINER_CLASS, null);
        
        writer.startElement("ul", listbox);
        writer.writeAttribute("class", MultiSelectListbox.LIST_CLASS, null);
        writer.endElement("ul");
        
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, MultiSelectListbox listbox) throws IOException {
    
    } 
    
    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return null;
    }


}
