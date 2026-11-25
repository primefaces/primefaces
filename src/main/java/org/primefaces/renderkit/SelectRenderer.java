/**
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek
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
import javax.faces.application.FacesMessage;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;

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
                        Map<?, ?> map = (Map) value;

                        for (Map.Entry<?, ?> entry : map.entrySet()) {
                            selectItems.add(createSelectItem(context, uiSelectItems, entry.getValue(), String.valueOf(entry.getKey())));
                        }
                    }
                    else if (value instanceof List && value instanceof RandomAccess) {
                        List<?> list = (List) value;

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
                        Collection<?> collection = (Collection) value;

                        for (Object item : collection) {
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
        boolean disabled = itemDisabled != null && Boolean.parseBoolean(itemDisabled.toString());
        boolean escaped = itemEscaped == null || Boolean.parseBoolean(itemEscaped.toString());
        boolean noSelectionOption = noSelection != null && Boolean.parseBoolean(noSelection.toString());

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
     * Restores checked, disabled select items (#3296) and leaves out submitted disabled select items which were not
     * checked before - this may occur with client side manipulation (#3264, #13954)
     *
     * @param context The FacesContext
     * @param component The component
     * @param oldValues The old value(s)
     * @param submittedValues The submitted value(s)
     *
     * @return <code>newSubmittedValues</code> merged with checked, disabled <code>oldValues</code>
     */
    protected List<String> validateSubmittedValues(FacesContext context, UIInput component, Object[] oldValues, String... submittedValues) {
        return validateSubmittedValues(context, component, oldValues, null, submittedValues);
    }

    /**
     * Restores checked, disabled select items (#3296) and collects submitted disabled select items which were not
     * checked before - this may occur with client side manipulation (#3264) or when the item has been disabled
     * after rendering (#13954).
     * A disabled select item with an empty value (a placeholder) is never treated as a submitted disabled item.
     *
     * @param context The FacesContext
     * @param component The component
     * @param oldValues The old value(s)
     * @param disabledSubmittedValues Optional list to collect the submitted disabled select item values which were not checked before
     * @param submittedValues The submitted value(s)
     *
     * @return <code>newSubmittedValues</code> merged with checked, disabled <code>oldValues</code>
     */
    protected List<String> validateSubmittedValues(FacesContext context, UIInput component, Object[] oldValues,
            List<String> disabledSubmittedValues, String... submittedValues) {

        // resolve the converter once instead of per select-item
        Converter converter = component.getConverter();

        // oldValues are model values (e.g. Integer, enum, entity), convert them once to compare them with the
        // string values of the select items
        Set<String> oldValuesSet;
        if (oldValues == null || oldValues.length == 0) {
            oldValuesSet = Collections.emptySet();
        }
        else {
            oldValuesSet = new HashSet<>(oldValues.length);
            for (Object oldValue : oldValues) {
                oldValuesSet.add(getOptionAsString(context, component, converter, oldValue));
            }
        }

        Set<String> submittedValuesSet = (submittedValues == null || submittedValues.length == 0)
                ? Collections.<String>emptySet()
                : new HashSet<>(Arrays.asList(submittedValues));

        List<String> validSubmittedValues = new ArrayList<>();
        doValidateSubmittedValues(context, component, converter, oldValuesSet, submittedValuesSet,
                getSelectItems(context, component), validSubmittedValues, disabledSubmittedValues);
        return validSubmittedValues;
    }

    private void doValidateSubmittedValues(
            FacesContext context,
            UIInput component,
            Converter converter,
            Set<String> oldValues,
            Set<String> submittedValues,
            List<SelectItem> selectItems,
            List<String> validSubmittedValues,
            List<String> disabledSubmittedValues) {

        // loop attached SelectItems - other values are not allowed
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            if (selectItem instanceof SelectItemGroup) {
                // if it's a SelectItemGroup also include its children in the checked values
                SelectItem[] groupItemsArray = ((SelectItemGroup) selectItem).getSelectItems();
                if (groupItemsArray != null && groupItemsArray.length > 0) {
                    doValidateSubmittedValues(context, component, converter, oldValues, submittedValues,
                            Arrays.asList(groupItemsArray), validSubmittedValues, disabledSubmittedValues);
                }
            }
            else {
                String selectItemVal = getOptionAsString(context, component, converter, selectItem.getValue());

                if (selectItem.isDisabled()) {
                    if (oldValues.contains(selectItemVal)) {
                        // a checked, disabled item is not submitted by the browser, restore it (#3296)
                        validSubmittedValues.add(selectItemVal);
                    }
                    else if (submittedValues.contains(selectItemVal) && disabledSubmittedValues != null
                            && LangUtils.isNotEmpty(selectItemVal)) {
                        // a disabled item which was not checked before has been submitted: client side manipulation
                        // (#3264) or the item has been disabled after rendering (#13954). The caller marks the
                        // component as invalid. An empty value is a placeholder and not collected here.
                        disabledSubmittedValues.add(selectItemVal);
                    }
                }
                else {
                    if (submittedValues.contains(selectItemVal)) {
                        validSubmittedValues.add(selectItemVal);
                    }
                }
            }
        }
    }

    /**
     * Marks the component as invalid because a disabled select item has been submitted (#13954).
     *
     * @param context The FacesContext
     * @param component The component
     * @param messageId The message id, e.g. {@link javax.faces.component.UISelectOne#INVALID_MESSAGE_ID}
     */
    protected void invalidateDisabledSelectItem(FacesContext context, UIInput component, String messageId) {
        component.setValid(false);
        context.addMessage(component.getClientId(context), MessageFactory.getMessage(messageId,
                FacesMessage.SEVERITY_ERROR, new Object[]{MessageFactory.getLabel(context, component)}));
    }
}
