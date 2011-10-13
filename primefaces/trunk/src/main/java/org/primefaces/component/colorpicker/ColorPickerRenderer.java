/*
 * Copyright 2009-2011 Prime Technology.
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class ColorPickerRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		ColorPicker colorPicker = (ColorPicker) component;
		String paramName = colorPicker.getClientId(context) + "_input";
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();

		if(params.containsKey(paramName)) {
			String submittedValue = params.get(paramName);

			colorPicker.setSubmittedValue(submittedValue);
		}
	}

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ColorPicker colorPicker = (ColorPicker) component;

		encodeMarkup(context, colorPicker);
		encodeScript(context, colorPicker);
	}

	protected void encodeMarkup(FacesContext context, ColorPicker colorPicker) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = colorPicker.getClientId(context);
        String inputId = clientId + "_input";
        String buttonId = clientId + "_button";
        String value = (String) colorPicker.getValue();
        boolean isPopup = colorPicker.getMode().equals("popup");
        String styleClass = colorPicker.getStyleClass();
        styleClass = styleClass == null ? ColorPicker.STYLE_CLASS : ColorPicker.STYLE_CLASS + " " + styleClass;

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(colorPicker.getStyle() != null)
            writer.writeAttribute("style", colorPicker.getStyle(), "style");

        if(isPopup) {
            //Button
            writer.startElement("button", null);
            writer.writeAttribute("id", buttonId, null);
            writer.writeAttribute("name", buttonId, null);
            writer.writeAttribute("type", "button", null);
            writer.write("<span id=\""+ clientId + "_livePreview\" style=\"overflow:hidden;width:1em;height:1em;display:block;border:solid 1px #000;text-indent:1em;white-space:nowrap;");
            if(value != null) {
                writer.write("background-color:#" + value);
            }
            writer.write("\">Live Preview</span>");

            writer.endElement("button");
        } 
        else {
            writer.startElement("div", null);
            writer.writeAttribute("id", clientId + "_inline", "id");
            writer.endElement("div");
        }

        //Input
		writer.startElement("input", null);
		writer.writeAttribute("id", inputId, null);
		writer.writeAttribute("name", inputId, null);
		writer.writeAttribute("type", "hidden", null);
		if(value != null) {
			writer.writeAttribute("value", value, null);
		}
		writer.endElement("input");

        writer.endElement("span");
    }

    protected void encodeScript(FacesContext context, ColorPicker colorPicker) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = colorPicker.getClientId(context);
        String value = (String) colorPicker.getValue();
        String effect = colorPicker.getEffect();

		startScript(writer, clientId);

        writer.write("$(function() {");
        
		writer.write(colorPicker.resolveWidgetVar() + " = new PrimeFaces.widget.ColorPicker('" + clientId + "', {");
        writer.write("mode:'" + colorPicker.getMode() + "'");

        if(value != null) writer.write(",color:'" + value + "'");
        if(!effect.equals("none")) {
            writer.write(",effect:'" + effect + "'");
            writer.write(",effectSpeed:'" + colorPicker.getEffectSpeed() + "'");
        }
        if(colorPicker.getZindex() != Integer.MAX_VALUE) writer.write(",zindex:" + colorPicker.getZindex());

        writer.write("});});");

        endScript(writer);
    }
}