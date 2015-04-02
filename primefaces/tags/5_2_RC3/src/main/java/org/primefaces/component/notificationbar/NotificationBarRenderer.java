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
package org.primefaces.component.notificationbar;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class NotificationBarRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		NotificationBar bar = (NotificationBar) component;
		
		encodeMarkup(context, bar);
		encodeScript(context, bar);
	}
	
	protected void encodeMarkup(FacesContext context, NotificationBar bar) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String styleClass = bar.getStyleClass();
        styleClass = styleClass == null ? NotificationBar.STYLE_CLASS : NotificationBar.STYLE_CLASS + " " + styleClass;
		
		writer.startElement("div", bar);
		writer.writeAttribute("id", bar.getClientId(context), null);
		writer.writeAttribute("class", styleClass, null);
		if(bar.getStyle() != null) {
          writer.writeAttribute("style", bar.getStyle(), null);
        }
		
		renderChildren(context, bar);
		
		writer.endElement("div");
	}

	private void encodeScript(FacesContext context, NotificationBar bar) throws IOException {
		String clientId = bar.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("NotificationBar", bar.resolveWidgetVar(), clientId)
            .attr("position", bar.getPosition())
            .attr("effect", bar.getEffect())
            .attr("effectSpeed", bar.getEffectSpeed())
            .attr("autoDisplay", bar.isAutoDisplay(), false);
		wb.finish();
	}

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}