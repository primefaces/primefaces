/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.faces.component.UISelectOne;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;

public abstract class SelectOneRenderer<T extends UISelectOne> extends SelectRenderer<T> {

    @Override
    public void decode(FacesContext context, T component) {
        if (!shouldDecode(component)) {
            return;
        }

        String clientId = getSubmitParam(context, component);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        String submittedValue = params.containsKey(clientId) ? params.get(clientId) : "";
        List<String> validSubmittedValues = validateSubmittedValues(context, component, (Object[]) getValues(component), submittedValue);
        component.setSubmittedValue(validSubmittedValues.isEmpty() || validSubmittedValues.contains(submittedValue)
                ? submittedValue
                : validSubmittedValues.get(0));

        decodeBehaviors(context, component);
    }

    protected Object getValues(T component) {
        Object value = component.getValue();

        if (value != null) {
            return new Object[]{value};
        }

        return null;
    }

    protected Object getSubmittedValues(T component) {
        Object val = component.getSubmittedValue();
        if (val != null) {
            return new Object[]{val};
        }

        return null;
    }

    /**
     * Recursive method used to find a SelectItem by its label.
     * @param fc FacesContext
     * @param component the current UI component to find value for
     * @param converter the converter for the select items
     * @param selectItems the List of SelectItems
     * @param valueOrLabel the input value/label to search for
     * @return either the SelectItem found or NULL if not found
     */
    protected SelectItem findSelectItemByLabel(FacesContext fc, T component, Converter converter, List<SelectItem> selectItems,
                String valueOrLabel) {
        return findSelectItem(fc, component, converter, selectItems, valueOrLabel, false);
    }

    /**
     * Recursive method used to find a SelectItem by its value.
     * @param fc FacesContext
     * @param component the current UI component to find value for
     * @param converter the converter for the select items
     * @param selectItems the List of SelectItems
     * @param valueOrLabel the input value/label to search for
     * @return either the SelectItem found or NULL if not found
     */
    protected SelectItem findSelectItemByValue(FacesContext fc, T component, Converter converter, List<SelectItem> selectItems,
                String valueOrLabel) {
        return findSelectItem(fc, component, converter, selectItems, valueOrLabel, true);
    }

    /**
     * Recursive method used to find a SelectItem by its value or label.
     * @param fc FacesContext
     * @param component the current UI component to find value for
     * @param converter the converter for the select items
     * @param selectItems the List of SelectItems
     * @param valueOrLabel the input value/label to search for
     * @param byValue true if searching by value false if by label
     * @return either the SelectItem found or NULL if not found
     */
    private SelectItem findSelectItem(FacesContext fc, T component, Converter converter, List<SelectItem> selectItems,
                String valueOrLabel, boolean byValue) {

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem item = selectItems.get(i);
            if (item instanceof SelectItemGroup) {
                SelectItemGroup selectItemGroup = (SelectItemGroup) item;
                if (selectItemGroup.getSelectItems() != null) {
                    SelectItem foundValue = findSelectItem(fc, component, converter,
                            Arrays.asList(selectItemGroup.getSelectItems()), valueOrLabel, byValue);
                    if (foundValue != null) {
                        return foundValue;
                    }
                }
            }
            else {
                String itemString;
                if (byValue) {
                    itemString = getOptionAsString(fc, component, converter, item.getValue());
                }
                else {
                    itemString = item.getLabel();
                }
                if (Objects.equals(valueOrLabel, itemString)) {
                    return item;
                }
            }
        }

        return null;
    }

    protected abstract String getSubmitParam(FacesContext context, T component);
}
