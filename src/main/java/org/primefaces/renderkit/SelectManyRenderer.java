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

import java.util.Collection;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;

public abstract class SelectManyRenderer extends SelectRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        UISelectMany selectMany = (UISelectMany) component;
        if (!shouldDecode(selectMany)) {
            return;
        }

        String submitParam = getSubmitParam(context, selectMany);
        Map<String, String[]> params = context.getExternalContext().getRequestParameterValuesMap();

        String[] submittedValues = params.containsKey(submitParam) ? params.get(submitParam) : new String[0];
        submittedValues = validateSubmittedValues(context, selectMany, (Object[]) getValues(selectMany), submittedValues);
        selectMany.setSubmittedValue(submittedValues);

        decodeBehaviors(context, selectMany);
    }

    protected Object getValues(UIComponent component) {
        UISelectMany selectMany = (UISelectMany) component;
        Object value = selectMany.getValue();

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

    protected Object getSubmittedValues(UIComponent component) {
        UISelectMany select = (UISelectMany) component;

        return (Object[]) select.getSubmittedValue();
    }

    protected abstract String getSubmitParam(FacesContext context, UISelectMany selectMany);
}
