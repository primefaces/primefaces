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
package org.primefaces.component.knob;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.awt.Color;
import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Knob.DEFAULT_RENDERER, componentFamily = Knob.COMPONENT_FAMILY)
public class KnobRenderer extends CoreRenderer<Knob> {

    public static final String RENDERER_TYPE = "org.primefaces.component.KnobRenderer";

    protected String colorToHex(Color color) {
        String red = Integer.toHexString(color.getRed());
        if (red.length() < 2) {
            red = "0" + red;
        }

        String blue = Integer.toHexString(color.getBlue());
        if (blue.length() < 2) {
            blue = "0" + blue;
        }

        String green = Integer.toHexString(color.getGreen());
        if (green.length() < 2) {
            green = "0" + green;
        }

        return "#" + red + green + blue;
    }

    @Override
    public void decode(FacesContext context, Knob component) {
        decodeBehaviors(context, component);

        String submittedValue = context.getExternalContext().getRequestParameterMap().get(component.getClientId(context) + "_hidden");

        if (!LangUtils.isEmpty(submittedValue)) {
            int submittedInt = Integer.parseInt(submittedValue);
            if (submittedInt < component.getMin() || submittedInt > component.getMax()) {
                return;
            }
        }

        component.setSubmittedValue(submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, Knob component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    private void encodeMarkup(FacesContext context, Knob component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        Object value = component.getValue() != null ? component.getValue() : 0;

        writer.startElement("input", component);
        writer.writeAttribute("id", component.getClientId(), null);
        writer.writeAttribute("name", component.getClientId(), null);
        writer.writeAttribute("disabled", true, null);
        writer.writeAttribute("value", value.toString(), null);
        writer.writeAttribute("data-min", component.getMin(), null);
        writer.writeAttribute("data-step", component.getStep(), null);
        writer.writeAttribute("data-max", component.getMax(), null);
        writer.writeAttribute("data-displayInput", Boolean.toString(component.isShowLabel()), null);
        writer.writeAttribute("data-readOnly", Boolean.toString(component.isDisabled()), null);
        writer.writeAttribute("data-cursor", Boolean.toString(component.isCursor()), null);
        writer.writeAttribute("data-linecap", component.getLineCap(), "butt");

        if (component.getThickness() != null) {
            writer.writeAttribute("data-thickness", component.getThickness(), null);
        }

        if (component.getWidth() != null) {
            writer.writeAttribute("data-width", component.getWidth().toString(), null);
        }

        if (component.getHeight() != null) {
            writer.writeAttribute("data-height", component.getHeight().toString(), null);
        }

        writer.writeAttribute("class", "knob", null);

        writer.endElement("input");

        renderHiddenInput(context, component.getClientId() + "_hidden", value.toString(), component.isDisabled());
    }

    private void encodeScript(FacesContext context, Knob component) throws IOException {
        String styleClass = component.getStyleClass() != null ? "ui-knob " + component.getStyleClass() : "ui-knob";

        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("Knob", component);
        wb.attr("labelTemplate", component.getLabelTemplate())
                .attr("colorTheme", component.getColorTheme())
                .attr("styleClass", styleClass)
                .callback("onchange", "function(value)", component.getOnchange());

        if (component.getForegroundColor() != null) {
            String fg;
            if (component.getForegroundColor() instanceof Color) {
                fg = colorToHex((Color) component.getForegroundColor());
            }
            else {
                fg = component.getForegroundColor().toString();
            }
            wb.attr("fgColor", fg);
        }

        if (component.getBackgroundColor() != null) {
            String bg;
            if (component.getBackgroundColor() instanceof Color) {
                bg = colorToHex((Color) component.getBackgroundColor());
            }
            else {
                bg = component.getBackgroundColor().toString();
            }
            wb.attr("bgColor", bg);
        }
        encodeClientBehaviors(context, component);

        wb.finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        if (submittedValue == null) {
            submittedValue = 0;
        }
        try {
            return ((submittedValue instanceof Integer) ? submittedValue : Integer.parseInt(submittedValue.toString()));
        }
        catch (NumberFormatException e) {
            throw new ConverterException(e);
        }
    }

}
