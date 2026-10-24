/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
        String clientId = component.getClientId(context);
        Object value = component.getValue() != null ? component.getValue() : 0;
        String styleClass = getStyleClassBuilder(context)
                .add(Knob.STYLE_CLASS)
                .add(component.getStyleClass())
                .add(component.isDisabled(), "ui-state-disabled")
                .build();

        // the knob is a square: the missing one of width and height follows the other (aspect-ratio of .ui-knob)
        String width = toCssLength(component.getWidth());
        String height = toCssLength(component.getHeight());
        String style = getStyleBuilder(context)
                .add(width != null, "width", width)
                .add(width == null && height != null, "width", "auto")
                .add(height != null, "height", height)
                .add(component.getForegroundColor() != null, "--p-knob-value-background", toCssColor(component.getForegroundColor()))
                .add(component.getBackgroundColor() != null, "--p-knob-range-background", toCssColor(component.getBackgroundColor()))
                .add(component.getStyle())
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (LangUtils.isNotBlank(style)) {
            writer.writeAttribute("style", style, null);
        }

        renderHiddenInput(context, clientId + "_hidden", value.toString(), component.isDisabled());

        writer.endElement("div");
    }

    private void encodeScript(FacesContext context, Knob component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("Knob", component)
                .attr("min", component.getMin())
                .attr("max", component.getMax())
                .attr("step", component.getStep())
                .attr("disabled", component.isDisabled(), false)
                .attr("showLabel", component.isShowLabel(), true)
                .attr("cursor", component.isCursor(), false)
                .attr("lineCap", component.getLineCap(), "butt")
                .attr("labelTemplate", component.getLabelTemplate(), "{value}")
                .callback("onchange", "function(value)", component.getOnchange());

        if (component.getThickness() != null) {
            wb.attr("thickness", component.getThickness());
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    /**
     * Converts a width or height to a CSS length: numbers are pixels.
     *
     * @param size the width or height, a number or a CSS length like {@code 50%}
     * @return the CSS length, {@code null} if the size is not set
     */
    protected String toCssLength(Object size) {
        if (size == null || LangUtils.isBlank(size.toString())) {
            return null;
        }
        String length = size.toString().trim();
        return length.matches("\\d+(\\.\\d+)?") ? length + "px" : length;
    }

    /**
     * Converts a color to a CSS color.
     *
     * @param color a {@link Color} or a CSS color
     * @return the CSS color, {@code null} if the color is not set
     */
    protected String toCssColor(Object color) {
        if (color == null) {
            return null;
        }
        return color instanceof Color ? colorToHex((Color) color) : color.toString();
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
