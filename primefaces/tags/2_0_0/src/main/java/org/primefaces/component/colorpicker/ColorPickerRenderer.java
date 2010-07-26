/*
 * Copyright 2009 Prime Technology.
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.resource.ResourceUtils;

public class ColorPickerRenderer extends CoreRenderer {

	private final static String DEFAULT_PICKER_THUMB = "/yui/colorpicker/assets/picker_thumb.png";
	private final static String DEFAULT_HUE_THUMB = "/yui/colorpicker/assets/hue_thumb.png";

	public void decode(FacesContext facesContext, UIComponent component) {
		String paramName = component.getClientId(facesContext) + "_input";
		String submittedValue = facesContext.getExternalContext().getRequestParameterMap().get(paramName);

		((ColorPicker) component).setSubmittedValue(submittedValue);
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ColorPicker colorPicker = (ColorPicker) component;

		encodeScript(facesContext, colorPicker);
		encodeMarkup(facesContext, colorPicker);
	}

	protected void encodeScript(FacesContext facesContext, ColorPicker colorPicker) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		String clientId = colorPicker.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, colorPicker);
		String rgb = getValueAsString(facesContext,colorPicker);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");

		writer.write(var + " = new PrimeFaces.widget.ColorPicker('" + clientId + "', {");
		writer.write("images: {");
		writer.write("PICKER_THUMB:'" + ResourceUtils.getResourceURL(facesContext, DEFAULT_PICKER_THUMB) + "'");
		writer.write(",HUE_THUMB:'" + ResourceUtils.getResourceURL(facesContext, DEFAULT_HUE_THUMB) + "'");
		writer.write("}");

		if(!colorPicker.isShowControls()) writer.write(",showcontrols: false");
		if(!colorPicker.isShowHexControls()) writer.write(",showhexcontrols: false");
		if(!colorPicker.isShowHexSummary()) writer.write(",showhexsummary: false");
		if(colorPicker.isShowHsvControls()) writer.write(",showhsvcontrols: true");
		if(!colorPicker.isShowRGBControls()) writer.write(",showrgbcontrols: false");
		if(!colorPicker.isShowWebSafe()) writer.write(",showwebsafe: false");
		writer.write("});\n");

		if(rgb != null)
			writer.write(var + ".setValue([" + rgb + "], false);\n");

		writer.write("});\n;");

		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext facesContext, ColorPicker colorPicker) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = colorPicker.getClientId(facesContext);

		renderIE6Fix(writer);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		
			encodeButtonMarkup(facesContext, colorPicker, clientId);
			encodeHiddenField(facesContext, colorPicker, clientId);

			writer.startElement("div", null);
			writer.writeAttribute("id", clientId + "_overlay", null);
			writer.writeAttribute("class", "yui-picker-panel", null);

				writer.startElement("div", null);
				writer.writeAttribute("class", "hd", null);
				writer.write(colorPicker.getHeader());
				writer.endElement("div");

				writer.startElement("div", null);
				writer.writeAttribute("class", "bd", null);

					writer.startElement("div", null);
					writer.writeAttribute("id", clientId + "_container", null);
					writer.writeAttribute("class", "yui-picker", null);
					writer.endElement("div");

				writer.endElement("div");

			writer.endElement("div");
		
		writer.endElement("div");
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

	protected void encodeHiddenField(FacesContext facesContext, ColorPicker colorPicker, String clientId) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String rgb = getValueAsString(facesContext,colorPicker);

		writer.startElement("input", null);
		writer.writeAttribute("id", clientId + "_input", null);
		writer.writeAttribute("name", clientId + "_input", null);
		writer.writeAttribute("type", "hidden", null);

		if(rgb != null)
			writer.writeAttribute("value", rgb, null);

		writer.endElement("input");
	}

	protected void encodeButtonMarkup(FacesContext facesContext, ColorPicker colorPicker, String clientId) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String rgb = getValueAsString(facesContext,colorPicker);

		writer.startElement("button", null);
		writer.writeAttribute("id", clientId + "_button", null);
		writer.writeAttribute("name", clientId + "_button", null);
		writer.writeAttribute("type", "button", null);

		writer.write("<em id=\""+ clientId + "_currentColorDisplay\"" +
						" style=\"overflow:hidden;width:1em;height:1em;display:block;border:solid 1px #000;text-indent:1em;white-space:nowrap;");
		
		if(rgb != null)
			writer.write("background-color:rgb(" + rgb + ");");

		writer.write("\">Current Color Display</em>");

		writer.endElement("button");
	}
	
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