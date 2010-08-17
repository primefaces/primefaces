/*
 * Copyright 2010 Prime Technology.
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

import java.awt.Color;
import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.CoreRenderer;

public class ColorPickerRenderer extends CoreRenderer {

	private final static String DEFAULT_PICKER_THUMB = "yui/colorpicker/assets/picker_thumb.png";
	private final static String DEFAULT_HUE_THUMB = "yui/colorpicker/assets/hue_thumb.png";

    @Override
	public void decode(FacesContext facesContext, UIComponent component) {
		ColorPicker colorPicker = (ColorPicker) component;
		String paramName = colorPicker.getClientId(facesContext) + "_input";
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		if(params.containsKey(paramName)) {
			String submittedValue = facesContext.getExternalContext().getRequestParameterMap().get(paramName);

			colorPicker.setSubmittedValue(submittedValue);
		}
	}

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ColorPicker colorPicker = (ColorPicker) component;

		encodeMarkup(facesContext, colorPicker);
		encodeScript(facesContext, colorPicker);
	}

	protected void encodeMarkup(FacesContext facesContext, ColorPicker colorPicker) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = colorPicker.getClientId(facesContext);
		String value = getValueAsString(facesContext,colorPicker);
		String buttonId = clientId + "_button";
		String inputId = clientId + "_input";

		renderIE6Fix(writer);
		
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId, null);

		//Button
		writer.startElement("button", null);
		writer.writeAttribute("id", buttonId, null);
		writer.writeAttribute("name", buttonId, null);
		writer.writeAttribute("type", "button", null);
		writer.write("<em id=\""+ clientId + "_livePreview\" style=\"overflow:hidden;width:1em;height:1em;display:block;border:solid 1px #000;text-indent:1em;white-space:nowrap;");
		if(value != null) {
			writer.write("background-color:rgb(" + value + ");");
		}
		writer.write("\">Live Preview</em>");

		writer.endElement("button");
		
		//Input
		writer.startElement("input", null);
		writer.writeAttribute("id", inputId, null);
		writer.writeAttribute("name", inputId, null);
		writer.writeAttribute("type", "hidden", null);
		if(value != null) {
			writer.writeAttribute("value", value, null);
		}
		writer.endElement("input");
		
		//Dialog
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_dialog", null);
		if(colorPicker.getHeader() != null) {
			writer.writeAttribute("title", colorPicker.getHeader(), null);
		}
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_cpContainer", null);
		writer.writeAttribute("class", "yui-picker", null);
		writer.endElement("div");
		
		writer.endElement("div");
		
		writer.endElement("span");
	}

	private void renderIE6Fix(ResponseWriter writer) throws IOException {
		writer.write("<!--[if lt IE 7]>\n");
		writer.startElement("style", null);
		writer.writeAttribute("type", "text/css", null);
		writer.write("\n* html .yui-picker-bg {\n");
		writer.write("background-image: none;\n");
		writer.write("}\n");
		writer.endElement("style");
		writer.write("<![endif]-->");
	}
	
	protected void encodeScript(FacesContext facesContext, ColorPicker colorPicker) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		String clientId = colorPicker.getClientId(facesContext);
		String value = getValueAsString(facesContext,colorPicker);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(colorPicker.resolveWidgetVar() + " = new PrimeFaces.widget.ColorPicker('" + clientId + "', {");
		writer.write("images: {");
		writer.write("PICKER_THUMB:'" + getResourceRequestPath(facesContext, DEFAULT_PICKER_THUMB) + "'");
		writer.write(",HUE_THUMB:'" + getResourceRequestPath(facesContext, DEFAULT_HUE_THUMB) + "'");
		writer.write("}");
		
		if(value != null) writer.write(",initialValue:[" + value + "]");
		if(!colorPicker.isShowControls()) writer.write(",showcontrols: false");
		if(!colorPicker.isShowHexControls()) writer.write(",showhexcontrols: false");
		if(!colorPicker.isShowHexSummary()) writer.write(",showhexsummary: false");
		if(colorPicker.isShowHsvControls()) writer.write(",showhsvcontrols: true");
		if(!colorPicker.isShowRGBControls()) writer.write(",showrgbcontrols: false");
		if(!colorPicker.isShowWebSafe()) writer.write(",showwebsafe: false");
		
		writer.write("});");

		writer.endElement("script");
	}

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object value) throws ConverterException {
		try {
			String submittedValue = (String) value;
	
			if(isValueBlank(submittedValue))
				return null;

			ColorPicker colorPicker = (ColorPicker) component;
			
			// Delegate to user supplied converter if defined
			if(colorPicker.getConverter() != null) {
				return colorPicker.getConverter().getAsObject(context, colorPicker, submittedValue);
			} else {
				String[] rgb = submittedValue.split(",");

				return new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
			}
		} catch (Exception e) {
			throw new ConverterException(e);
		}
	}
	
	protected String getValueAsString(FacesContext facesContext, ColorPicker colorPicker) {
		Object value = colorPicker.getValue();
		
		if(value == null)
			return null;
		else {
			if(colorPicker.getConverter() != null)
				return colorPicker.getConverter().getAsString(facesContext, colorPicker, value);
			else {
				Color color = (Color) value;

				return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
			}		
		}
	}
}