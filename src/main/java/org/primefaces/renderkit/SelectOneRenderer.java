/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

public abstract class SelectOneRenderer extends SelectRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        UISelectOne selectOne = (UISelectOne) component;
        if (!shouldDecode(selectOne)) {
            return;
        }

        String clientId = getSubmitParam(context, selectOne);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        String submittedValue = params.containsKey(clientId) ? params.get(clientId) : "";
        List<String> validSubmittedValues = validateSubmittedValues(context, selectOne, (Object[]) getValues(selectOne), submittedValue);
        selectOne.setSubmittedValue(validSubmittedValues.isEmpty() || validSubmittedValues.contains(submittedValue)
                ? submittedValue
                : validSubmittedValues.get(0));

        decodeBehaviors(context, selectOne);
    }

    protected Object getValues(UISelectOne selectOne) {
        Object value = selectOne.getValue();

        if (value != null) {
            return new Object[]{value};
        }

        return null;
    }

    protected Object getSubmittedValues(UIComponent component) {
        UISelectOne select = (UISelectOne) component;

        Object val = select.getSubmittedValue();
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
    protected SelectItem findSelectItemByLabel(FacesContext fc, UIComponent component, Converter converter, List<SelectItem> selectItems,
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
    protected SelectItem findSelectItemByValue(FacesContext fc, UIComponent component, Converter converter, List<SelectItem> selectItems,
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
    private SelectItem findSelectItem(FacesContext fc, UIComponent component, Converter converter, List<SelectItem> selectItems,
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

    protected abstract String getSubmitParam(FacesContext context, UISelectOne selectOne);
}
