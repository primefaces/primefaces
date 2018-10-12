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
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.component.api.RTLAware;

import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

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

    protected boolean isDisabled(UIInput component) {
        return Boolean.parseBoolean(String.valueOf(component.getAttributes().get("disabled")));
    }

    protected boolean isReadOnly(UIInput component) {
        return Boolean.parseBoolean(String.valueOf(component.getAttributes().get("readonly")));
    }

    protected boolean shouldDecode(UIInput component) {
        return !isDisabled(component) && !isReadOnly(component);
    }

    public <T extends UIComponent & RTLAware> void renderRTLDirection(FacesContext context, T component) throws IOException {
        if (ComponentUtils.isRTL(context, component)) {
            context.getResponseWriter().writeAttribute("dir", "rtl", null);
        }
    }

    /**
     * Adds "aria-required" if the component is required.
     *
     * @param context the {@link FacesContext}
     * @param component the {@link UIInput} component to add attributes for
     * @throws IOException if any error occurs writing the response
     */
    protected void renderARIARequired(FacesContext context, UIInput component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        if (component.isRequired()) {
            writer.writeAttribute(HTML.ARIA_REQUIRED, "true", null);
        }
    }

    /**
     * Adds "aria-invalid" if the component is invalid.
     *
     * @param context the {@link FacesContext}
     * @param component the {@link UIInput} component to add attributes for
     * @throws IOException if any error occurs writing the response
     */
    protected void renderARIAInvalid(FacesContext context, UIInput component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        if (!component.isValid()) {
            writer.writeAttribute(HTML.ARIA_INVALID, "true", null);
        }
    }

    /**
     * Adds the following accessibility attributes to an HTML DOM element.
     * <pre>
     * "aria-required" if the component is required
     * "aria-invalid" if the component is invalid
     * "aria-labelledby" if the component has a labelledby attribute
     * "disabled" and "aria-disabled" if the component is disabled
     * "readonly" and "aria-readonly" if the component is readonly
     * </pre>
     * @param context the {@link FacesContext}
     * @param component the {@link UIInput} component to add attributes for
     * @throws IOException if any error occurs writing the response
     */
    protected void renderAccessibilityAttributes(FacesContext context, UIInput component) throws IOException {
        renderAccessibilityAttributes(context, component, isDisabled(component), isReadOnly(component));
    }

    protected void renderAccessibilityAttributes(FacesContext context, UIInput component, boolean disabled, boolean readonly)
                throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        renderARIARequired(context, component);
        renderARIAInvalid(context, component);

        String labelledBy = (String) component.getAttributes().get("labelledby");
        if (labelledBy != null) {
            writer.writeAttribute(HTML.ARIA_LABELLEDBY, labelledBy, null);
        }

        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
            writer.writeAttribute(HTML.ARIA_DISABLED, "true", null);
        }

        if (readonly) {
            writer.writeAttribute("readonly", "readonly", null);
            writer.writeAttribute(HTML.ARIA_READONLY, "true", null);
        }
    }

    /**
     * Adds ARIA attributes if the component is "role=combobox".
     *
     * @param context the {@link FacesContext}
     * @param component the {@link UIInput} component to add attributes for
     * @throws IOException if any error occurs writing the response
     * @see https://www.w3.org/TR/wai-aria-practices/#combobox
     */
    protected void renderARIACombobox(FacesContext context, UIInput component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.writeAttribute("role", "combobox", null);
        writer.writeAttribute(HTML.ARIA_HASPOPUP, "true", null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, "false", null);
    }

}
