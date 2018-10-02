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
package org.primefaces.component.colorpicker;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class ColorPickerRenderer extends InputRenderer {

    private static final Pattern COLOR_HEX_PATTERN = Pattern.compile("([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");

    @Override
    public void decode(FacesContext context, UIComponent component) {
        ColorPicker colorPicker = (ColorPicker) component;
        if (!shouldDecode(colorPicker)) {
            return;
        }
        String paramName = colorPicker.getClientId(context) + "_input";
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (params.containsKey(paramName)) {
            String submittedValue = params.get(paramName);

            if (!COLOR_HEX_PATTERN.matcher(submittedValue).matches()) {
                return;
            }

            Converter converter = colorPicker.getConverter();
            if (converter != null) {
                colorPicker.setSubmittedValue(
                        converter.getAsObject(context, component, submittedValue));
            }
            else {
                colorPicker.setSubmittedValue(submittedValue);
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ColorPicker colorPicker = (ColorPicker) component;
        Converter converter = colorPicker.getConverter();
        String value;
        if (converter != null) {
            value = converter.getAsString(context, component, colorPicker.getValue());
        }
        else {
            value = (String) colorPicker.getValue();
        }

        encodeMarkup(context, colorPicker, value);
        encodeScript(context, colorPicker, value);
    }

    protected void encodeMarkup(FacesContext context, ColorPicker colorPicker, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = colorPicker.getClientId(context);
        String inputId = clientId + "_input";
        boolean isPopup = colorPicker.getMode().equals("popup");
        String styleClass = colorPicker.getStyleClass();
        styleClass = styleClass == null ? ColorPicker.STYLE_CLASS : ColorPicker.STYLE_CLASS + " " + styleClass;

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (colorPicker.getStyle() != null) {
            writer.writeAttribute("style", colorPicker.getStyle(), "style");
        }

        if (isPopup) {
            encodeButton(context, colorPicker, clientId, value);
        }
        else {
            encodeInline(context, colorPicker, clientId);
        }

        //Input
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "hidden", null);

        String onchange = colorPicker.getOnchange();
        if (!isValueBlank(onchange)) {
            writer.writeAttribute("onchange", onchange, null);
        }

        renderPassThruAttributes(context, colorPicker, null);

        if (value != null) {
            writer.writeAttribute("value", value, null);
        }
        writer.endElement("input");

        writer.endElement("span");
    }

    protected void encodeButton(FacesContext context, ColorPicker colorPicker, String clientId, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("button", null);
        writer.writeAttribute("id", clientId + "_button", null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_ONLY_BUTTON_CLASS, null);
        renderAccessibilityAttributes(context, colorPicker);

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);

        writer.write("<span id=\"" + clientId + "_livePreview\" "
                + "style=\"overflow:hidden;width:1em;height:1em;display:block;border:solid 1px #000;text-indent:1em;white-space:nowrap;");
        if (value != null) {
            writer.write("background-color:#" + value);
        }
        writer.write("\">Live Preview</span>");

        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeInline(FacesContext context, ColorPicker colorPicker, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_inline", "id");
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ColorPicker colorPicker, String value) throws IOException {
        String clientId = colorPicker.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("ColorPicker", colorPicker.resolveWidgetVar(), clientId)
                .attr("mode", colorPicker.getMode())
                .attr("color", value, null);

        encodeClientBehaviors(context, colorPicker);

        wb.finish();
    }
}
