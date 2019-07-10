/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.renderkit;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import org.primefaces.util.LangUtils;

public abstract class SelectRenderer extends InputRenderer {

    protected List<SelectItem> getSelectItems(FacesContext context, UIInput component) {
        List<SelectItem> selectItems = new ArrayList<>();

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
        boolean disabled = itemDisabled == null ? false : Boolean.parseBoolean(itemDisabled.toString());
        boolean escaped = itemEscaped == null ? true : Boolean.parseBoolean(itemEscaped.toString());
        boolean noSelectionOption = noSelection == null ? false : Boolean.parseBoolean(noSelection.toString());

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
        catch (ELException | IllegalArgumentException ele) {
            newValue = value;
        }

        return newValue;
    }

    protected boolean isSelected(FacesContext context, UIComponent component, Object itemValue, Object valueArray, Converter converter) {
        if (itemValue == null && valueArray == null) {
            return true;
        }

        if (itemValue == valueArray) {
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
    protected String[] validateSubmittedValues(FacesContext context, UIInput component, Object[] oldValues, String... submittedValues)
            throws FacesException {
        List<String> validSubmittedValues = doValidateSubmittedValues(
                context,
                component,
                oldValues,
                getSelectItems(context, component),
                submittedValues);
        return validSubmittedValues.toArray(new String[validSubmittedValues.size()]);
    }

    private List<String> doValidateSubmittedValues(
            FacesContext context,
            UIInput component,
            Object[] oldValues,
            List<SelectItem> selectItems,
            String... submittedValues) {

        List<String> validSubmittedValues = new ArrayList<>();

        // loop attached SelectItems - other values are not allowed
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            if (selectItem instanceof SelectItemGroup) {
                SelectItem[] groupItemsArray = ((SelectItemGroup) selectItem).getSelectItems();
                // if it's a SelectItemGroup also include its children in the checked values
                validSubmittedValues.addAll(
                        doValidateSubmittedValues(context,
                                component,
                                oldValues,
                                groupItemsArray == null ? Collections.emptyList() : Arrays.asList(groupItemsArray),
                                submittedValues));
            }
            else {
                String selectItemVal = getOptionAsString(context, component, component.getConverter(), selectItem.getValue());

                if (selectItem.isDisabled()) {
                    if (LangUtils.contains(submittedValues, selectItemVal) && !LangUtils.contains(oldValues, selectItemVal)) {
                        // disabled select item has been selected
                        // throw new FacesException("Disabled select item has been submitted. ClientId: " + component.getClientId(context));
                        // ignore it silently for now
                    }
                    else if (LangUtils.contains(oldValues, selectItemVal)) {
                        validSubmittedValues.add(selectItemVal);
                    }
                }
                else {
                    if (LangUtils.contains(submittedValues, selectItemVal)) {
                        validSubmittedValues.add(selectItemVal);
                    }
                }
            }
        }

        return validSubmittedValues;
    }
}
