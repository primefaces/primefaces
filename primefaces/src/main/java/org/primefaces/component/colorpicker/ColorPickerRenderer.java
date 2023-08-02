/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.component.colorpicker;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.*;

public class ColorPickerRenderer extends InputRenderer {

    private static final Pattern COLOR_PATTERN = Pattern.compile(
            "^#(?:[\\da-f]{3}){1,2}$|^#(?:[\\da-f]{4}){1,2}$|(rgb|hsl)a?\\((\\s*-?\\d+%?\\s*,){2}(\\s*-?\\d+%?\\s*)\\)"
            + "|(rgb|hsl)a?\\((\\s*-?\\d+%?\\s*,){3}\\s*(0|(0?\\.\\d+)|1)\\)", Pattern.CASE_INSENSITIVE);

    @Override
    public void decode(FacesContext context, UIComponent component) {
        ColorPicker colorPicker = (ColorPicker) component;
        if (!shouldDecode(colorPicker)) {
            return;
        }
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String paramName = colorPicker.getClientId(context);
        boolean inline = !"popup".equalsIgnoreCase(colorPicker.getMode());
        if (inline) {
            paramName = paramName + "_color";
            if (!params.containsKey(paramName)) {
                paramName = "clr-color-value";
            }
        }

        if (params.containsKey(paramName)) {
            String submittedValue = params.get(paramName);
            if (!COLOR_PATTERN.matcher(submittedValue).matches()) {
                submittedValue = Constants.EMPTY_STRING;
            }
            colorPicker.setSubmittedValue(ComponentUtils.getConvertedValue(context, component, submittedValue));
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ColorPicker colorPicker = (ColorPicker) component;
        Converter<Object> converter = ComponentUtils.getConverter(context, component);
        String value;
        if (converter != null) {
            value = converter.getAsString(context, component, colorPicker.getValue());
        }
        else {
            value = (String) colorPicker.getValue();
        }

        String clientId = colorPicker.getClientId(context);
        String uuid = clientId.replaceAll(Character.toString(UINamingContainer.getSeparatorChar(context)), "-");
        encodeMarkup(context, colorPicker, value, uuid);
        encodeScript(context, colorPicker, value, uuid);
    }

    protected void encodeMarkup(FacesContext context, ColorPicker colorPicker, String value, String uuid) throws IOException {
        if ("popup".equalsIgnoreCase(colorPicker.getMode())) {
            encodePopup(context, colorPicker, value, uuid);
        }
        else {
            encodeInline(context, colorPicker, value, uuid);
        }
    }

    protected void encodePopup(FacesContext context, ColorPicker colorPicker, String value, String uuid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = colorPicker.getClientId(context);
        String styleClass = getStyleClassBuilder(context)
                .add(createStyleClass(colorPicker, ColorPicker.POPUP_STYLE_CLASS))
                .add(ComponentUtils.isRTL(context, colorPicker), "clr-rtl")
                .add(uuid)
                .build();

        //Input
        writer.startElement("input", colorPicker);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (colorPicker.getStyle() != null) {
            writer.writeAttribute("style", colorPicker.getStyle(), "style");
        }

        String onchange = colorPicker.getOnchange();
        if (!isValueBlank(onchange)) {
            writer.writeAttribute("onchange", onchange, null);
        }

        renderAccessibilityAttributes(context, colorPicker);
        renderRTLDirection(context, colorPicker);
        renderPassThruAttributes(context, colorPicker, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, colorPicker, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, colorPicker);

        if (value != null) {
            writer.writeAttribute("value", value, null);
        }
        writer.endElement("input");
    }

    protected void encodeInline(FacesContext context, ColorPicker colorPicker, String value, String uuid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = colorPicker.getClientId(context);

        //Input
        writer.startElement("div", colorPicker);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("class", createStyleClass(colorPicker, ColorPicker.INLINE_STYLE_CLASS) + " " + uuid, null);

        if (colorPicker.getStyle() != null) {
            writer.writeAttribute("style", colorPicker.getStyle(), "style");
        }

        String onchange = colorPicker.getOnchange();
        if (!isValueBlank(onchange)) {
            writer.writeAttribute("onchange", onchange, null);
        }
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ColorPicker colorPicker, String value, String uuid) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("ColorPicker", colorPicker)
                .attr("instance", uuid)
                .attr("locale", colorPicker.calculateLocale(context).toString())
                .attr("mode", colorPicker.getMode())
                .attr("defaultColor", value, null)
                .attr("theme", colorPicker.getTheme())
                .attr("theme", colorPicker.getTheme(), "default")
                .attr("themeMode", colorPicker.getThemeMode(), "auto")
                .attr("format", colorPicker.getFormat(), "hex")
                .attr("formatToggle", colorPicker.isFormatToggle(), false)
                .attr("clearButton", colorPicker.isClearButton(), false)
                .attr("closeButton", colorPicker.isCloseButton(), false)
                .attr("alpha", colorPicker.isAlpha(), true)
                .attr("forceAlpha", colorPicker.isForceAlpha(), false)
                .attr("swatchesOnly", colorPicker.isSwatchesOnly(), false)
                .attr("focusInput", colorPicker.isFocusInput(), true)
                .attr("selectInput", colorPicker.isSelectInput(), false);

        String swatchProp = colorPicker.getSwatches();
        if (LangUtils.isNotBlank(swatchProp)) {
            String[] swatches = swatchProp.split(",");
            wb.append(",swatches:[");
            for (int i = 0; i < swatches.length; i++) {
                String swatch = swatches[i];
                if (i != 0) {
                    wb.append(",");
                }

                wb.append("\"" + EscapeUtils.forJavaScript(swatch) + "\"");
            }
            wb.append("]");
        }

        encodeClientBehaviors(context, colorPicker);

        wb.finish();
    }

}
