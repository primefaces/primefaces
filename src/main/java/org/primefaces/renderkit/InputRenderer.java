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

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.component.api.RTLAware;

import org.primefaces.util.ComponentUtils;

public abstract class InputRenderer extends CoreRenderer {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Converter converter = ComponentUtils.getConverter(context, component);

        if (converter != null) {
            String convertableValue = submittedValue == null ? null : submittedValue.toString();
            return converter.getAsObject(context, component, convertableValue);
        }
        else {
            return submittedValue;
        }
    }

    protected boolean shouldDecode(UIComponent component) {
        boolean disabled = Boolean.parseBoolean(String.valueOf(component.getAttributes().get("disabled")));
        boolean readonly = Boolean.parseBoolean(String.valueOf(component.getAttributes().get("readonly")));

        return !disabled && !readonly;
    }

    public <T extends UIComponent & RTLAware> void renderRTLDirection(FacesContext context, T component) throws IOException {
        if (ComponentUtils.isRTL(context, component)) {
            context.getResponseWriter().writeAttribute("dir", "rtl", null);
        }
    }
}
