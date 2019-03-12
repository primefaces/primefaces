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

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.RTLAware;

import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;

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
        if (component.isRequired()) {
            ResponseWriter writer = context.getResponseWriter();
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
        if (!component.isValid()) {
            ResponseWriter writer = context.getResponseWriter();
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

        if (component instanceof InputHolder) {
            InputHolder inputHolder = ((InputHolder) component);
            String labelledBy = inputHolder.getLabelledBy();
            if (!LangUtils.isValueBlank(labelledBy)) {
                writer.writeAttribute(HTML.ARIA_LABELLEDBY, labelledBy, null);
            }
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
