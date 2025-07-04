/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.component.api.WrapperSelectItem;
import org.primefaces.util.LangUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;

import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.UISelectItem;
import jakarta.faces.component.UISelectItems;
import jakarta.faces.component.ValueHolder;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;

public abstract class SelectRenderer<T extends UIInput> extends InputRenderer<T> {

    protected boolean isHideNoSelection(UIComponent component) {
        Object attribute = component.getAttributes().get("hideNoSelectionOption");
        if (attribute instanceof String) {
            attribute = Boolean.parseBoolean((String) attribute);
        }
        return Boolean.TRUE.equals(attribute);
    }

    protected void addSelectItem(T component, List<SelectItem> selectItems, SelectItem item, boolean hideNoSelectOption) {
        if (hideNoSelectOption && item.isNoSelectionOption()) {
            return;
        }
        selectItems.add(item);
    }

    protected List<SelectItem> getSelectItems(FacesContext context, T component) {
        List<SelectItem> selectItems = new ArrayList<>();
        boolean hideNoSelectOption = isHideNoSelection(component);
        SelectItem selectItem;

        for (int i = 0; i < component.getChildCount(); i++) {
            UIComponent child = component.getChildren().get(i);
            if (child instanceof UISelectItem) {
                UISelectItem uiSelectItem = (UISelectItem) child;
                Object selectItemValue = uiSelectItem.getValue();

                if (selectItemValue == null) {
                    selectItem = new WrapperSelectItem(uiSelectItem);
                }
                else {
                    selectItem = (SelectItem) selectItemValue;
                }
                addSelectItem(component, selectItems, selectItem, hideNoSelectOption);
            }
            else if (child instanceof UISelectItems) {
                UISelectItems uiSelectItems = ((UISelectItems) child);
                Object value = uiSelectItems.getValue();

                if (value != null) {
                    if (value instanceof SelectItem) {
                        addSelectItem(component, selectItems, (SelectItem) value, hideNoSelectOption);
                    }
                    else if (value.getClass().isArray()) {
                        for (int j = 0; j < Array.getLength(value); j++) {
                            Object item = Array.get(value, j);

                            if (item instanceof SelectItem) {
                                selectItem = updateSelectItem(context, uiSelectItems, (SelectItem) item);
                            }
                            else {
                                selectItem = createSelectItem(context, uiSelectItems, item, null);
                            }
                            addSelectItem(component, selectItems, selectItem, hideNoSelectOption);
                        }
                    }
                    else if (value instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) value;

                        for (Map.Entry<?, ?> entry : map.entrySet()) {
                            selectItem = createSelectItem(context, uiSelectItems, entry.getValue(), String.valueOf(entry.getKey()));
                            addSelectItem(component, selectItems, selectItem, hideNoSelectOption);
                        }
                    }
                    else if (value instanceof List && value instanceof RandomAccess) {
                        List<?> list = (List<?>) value;

                        for (int j = 0; j < list.size(); j++) {
                            Object item = list.get(j);
                            if (item instanceof SelectItem) {
                                selectItem = updateSelectItem(context, uiSelectItems, (SelectItem) item);
                            }
                            else {
                                selectItem = createSelectItem(context, uiSelectItems, item, null);
                            }
                            addSelectItem(component, selectItems, selectItem, hideNoSelectOption);
                        }
                    }
                    else if (value instanceof Collection) {
                        Collection<?> collection = (Collection<?>) value;

                        for (Object item : collection) {
                            if (item instanceof SelectItem) {
                                selectItem = updateSelectItem(context, uiSelectItems, (SelectItem) item);
                            }
                            else {
                                selectItem = createSelectItem(context, uiSelectItems, item, null);
                            }
                            addSelectItem(component, selectItems, selectItem, hideNoSelectOption);
                        }
                    }
                    else {
                        throw new FacesException("SelectItems' value type is not compatible: " + value.getClass());
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
        boolean disabled = itemDisabled != null && Boolean.parseBoolean(itemDisabled.toString());
        boolean escaped = itemEscaped == null || Boolean.parseBoolean(itemEscaped.toString());
        boolean noSelectionOption = noSelection != null && Boolean.parseBoolean(noSelection.toString());

        if (var != null) {
            requestMap.remove(var);
        }

        WrapperSelectItem wrapper = new WrapperSelectItem(itemValue, itemLabel, description, disabled, escaped, noSelectionOption);
        wrapper.setComponent(uiSelectItems);
        return wrapper;
    }

    protected SelectItem updateSelectItem(FacesContext context, UISelectItems uiSelectItems, SelectItem value) {
        if (value instanceof SelectItemGroup) {
            return value;
        }
        String var = (String) uiSelectItems.getAttributes().get("var");
        Map<String, Object> attrs = uiSelectItems.getAttributes();
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

        if (var != null) {
            requestMap.put(var, value);
        }

        Object itemLabelValue = attrs.get("itemLabel");
        Object itemDisabled = attrs.get("itemDisabled");
        Object itemEscaped = attrs.get("itemLabelEscaped");
        Object noSelection = attrs.get("noSelectionOption");

        if (itemLabelValue != null) {
            value.setLabel(String.valueOf(itemLabelValue));
        }
        if (itemDisabled != null) {
            value.setDisabled(Boolean.parseBoolean(itemDisabled.toString()));
        }
        if (itemEscaped != null) {
            value.setEscape(Boolean.parseBoolean(itemEscaped.toString()));
        }
        if (noSelection != null) {
            value.setNoSelectionOption(Boolean.parseBoolean(noSelection.toString()));
        }

        if (var != null) {
            requestMap.remove(var);
        }

        return value;
    }

    protected String getOptionAsString(FacesContext context, T component, Converter converter, Object value) throws ConverterException {
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

    protected Converter<?> findImplicitConverter(FacesContext context, T component) {
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

    protected Object coerceToModelType(FacesContext ctx, Object value, Class<?> itemValueType) {
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

                if (isSelectValueEqual(context, component, itemValue, value, converter)) {
                    return (true);
                }
            }
        }
        return false;
    }

    /**
     * Compares two select options against each other. Values can be either a serialized string,
     * or the actual object, this method takes care of the conversion.
     * @param context The currently active faces context.
     * @param component The select component for which to compare values.
     * @param itemValue First value to compare against the second. May be a submitted string value,
     * in which case it run through the given {@code converter}.
     * @param value Second value to compare against the first. Should be the model value, i.e. not
     * a string, unless {@code itemValue} is a string too.
     * @param converter Optional converter defined for the select component.
     * @return {@code true} if the two values are equal, or {@code false} otherwise.
     */
    protected boolean isSelectValueEqual(FacesContext context, UIComponent component, Object itemValue, Object value, Converter converter) {
        if (value == null && itemValue == null) {
            return true;
        }
        if ((value == null) ^ (itemValue == null)) {
            return false;
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

        return Objects.equals(value, compareValue);
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
     *
     * @param context The FacesContext
     * @param component The component
     * @param oldValues The old value(s)
     * @param submittedValues The submitted value(s)
     *
     * @return <code>newSubmittedValues</code> merged with checked, disabled <code>oldValues</code>
     * @throws jakarta.faces.FacesException if client side manipulation has been detected, in order to reject the submission
     */
    protected List<String> validateSubmittedValues(FacesContext context, T component, Object[] oldValues, String... submittedValues)
            throws FacesException {
        return doValidateSubmittedValues(
                context,
                component,
                oldValues,
                getSelectItems(context, component),
                submittedValues);
    }

    private List<String> doValidateSubmittedValues(
            FacesContext context,
            T component,
            Object[] oldValues,
            List<SelectItem> selectItems,
            String... submittedValues) {

        List<String> validSubmittedValues = new ArrayList<>();

        // loop attached SelectItems - other values are not allowed
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            if (selectItem instanceof SelectItemGroup) {
                // if it's a SelectItemGroup also include its children in the checked values
                SelectItem[] groupItemsArray = ((SelectItemGroup) selectItem).getSelectItems();
                if (groupItemsArray != null && groupItemsArray.length > 0) {
                    validSubmittedValues.addAll(
                            doValidateSubmittedValues(context,
                                    component,
                                    oldValues,
                                    Arrays.asList(groupItemsArray),
                                    submittedValues));
                }
            }
            else {
                String selectItemVal = getOptionAsString(context, component, component.getConverter(), selectItem.getValue());

                if (selectItem.isDisabled()) {
                    if (LangUtils.contains(submittedValues, selectItemVal) && !LangUtils.contains(oldValues, selectItemVal)) {
                        // disabled select item has been selected
                        throw new FacesException("Disabled select item has been submitted. ClientId: " + component.getClientId(context));
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

    /**
     * Helper method to find the defining component of a SelectItem so passthrough attributes can be rendered.
     * @param item the SelectItem to check
     * @return either NULL or a component the SelectItem was defined by
     */
    public UIComponent getSelectItemComponent(SelectItem item) {
        if (item instanceof WrapperSelectItem) {
            WrapperSelectItem wrapper = (WrapperSelectItem) item;
            return wrapper.getComponent();
        }

        return null;
    }
}
