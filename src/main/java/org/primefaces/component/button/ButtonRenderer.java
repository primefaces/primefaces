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
package org.primefaces.component.button;

import java.io.IOException;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;

public class ButtonRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Button button = (Button) component;

        encodeMarkup(context, button);
        encodeScript(context, button);
    }

    public void encodeMarkup(FacesContext context, Button button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
		String clientId = button.getClientId(context);

		writer.startElement("button", button);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("name", clientId, "name");
		if(button.getStyleClass() != null) {
			writer.writeAttribute("class", button.getStyleClass() , "styleClass");
        }

		renderPassThruAttributes(context, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);

		writer.writeAttribute("onclick", getOnclick(context, button), null);

		if(button.getValue() != null) {
			writer.write(button.getValue().toString());
		}

		writer.endElement("button");
    }

    public void encodeScript(FacesContext context, Button button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
		String clientId = button.getClientId(context);
		String widgetVar = createUniqueWidgetVar(context, button);
		boolean hasValue = (button.getValue() != null);

		writer.startElement("script", button);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(widgetVar + " = new PrimeFaces.widget.Button('" + clientId + "', {");

		if(button.getImage() != null) {
			writer.write("text:" + hasValue);
			writer.write(",icons:{");
			writer.write("primary:'" + button.getImage() + "'");
			writer.write("}");
		}

		writer.write("});");

		writer.endElement("script");
    }

    protected String getOnclick(FacesContext context, Button button) {
        NavigationCase navCase = findNavigationCase(context, button);
        String toViewId = navCase.getToViewId(context);
        boolean isIncludeViewParams = isIncludeViewParams(button, navCase);

        String bookmarkableURL = context.getApplication().getViewHandler().getBookmarkableURL(context, toViewId, null, isIncludeViewParams);
    
        String onclick = "window.location.href='" + bookmarkableURL + "';";

        return onclick;
    }

    protected NavigationCase findNavigationCase(FacesContext context, Button button) {
        ConfigurableNavigationHandler navHandler = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();
        String outcome = button.getOutcome();
        
        if(outcome == null) {
            outcome = context.getViewRoot().getViewId();
        }
        
        return navHandler.getNavigationCase(context, null, outcome);
    }

    protected boolean isIncludeViewParams(Button button, NavigationCase navCase) {
        return button.isIncludeViewParams() || navCase.isIncludeViewParams();
    }
}
