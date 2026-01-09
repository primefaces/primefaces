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
package org.primefaces.component.colorpicker;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.faces.component.UINamingContainer;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = ColorPicker.DEFAULT_RENDERER, componentFamily = ColorPicker.COMPONENT_FAMILY)
public class ColorPickerRenderer extends InputRenderer<ColorPicker> {

    private static final Pattern COLOR_PATTERN = Pattern.compile(
            "^#(?:[\\da-f]{3}){1,2}$|^#(?:[\\da-f]{4}){1,2}$|(rgb|hsl)a?\\((\\s*-?\\d+%?\\s*,){2}(\\s*-?\\d+%?\\s*)\\)"
            + "|(rgb|hsl)a?\\((\\s*-?\\d+%?\\s*,){3}\\s*(0|(0?\\.\\d+)|1)\\)", Pattern.CASE_INSENSITIVE);

    @Override
    public void decode(FacesContext context, ColorPicker component) {
        if (!shouldDecode(component)) {
            return;
        }
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String paramName = component.getClientId(context);
        boolean inline = !"popup".equalsIgnoreCase(component.getMode());
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
            component.setSubmittedValue(submittedValue);
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, ColorPicker component) throws IOException {
        String value = ComponentUtils.getValueToRender(context, component);

        String clientId = component.getClientId(context);
        String uuid = clientId.replaceAll(Character.toString(UINamingContainer.getSeparatorChar(context)), "-");
        encodeMarkup(context, component, value, uuid);
        encodeScript(context, component, value, uuid);
    }

    protected void encodeMarkup(FacesContext context, ColorPicker component, String value, String uuid) throws IOException {
        if ("popup".equalsIgnoreCase(component.getMode())) {
            encodePopup(context, component, value, uuid);
        }
        else {
            encodeInline(context, component, value, uuid);
        }
    }

    protected void encodePopup(FacesContext context, ColorPicker component, String value, String uuid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String styleClass = getStyleClassBuilder(context)
                .add(createStyleClass(component, ColorPicker.POPUP_STYLE_CLASS))
                .add(ComponentUtils.isRTL(context, component), "clr-rtl")
                .add(uuid)
                .build();

        //Input
        writer.startElement("input", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        String onchange = component.getOnchange();
        if (!isValueBlank(onchange)) {
            writer.writeAttribute("onchange", onchange, null);
        }

        renderAccessibilityAttributes(context, component);
        renderRTLDirection(context, component);
        renderPassThruAttributes(context, component, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, component, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, component);

        if (value != null) {
            writer.writeAttribute("value", value, null);
        }
        writer.endElement("input");
    }

    protected void encodeInline(FacesContext context, ColorPicker component, String value, String uuid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        //Input
        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("class", createStyleClass(component, ColorPicker.INLINE_STYLE_CLASS) + " " + uuid, null);

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        String onchange = component.getOnchange();
        if (!isValueBlank(onchange)) {
            writer.writeAttribute("onchange", onchange, null);
        }
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ColorPicker component, String value, String uuid) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("ColorPicker", component)
                .attr("instance", uuid)
                .attr("locale", component.calculateLocale(context).toString())
                .attr("mode", component.getMode())
                .attr("defaultColor", value, null)
                .attr("theme", component.getTheme(), "default")
                .attr("themeMode", component.getThemeMode(), "auto")
                .attr("format", component.getFormat(), "hex")
                .attr("formatToggle", component.isFormatToggle(), false)
                .attr("clearButton", component.isClearButton(), false)
                .attr("closeButton", component.isCloseButton(), false)
                .attr("alpha", component.isAlpha(), true)
                .attr("forceAlpha", component.isForceAlpha(), false)
                .attr("swatchesOnly", component.isSwatchesOnly(), false)
                .attr("focusInput", component.isFocusInput(), true)
                .attr("selectInput", component.isSelectInput(), false);

        String swatchProp = component.getSwatches();
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

        encodeClientBehaviors(context, component);

        wb.finish();
    }

}
