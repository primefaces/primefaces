/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.component.watermark;

import java.io.IOException;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class WatermarkRenderer extends CoreRenderer {

	private static final Logger LOG = Logger.getLogger(WatermarkRenderer.class.getName());
	
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Watermark watermark = (Watermark) component;
		String target = null;
		
		String _for = watermark.getFor();
		if(_for != null) {
			target = SearchExpressionFacade.resolveComponentsForClient(context, watermark, _for);
		} 
        else if(watermark.getForElement() != null) {
        	LOG.warning("The attribute 'forElement' will be removed in the future. Please use the 'for' attribute with PrimeFaces Selectors");
			target = "@(" + watermark.getForElement() + ")";
		} 
        else {
			throw new FacesException("Either for or forElement options must be used to define a watermark.");
		}

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.widget("Watermark", watermark.resolveWidgetVar(), watermark.getClientId(context), "watermark", true)
            .attr("value", watermark.getValue())
            .attr("target", target);
        
        startScript(writer, watermark.getClientId(context));
		writer.write(wb.build());
		endScript(writer);
	}
}