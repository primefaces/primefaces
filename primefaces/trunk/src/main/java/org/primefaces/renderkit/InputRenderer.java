/*
 * Copyright 2010 Prime Technology.
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

public class InputRenderer extends CoreRenderer {

    protected void decodeBehaviors(FacesContext context, UIComponent component)  {

        if(!(component instanceof ClientBehaviorHolder)) {
            return;
        }

        Map<String, List<ClientBehavior>> behaviors = ((ClientBehaviorHolder) component).getClientBehaviors();
        if(behaviors.isEmpty()) {
            return;
        }

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String behaviorEvent = params.get("javax.faces.behavior.event");

        if(null != behaviorEvent) {
            List<ClientBehavior> behaviorsForEvent = behaviors.get(behaviorEvent);

            if(behaviors.size() > 0) {
               String behaviorSource = params.get("javax.faces.source");
               String clientId = component.getClientId();
               
               if(behaviorSource != null && behaviorSource.equals(clientId)) {
                   for (ClientBehavior behavior: behaviorsForEvent) {
                       behavior.decode(context, component);
                   }
               }
            }
        }
    }

    protected void encodeSelectItems(FacesContext context, UIInput component) throws IOException {
        Object componentValue = component.getValue();

        for(UIComponent child : component.getChildren()) {
            if(child instanceof UISelectItem) {
                UISelectItem uiSelectItem = (UISelectItem) child;

				encodeOption(context, component, componentValue, uiSelectItem.getItemLabel(), uiSelectItem.getItemValue());
			}
            else if(child instanceof UISelectItems) {
                UISelectItems uiSelectItems = ((UISelectItems) child);
				Object value = uiSelectItems.getValue();

                if(value instanceof SelectItem[]) {
                    for(SelectItem selectItem : (SelectItem[]) value) {
                        encodeOption(context, component, componentValue, selectItem.getLabel(), selectItem.getValue());
                    }
                }
                else if(value instanceof Map) {
                    Map map = (Map) value;

                    for(Iterator it = map.keySet().iterator(); it.hasNext();) {
                        Object key = it.next();
                        encodeOption(context, component, componentValue, String.valueOf(key), map.get(key));
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

                        encodeOption(context, component, componentValue, itemLabel, itemValue);
                    }
                }
			}
        }
	}

    protected void encodeOption(FacesContext context, UIInput component, Object componentValue, String label, Object value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String formattedValue = formatOptionValue(context, component, value);

        writer.startElement("option", null);
        writer.writeAttribute("value", formattedValue, null);
        if(componentValue != null && componentValue.equals(value)) {
            writer.writeAttribute("selected", "selected", null);
        }
        writer.write(label);
        writer.endElement("option");
    }

	public String formatOptionValue(FacesContext context, UIInput component, Object value) {
		Converter converter = component.getConverter();

		//first ask the converter
		if(converter != null) {
			return converter.getAsString(context, component, value);
		}
		//Try to guess
		else {
            ValueExpression ve = component.getValueExpression("value");

            if(ve != null) {
                Class<?> valueType = ve.getType(context.getELContext());
                Converter converterForType = context.getApplication().createConverter(valueType);

                if(converterForType != null) {
                    return converterForType.getAsString(context, component, value);
                }
            }
		}

		if(value == null)
            return "";
        else
            return value.toString();
	}
}
