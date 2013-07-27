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
package org.primefaces.component.stack;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class StackRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Stack stack = (Stack) component;
				
		encodeMarkup(facesContext, stack);
		encodeScript(facesContext, stack);
	}
	
	protected void encodeScript(FacesContext context, Stack stack) throws IOException {
		String clientId = stack.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Stack", stack.resolveWidgetVar(), clientId, "stack")
            .attr("openSpeed", stack.getOpenSpeed())
            .attr("closeSpeed", stack.getCloseSpeed())
            .attr("expanded", stack.isExpanded(), false);

        wb.finish();
	}
	
	protected void encodeMarkup(FacesContext context, Stack stack) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = stack.getClientId(context);
		
		writer.startElement("div", stack);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("class", "ui-stack", null);
		
		writer.startElement("img", null);
		writer.writeAttribute("src", getResourceURL(context, stack.getIcon()), null);
		writer.endElement("img");
		
        if(stack.getElementsCount() > 0) {
            List<MenuElement> elements = stack.getElements();
            
            writer.startElement("ul", null);
            writer.writeAttribute("id", clientId + "_stack", "id");
		
            for(MenuElement element : elements) {
                if(element.isRendered() && element instanceof MenuItem) {
                    encodeMenuItem(context, (MenuItem) element);
                }
            }
        }
		
		writer.endElement("ul");
		
		writer.endElement("div");
	}
	
	protected void encodeMenuItem(FacesContext context, MenuItem menuitem) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.startElement("li", null);
		
        writer.startElement("a", null);

        if(menuitem.getStyle() != null) writer.writeAttribute("style", menuitem.getStyle(), null);
        if(menuitem.getStyleClass() != null) writer.writeAttribute("class", menuitem.getStyleClass(), null);

        if(menuitem.getUrl() != null) {
            writer.writeAttribute("href", getResourceURL(context, menuitem.getUrl()), null);
            
            if(menuitem.getOnclick() != null) writer.writeAttribute("onclick", menuitem.getOnclick(), null);
            if(menuitem.getTarget() != null) writer.writeAttribute("target", menuitem.getTarget(), null);
        } 
        else {
            writer.writeAttribute("href", "#", null);

            /*UIComponent form = ComponentUtils.findParentForm(context, menuitem);
            if(form == null) {
                throw new FacesException("Stack must be inside a form element");
            }

            String command = menuitem.isAjax() ? buildAjaxRequest(context, menuitem, form) : buildNonAjaxRequest(context, menuitem, form, clientId, true);


            String command = menuitem.getOnclick() == null ? command : menuitem.getOnclick() + ";" + command;

            writer.writeAttribute("onclick", command, null);*/
        }

        //Label
        writer.startElement("span", null);

        if(menuitem.getValue() != null) writer.write((String) menuitem.getValue());

        writer.endElement("span");

        //Icon
        writer.startElement("img", null);
        writer.writeAttribute("src", getResourceURL(context, menuitem.getIcon()), null);
        writer.endElement("img");

        writer.endElement("a");

		writer.endElement("li");
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