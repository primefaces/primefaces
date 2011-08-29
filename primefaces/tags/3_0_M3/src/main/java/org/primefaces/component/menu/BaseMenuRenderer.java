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
package org.primefaces.component.menu;

import java.io.IOException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public abstract class BaseMenuRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		AbstractMenu menu = (AbstractMenu) component;

        if(menu.isDynamic()) {
            menu.buildMenuFromModel();
        }

		encodeMarkup(context, menu);
		encodeScript(context, menu);
	}

    protected abstract void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException;

    protected abstract void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException;

    protected void encodeMenuItem(FacesContext context, MenuItem menuItem) throws IOException {
		String clientId = menuItem.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();
        String icon = menuItem.getIcon();

		if(menuItem.shouldRenderChildren()) {
			renderChildren(context, menuItem);
		}
        else {
            boolean disabled = menuItem.isDisabled();
            String onclick = menuItem.getOnclick();
            
            writer.startElement("a", null);
            String styleClass = menuItem.getStyleClass();
            styleClass = styleClass == null ? MenuItem.STYLE_CLASS : MenuItem.STYLE_CLASS + " " + styleClass;
            styleClass = disabled ? styleClass + " ui-state-disabled" : styleClass;
            
            writer.writeAttribute("class", styleClass, null);
            
            if(menuItem.getStyle() != null) 
                writer.writeAttribute("style", menuItem.getStyle(), null);
                        
			if(menuItem.getUrl() != null) {
                String href = disabled ? "javascript:void(0)" : getResourceURL(context, menuItem.getUrl());
				writer.writeAttribute("href", href, null);
                                
				if(menuItem.getTarget() != null) 
                    writer.writeAttribute("target", menuItem.getTarget(), null);
			} 
            else {
				writer.writeAttribute("href", "javascript:void(0)", null);

				UIComponent form = ComponentUtils.findParentForm(context, menuItem);
				if(form == null) {
					throw new FacesException("Menubar must be inside a form element");
				}

                String command = menuItem.isAjax() ? buildAjaxRequest(context, menuItem) : buildNonAjaxRequest(context, menuItem, form.getClientId(context), clientId);

                onclick = onclick == null ? command : onclick + ";" + command;
			}

            if(onclick != null && !disabled) {
                writer.writeAttribute("onclick", onclick, null);
            }

            if(icon != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", icon + " wijmo-wijmenu-icon-left", null);
                writer.endElement("span");
            }

			if(menuItem.getValue() != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class",  "wijmo-wijmenu-text", null);
                writer.write((String) menuItem.getValue());
                writer.endElement("span");
            }

            writer.endElement("a");
		}
	}

    @Override
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}
