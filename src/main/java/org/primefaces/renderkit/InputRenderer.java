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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.component.*;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;

import org.primefaces.util.ComponentUtils;

public abstract class InputRenderer extends CoreRenderer {

    protected List<SelectItem> getSelectItems(FacesContext context, UIInput component) {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();

        for(UIComponent child : component.getChildren()) {
            if(child instanceof UISelectItem) {
                UISelectItem uiSelectItem = (UISelectItem) child;
                Object selectItemValue = uiSelectItem.getValue();
                
                if(selectItemValue == null) {
                    selectItems.add(new SelectItem(uiSelectItem.getItemValue(), uiSelectItem.getItemLabel(), uiSelectItem.getItemDescription(), uiSelectItem.isItemDisabled(), uiSelectItem.isItemEscaped(), uiSelectItem.isNoSelectionOption()));
                }
                else {
                    selectItems.add((SelectItem) selectItemValue);
                }	
			}
            else if(child instanceof UISelectItems) {
                UISelectItems uiSelectItems = ((UISelectItems) child);
				Object value = uiSelectItems.getValue();
                
                if(value != null) {
                    if(value instanceof SelectItem) {
                        selectItems.add((SelectItem) value);
                    }
                    else if(value.getClass().isArray()) {
                        for(int i = 0; i < Array.getLength(value); i++) {
                            Object item = Array.get(value, i);

                            if(item instanceof SelectItem)
                                selectItems.add((SelectItem) item);
                            else
                                selectItems.add(createSelectItem(context, uiSelectItems, item, null));
                        }
                    }
                    else if(value instanceof Map) {
                        Map map = (Map) value;

                        for(Iterator it = map.keySet().iterator(); it.hasNext();) {
                            Object key = it.next();
                            
                            selectItems.add(createSelectItem(context, uiSelectItems, map.get(key), String.valueOf(key)));
                        }
                    }
                    else if(value instanceof Collection) {
                        Collection collection = (Collection) value;

                        for(Iterator it = collection.iterator(); it.hasNext();) {
                            Object item = it.next();
                            if(item instanceof SelectItem)
                                selectItems.add((SelectItem) item);
                            else
                                selectItems.add(createSelectItem(context, uiSelectItems, item, null));
                        }               
                    }
                }
			}
        }

        return selectItems;
	}
    
    protected SelectItem createSelectItem(FacesContext context, UISelectItems uiSelectItems, Object value, Object label) {
        String var = (String) uiSelectItems.getAttributes().get("var");
        Map<String,Object> attrs = uiSelectItems.getAttributes();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        
        if(var != null) {
            requestMap.put(var, value);
        }

        Object itemLabelValue = attrs.get("itemLabel");
        Object itemValue = attrs.get("itemValue");
        String description = (String) attrs.get("itemDescription");
        Object itemDisabled = attrs.get("itemDisabled");
        Object itemEscaped = attrs.get("itemLabelEscaped");
        Object noSelection = attrs.get("noSelectionOption");

        if(itemValue == null) {
            itemValue = value;
        }
        
        if(itemLabelValue == null) {
            itemLabelValue = label;
        }

        String itemLabel = itemLabelValue == null ? String.valueOf(value) : String.valueOf(itemLabelValue);
        boolean disabled = itemDisabled == null ? false : Boolean.valueOf(itemDisabled.toString());
        boolean escaped = itemEscaped == null ? true : Boolean.valueOf(itemEscaped.toString());
        boolean noSelectionOption = noSelection == null ? false : Boolean.valueOf(noSelection.toString());
        
        if(var != null) {
            requestMap.remove(var);
        }

        return new SelectItem(itemValue, itemLabel, description, disabled, escaped, noSelectionOption);
    }
    
	protected String getOptionAsString(FacesContext context, UIComponent component, Converter converter, Object value) throws ConverterException {
        if(!(component instanceof ValueHolder)) {
            return value == null ? null : value.toString();
        }
        
        if(converter == null) {
            if(value == null) {
                return "";
            }
            else if(value instanceof String) {
                return (String) value;
            }
            else {
                Converter implicitConverter = findImplicitConverter(context, component);
                
                return implicitConverter == null ? value.toString() : implicitConverter.getAsString(context, component, value);                
            }
        }
        else {
            return converter.getAsString(context, component, value);
        }
	}

    protected Converter findImplicitConverter(FacesContext context, UIComponent component) {
        ValueExpression ve = component.getValueExpression("value");

        if(ve != null) {
            Class<?> valueType = ve.getType(context.getELContext());

            if (valueType != null) {
                if (valueType.isArray()) {
                    valueType = valueType.getComponentType();
                }
                
                return context.getApplication().createConverter(valueType);
            }
        }

        return null;
    }

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
    	Converter converter = ComponentUtils.getConverter(context, component);

		if(converter != null) {
            String convertableValue = submittedValue == null ? null : submittedValue.toString();
            return converter.getAsObject(context, component, convertableValue);
        }
        else {
            return submittedValue;
        }
	}
    
    protected Object coerceToModelType(FacesContext ctx, Object value, Class itemValueType) {
        Object newValue;
        try {
            ExpressionFactory ef = ctx.getApplication().getExpressionFactory();
            newValue = ef.coerceToType(value, itemValueType);
        } 
        catch (ELException ele) {
            newValue = value;
        } 
        catch (IllegalArgumentException iae) {
            newValue = value;
        }

        return newValue;
    }
    
    public static boolean shouldDecode(UIComponent component) {
        boolean disabled = Boolean.valueOf(String.valueOf(component.getAttributes().get("disabled")));
        boolean readonly = Boolean.valueOf(String.valueOf(component.getAttributes().get("readonly")));
        
        return !disabled && !readonly;
    }
}
