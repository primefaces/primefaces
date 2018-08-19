/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.linkbutton;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.OutcomeTargetRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class LinkButtonRenderer extends OutcomeTargetRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        LinkButton linkButton = (LinkButton) component;

        encodeMarkup(context, linkButton);
        encodeScript(context, linkButton);
    }

    protected void encodeMarkup(FacesContext context, LinkButton linkButton) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        boolean disabled = linkButton.isDisabled();

        String style = linkButton.getStyle();
        String defaultStyleClass = disabled ? LinkButton.DISABLED_STYLE_CLASS : LinkButton.STYLE_CLASS;

        String styleClass = linkButton.getStyleClass();
        styleClass = (styleClass == null) ? defaultStyleClass : defaultStyleClass + " " + styleClass;

        writer.startElement("span", linkButton);
        writer.writeAttribute("id", linkButton.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        if (disabled) {
            writer.startElement("span", null);
            writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);

            renderContent(context, linkButton);
            writer.endElement("span");
        }
        else {
            String targetURL = getTargetURL(context, linkButton);
            if (targetURL == null) {
                targetURL = "#";
            }

            writer.startElement("a", null);
            writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
            writer.writeAttribute("href", targetURL, null);
            renderPassThruAttributes(context, linkButton, HTML.LINK_ATTRS_WITHOUT_EVENTS);
            renderDomEvents(context, linkButton, HTML.OUTPUT_EVENTS);
            renderContent(context, linkButton);
            writer.endElement("a");
        }

        writer.endElement("span");
    }

    protected void encodeScript(FacesContext context, LinkButton button) throws IOException {
        String clientId = button.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("LinkButton", button.resolveWidgetVar(), clientId);

        wb.finish();
    }

    protected void renderContent(FacesContext context, LinkButton linkButton) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Object value = linkButton.getValue();

        if (value != null) {
            if (linkButton.isEscape()) {
                writer.writeText(value, "value");
            }
            else {
                writer.write(value.toString());
            }
        }
        else {
            renderChildren(context, linkButton);
        }
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
