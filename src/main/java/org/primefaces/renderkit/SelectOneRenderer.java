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

import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;

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
        String[] submittedValues = validateSubmittedValues(context, selectOne, (Object[]) getValues(selectOne), submittedValue);
        submittedValue = submittedValues.length == 0 ? submittedValue : submittedValues[0];
        selectOne.setSubmittedValue(submittedValue);

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

    protected abstract String getSubmitParam(FacesContext context, UISelectOne selectOne);
}
