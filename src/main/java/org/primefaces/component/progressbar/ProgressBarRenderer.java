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
package org.primefaces.component.progressbar;

import java.io.IOException;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class ProgressBarRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		ProgressBar progressBar = (ProgressBar) component;
		String clientId = progressBar.getClientId(facesContext);
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		if(params.containsKey(clientId + "_cancel")) {
			progressBar.getValueExpression("value").setValue(facesContext.getELContext(), progressBar.getMinValue());
			
		} else if(params.containsKey(clientId + "_complete")){
			if(progressBar.getCompleteListener() != null)
				progressBar.getCompleteListener().invoke(facesContext.getELContext(), null);
				
		} else {
			RequestContext.getCurrentInstance().addCallbackParam(progressBar.getClientId(facesContext) + "_value", progressBar.getValue());
		}
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ProgressBar progressBar = (ProgressBar) component;
		
		encodeMarkup(facesContext, progressBar);
		encodeScript(facesContext, progressBar);
	}

	private void encodeMarkup(FacesContext facesContext, ProgressBar progressBar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", progressBar);
		writer.writeAttribute("id", progressBar.getClientId(facesContext), "id");
		if(progressBar.getStyle() != null) writer.writeAttribute("style", progressBar.getStyle(), "style");
		if(progressBar.getStyleClass() != null) writer.writeAttribute("class", progressBar.getStyleClass(), "styleClass");
		writer.endElement("div");
	}

	private void encodeScript(FacesContext facesContext, ProgressBar progressBar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = progressBar.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, progressBar);
		
		writer.startElement("script", progressBar);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(var + " = new PrimeFaces.widget.ProgressBar('" + clientId + "', {");
		writer.write("ajax:" + progressBar.isAjax());
		writer.write(",interval:" + progressBar.getInterval());
		
		if(progressBar.getValue() != 0) writer.write(",value:" + progressBar.getValue());	
		if(progressBar.getMinValue() != 0) writer.write(",minValue:" + progressBar.getHeight());
		if(progressBar.getMaxValue() != 100) writer.write(",maxValue:" + progressBar.getHeight());
		if(progressBar.getWidth() != null) writer.write(",width:'" + progressBar.getWidth() + "'");
		if(progressBar.getHeight() != null) writer.write(",height:'" + progressBar.getHeight() + "'");
		if(progressBar.getDirection() != null) writer.write(",direction:'" + progressBar.getDirection() + "'");
		
		if(progressBar.getEffect() != null) {
			writer.write(",effect:YAHOO.util.Easing." + progressBar.getEffect());
			writer.write(",effectDuration:" + progressBar.getEffectDuration());
		}
		
		if(progressBar.isAjax()) {
			UIComponent form = ComponentUtils.findParentForm(facesContext, progressBar);
			if(form == null)
				throw new FacesException("ProgressBar \"" + clientId + "\" must be enclosed with a form in ajax mode.");
			
			writer.write(",formId:'" + form.getClientId(facesContext) + "'");
			writer.write(",actionURL:'" + getActionURL(facesContext) + "'");
			
			if(progressBar.getOncompleteUpdate() != null)
				writer.write(",oncompleteUpdate:'" + ComponentUtils.findClientIds(facesContext, progressBar, progressBar.getOncompleteUpdate()) + "'");
			
			if(progressBar.getCompleteListener() != null || progressBar.getOncompleteUpdate() != null)
				writer.write(",hasCompleteProcess:true");
		} else {
			writer.write(",step:" + progressBar.getStep());
		}
		
 		writer.write("});");
		
		writer.endElement("script");
	}
}
