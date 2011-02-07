/*
 * Copyright 2009-2010 Prime Technology.
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
package org.primefaces.component.selectonemenu;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import org.primefaces.renderkit.CoreRenderer;

public class SelectOneMenuRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectOneMenu menu = (SelectOneMenu) component;

        encodeMarkup(context, menu);
        encodeScript(context, menu);
    }

    protected void encodeMarkup(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);

        writer.startElement("select", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, null);

        encodeSelectItems(context, menu);

        writer.endElement("select");
    }

    protected void encodeScript(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);

        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(menu.resolveWidgetVar() + " = new PrimeFaces.widget.SelectOneMenu('" + clientId + "',{");

        writer.write("});");

        writer.endElement("script");
    }

    protected void encodeSelectItems(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
 
        for(UIComponent child : component.getChildren()) {
            if(child instanceof UISelectItem) {
                UISelectItem uiSelectItem = (UISelectItem) child;
                
				encodeOption(context, component, uiSelectItem.getItemLabel(), uiSelectItem.getItemValue());
			}
            else if(child instanceof UISelectItems) {
                UISelectItems uiSelectItems = ((UISelectItems) child);
				Object value = uiSelectItems.getValue();

                if(value instanceof SelectItem[]) {
                    for(SelectItem selectItem : (SelectItem[]) value) {
                        encodeOption(context, component, selectItem.getLabel(), selectItem.getValue());
                    }
                } 
                else if(value instanceof Map) {
                    Map map = (Map) value;

                    for(Iterator it = map.keySet().iterator(); it.hasNext();) {
                        Object key = it.next();
                        encodeOption(context, component, String.valueOf(key), map.get(key));
                    }
                }
                else if(value instanceof Collection) {
                    Collection collection = (Collection) value;
                    String var = (String) uiSelectItems.getAttributes().get("var");

                    for (Iterator it = collection.iterator(); it.hasNext();) {
                        Object object = it.next();
                        context.getExternalContext().getRequestMap().put(var, object);
                        String itemLabel = (String) uiSelectItems.getAttributes().get("itemLabel");
                        Object itemValue = uiSelectItems.getAttributes().get("itemValue");

                        encodeOption(context, component, itemLabel, itemValue);
                    }
                }
			}
        }

	}

    protected void encodeOption(FacesContext context, UIComponent component, String label, Object value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("option", null);
        writer.writeAttribute("value", value, null);
        writer.write(label);
        writer.endElement("option");
    }
}
