/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
        String styleClass = linkButton.resolveStyleClass();
        String title = linkButton.getTitle();

        writer.startElement("span", linkButton);
        writer.writeAttribute("id", linkButton.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        if (title != null) {
            writer.writeAttribute("title", title, "title");
        }
        renderPassThruAttributes(context, linkButton, HTML.OUTPUT_EVENTS);

        if (disabled) {
            renderContent(context, linkButton);
        }
        else {
            String targetURL = getTargetURL(context, linkButton);
            if (targetURL == null) {
                targetURL = "#";
            }

            writer.startElement("a", null);
            writer.writeAttribute("href", targetURL, null);
            renderPassThruAttributes(context, linkButton, HTML.LINK_ATTRS_WITHOUT_EVENTS_AND_STYLE, HTML.TITLE);
            renderDomEvents(context, linkButton, HTML.OUTPUT_EVENTS);
            renderContent(context, linkButton);
            writer.endElement("a");
        }

        writer.endElement("span");
    }

    protected void encodeScript(FacesContext context, LinkButton button) throws IOException {
        String clientId = button.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("LinkButton", button.resolveWidgetVar(context), clientId);

        wb.finish();
    }

    protected void renderContent(FacesContext context, LinkButton linkButton) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String icon = linkButton.getIcon();
        if (!isValueBlank(icon)) {
            String defaultIconClass = linkButton.getIconPos().equals("left") ? HTML.BUTTON_LEFT_ICON_CLASS : HTML.BUTTON_RIGHT_ICON_CLASS;
            String iconClass = defaultIconClass + " " + icon;

            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);

        String value = (String) linkButton.getValue();
        if (value == null) {
            writer.write("ui-button");
        }
        else {
            if (linkButton.isEscape()) {
                writer.writeText(value, "value");
            }
            else {
                writer.write(value);
            }
        }

        writer.endElement("span");
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
