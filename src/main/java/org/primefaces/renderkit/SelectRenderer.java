/**
 * Copyright 2009-2018 PrimeTek.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

public class SelectRenderer extends InputRenderer {

    protected List<SelectItem> getSelectItems(FacesContext context, UIInput component) {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();

        for (int i = 0; i < component.getChildCount(); i++) {
            UIComponent child = component.getChildren().get(i);
            if (child instanceof UISelectItem) {
                UISelectItem uiSelectItem = (UISelectItem) child;
                Object selectItemValue = uiSelectItem.getValue();

                if (selectItemValue == null) {
                    selectItems.add(new SelectItem(uiSelectItem.getItemValue(),
                            uiSelectItem.getItemLabel(),
                            uiSelectItem.getItemDescription(),
                            uiSelectItem.isItemDisabled(),
                            uiSelectItem.isItemEscaped(),
                            uiSelectItem.isNoSelectionOption()));
                }
                else {
                    selectItems.add((SelectItem) selectItemValue);
                }
            }
            else if (child instanceof UISelectItems) {
                UISelectItems uiSelectItems = ((UISelectItems) child);
                Object value = uiSelectItems.getValue();

                if (value != null) {
                    if (value instanceof SelectItem) {
                        selectItems.add((SelectItem) value);
                    }
                    else if (value.getClass().isArray()) {
                        for (int j = 0; j < Array.getLength(value); j++) {
                            Object item = Array.get(value, j);

                            if (item instanceof SelectItem) {
                                selectItems.add((SelectItem) item);
                            }
                            else {
                                selectItems.add(createSelectItem(context, uiSelectItems, item, null));
                            }
                        }
                    }
                    else if (value instanceof Map) {
                        Map map = (Map) value;

                        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
                            Object key = it.next();

                            selectItems.add(createSelectItem(context, uiSelectItems, map.get(key), String.valueOf(key)));
                        }
                    }
                    else if (value instanceof List && value instanceof RandomAccess) {
                        List list = (List) value;

                        for (int j = 0; j < list.size(); j++) {
                            Object item = list.get(j);
                            if (item instanceof SelectItem) {
                                selectItems.add((SelectItem) item);
                            }
                            else {
                                selectItems.add(createSelectItem(context, uiSelectItems, item, null));
                            }
                        }
                    }
                    else if (value instanceof Collection) {
                        Collection collection = (Collection) value;

                        for (Iterator it = collection.iterator(); it.hasNext();) {
                            Object item = it.next();
                            if (item instanceof SelectItem) {
                                selectItems.add((SelectItem) item);
                            }
                            else {
                                selectItems.add(createSelectItem(context, uiSelectItems, item, null));
                            }
                        }
                    }
                }
            }
        }

        return selectItems;
    }

    protected SelectItem createSelectItem(FacesContext context, UISelectItems uiSelectItems, Object value, Object label) {
        String var = (String) uiSelectItems.getAttributes().get("var");
        Map<String, Object> attrs = uiSelectItems.getAttributes();
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

        if (var != null) {
            requestMap.put(var, value);
        }

        Object itemLabelValue = attrs.get("itemLabel");
        Object itemValue = attrs.get("itemValue");
        String description = (String) attrs.get("itemDescription");
        Object itemDisabled = attrs.get("itemDisabled");
        Object itemEscaped = attrs.get("itemLabelEscaped");
        Object noSelection = attrs.get("noSelectionOption");

        if (itemValue == null) {
            itemValue = value;
        }

        if (itemLabelValue == null) {
            itemLabelValue = label;
        }

        String itemLabel = itemLabelValue == null ? String.valueOf(value) : String.valueOf(itemLabelValue);
        boolean disabled = itemDisabled == null ? false : Boolean.valueOf(itemDisabled.toString());
        boolean escaped = itemEscaped == null ? true : Boolean.valueOf(itemEscaped.toString());
        boolean noSelectionOption = noSelection == null ? false : Boolean.valueOf(noSelection.toString());

        if (var != null) {
            requestMap.remove(var);
        }

        return new SelectItem(itemValue, itemLabel, description, disabled, escaped, noSelectionOption);
    }

    protected String getOptionAsString(FacesContext context, UIComponent component, Converter converter, Object value) throws ConverterException {
        if (!(component instanceof ValueHolder)) {
            return value == null ? null : value.toString();
        }

        if (converter == null) {
            if (value == null) {
                return "";
            }
            else if (value instanceof String) {
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

        if (ve != null) {
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

    protected boolean isSelected(FacesContext context, UIComponent component, Object itemValue, Object valueArray, Converter converter) {
        if (itemValue == null && valueArray == null) {
            return true;
        }

        if (valueArray != null) {
            if (!valueArray.getClass().isArray()) {
                return valueArray.equals(itemValue);
            }

            int length = Array.getLength(valueArray);
            for (int i = 0; i < length; i++) {
                Object value = Array.get(valueArray, i);

                if (value == null && itemValue == null) {
                    return true;
                }
                else {
                    if ((value == null) ^ (itemValue == null)) {
                        continue;
                    }

                    Object compareValue;
                    if (converter == null) {
                        compareValue = coerceToModelType(context, itemValue, value.getClass());
                    }
                    else {
                        compareValue = itemValue;

                        if (compareValue instanceof String && !(value instanceof String)) {
                            compareValue = converter.getAsObject(context, component, (String) compareValue);
                        }
                    }

                    if (value.equals(compareValue)) {
                        return (true);
                    }
                }
            }
        }
        return false;
    }

    protected int countSelectItems(List<SelectItem> selectItems) {
        if (selectItems == null) {
            return 0;
        }

        int count = selectItems.size();
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            if (selectItem instanceof SelectItemGroup) {
                count += countSelectItems(((SelectItemGroup) selectItem).getSelectItems());
            }
        }
        return count;
    }

    protected int countSelectItems(SelectItem[] selectItems) {
        if (selectItems == null) {
            return 0;
        }

        int count = selectItems.length;
        for (SelectItem selectItem : selectItems) {
            if (selectItem instanceof SelectItemGroup) {
                count += countSelectItems(((SelectItemGroup) selectItem).getSelectItems());
            }
        }
        return count;
    }

    /**
     * Restores checked, disabled select items (#3296) and checks if at least one disabled select item has been submitted - 
     * this may occur with client side manipulation (#3264)
     * @return <code>newSubmittedValues</code> merged with checked, disabled <code>oldValues</code>
     * @throws javax.faces.FacesException if client side manipulation has been detected, in order to reject the submission
     */
    protected String[] restoreAndCheckDisabledSelectItems(FacesContext context, UIInput component, Object[] oldValues, String... newSubmittedValues) 
            throws FacesException {
        List<String> restoredSubmittedValues = new ArrayList<String>();
        String msg = "Disabled select item has been submitted";
        List<Object> oldVals = oldValues == null ? Collections.emptyList() : Arrays.asList(oldValues);
        List<String> newSubmittedValsStr = newSubmittedValues == null ? Collections.<String>emptyList() : Arrays.asList(newSubmittedValues);
        for (SelectItem selectItem : getSelectItems(context, component)) {
            String selectItemValStr = getOptionAsString(context, component, component.getConverter(), selectItem.getValue());
            if (selectItem.isDisabled()) {
                if (newSubmittedValsStr.contains(selectItemValStr) && !oldVals.contains(selectItemValStr)) {
                    // disabled select item has been selected
                    throw new FacesException(msg);
                }
                if (oldVals.contains(selectItemValStr)) {
                    restoredSubmittedValues.add(selectItemValStr);
                }
            } 
            else {
                if (newSubmittedValsStr.contains(selectItemValStr)) {
                    restoredSubmittedValues.add(selectItemValStr);
                }
            }
        }
        return restoredSubmittedValues.toArray(new String[restoredSubmittedValues.size()]);
    }
    
}
