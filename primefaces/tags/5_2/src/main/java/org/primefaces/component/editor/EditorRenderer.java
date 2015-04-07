/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.component.editor;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.application.Resource;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.AgentUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class EditorRenderer extends CoreRenderer{

    private final static Logger logger = Logger.getLogger(EditorRenderer.class.getName());
    
    @Override
	public void decode(FacesContext context, UIComponent component) {
		Editor editor = (Editor) component;
        String inputParam = editor.getClientId(context) + "_input";
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String value = params.get(inputParam);
        
        if(value != null && value.equals("<br/>")) {
            value = "";
        }
        
        editor.setSubmittedValue(value);
	}

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Editor editor = (Editor) component;

		encodeMarkup(facesContext, editor);
		encodeScript(facesContext, editor);
	}

	protected void encodeMarkup(FacesContext context, Editor editor) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = editor.getClientId(context);
        String valueToRender = ComponentUtils.getValueToRender(context, editor);
        String inputId = clientId + "_input";

        String style = editor.getStyle();
        style = style == null ? "visibility:hidden" : "visibility:hidden;" + style;

        writer.startElement("div", editor);
        writer.writeAttribute("id", clientId , null);
        writer.writeAttribute("style", style, null);
        if(editor.getStyleClass() != null) {
            writer.writeAttribute("class", editor.getStyleClass(), null);
        }
        
		writer.startElement("textarea", null);
		writer.writeAttribute("id", inputId , null);
        writer.writeAttribute("name", inputId , null);

        if(valueToRender != null) {
            writer.write(valueToRender);
        }

		writer.endElement("textarea");

        writer.endElement("div");
	}
	
	private void encodeScript(FacesContext context, Editor editor) throws IOException{
		String clientId = editor.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Editor", editor.resolveWidgetVar(), clientId, "editor")
                .attr("disabled", editor.isDisabled(), false)
                .attr("invalid", editor.isValid(), true)
                .attr("controls", editor.getControls(), null)
                .attr("width", editor.getWidth(), Integer.MIN_VALUE)
                .attr("height", editor.getHeight(), Integer.MIN_VALUE)
                .attr("maxlength", editor.getMaxlength(), Integer.MAX_VALUE)
                .callback("change", "function(e)", editor.getOnchange());
        
        if(AgentUtils.isIE(context)) {
            Resource resource = context.getApplication().getResourceHandler().createResource("editor/editor-ie.css", "primefaces");
            wb.attr("docCSSFile", resource.getRequestPath());
        }
        
        if(editor.getMaxlength() != Integer.MAX_VALUE) {
            logger.info("Maxlength option is deprecated and will be removed in a future version.");
        }

        wb.finish();
	}
    
    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		Editor editor = (Editor) component;
		String value = (String) submittedValue;
		Converter converter = ComponentUtils.getConverter(context, component);

		if(converter != null) {
			return converter.getAsObject(context, editor, value);
		}

		return value;
	}
}