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

import org.primefaces.component.api.PrimeSelect;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.component.UISelectMany;
import jakarta.faces.context.FacesContext;

public abstract class SelectManyRenderer<T extends UISelectMany & PrimeSelect> extends SelectRenderer<T> {

    @Override
    public void decode(FacesContext context, T component) {
        if (!shouldDecode(component)) {
            return;
        }

        String submitParam = getSubmitParam(context, component);
        Map<String, String[]> params = context.getExternalContext().getRequestParameterValuesMap();

        String[] submittedValues = params.containsKey(submitParam) ? params.get(submitParam) : new String[0];
        List<String> validSubmittedValues = validateSubmittedValues(context, component, (Object[]) getValues(component), submittedValues);
        component.setSubmittedValue(validSubmittedValues.toArray(new String[validSubmittedValues.size()]));

        decodeBehaviors(context, component);
    }

    protected Object getValues(T component) {
        Object value = component.getValue();

        if (value != null) {
            if (value instanceof Collection) {
                return ((Collection) value).toArray();
            }
            else if (value.getClass().isArray()) {
                return value;
            }
            else {
                throw new FacesException("Value of '" + component.getClientId() + "'must be an array or a collection");
            }
        }

        return null;
    }

    protected Object getSubmittedValues(T component) {
        return component.getSubmittedValue();
    }

    protected abstract String getSubmitParam(FacesContext context, T component);
}
