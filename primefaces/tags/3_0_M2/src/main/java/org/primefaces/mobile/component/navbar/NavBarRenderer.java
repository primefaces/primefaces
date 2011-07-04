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
package org.primefaces.mobile.component.navbar;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class NavBarRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        NavBar navBar = (NavBar) component;

        writer.startElement("div", navBar);
        writer.writeAttribute("id", navBar.getClientId(context), "id");
        writer.writeAttribute("data-role", "navbar", null);
        if(navBar.getSwatch() != null)
            writer.writeAttribute("data-theme", navBar.getSwatch(), null);

        writer.startElement("ul", navBar);
        for(UIComponent child : navBar.getChildren()) {
            writer.startElement("li", navBar);
            if(child.isRendered()) {
                child.encodeAll(context);
            }
            writer.endElement("li");
        }
        writer.endElement("ul");

        writer.endElement("div");
    }

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}