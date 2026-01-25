/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.renderkit.OutcomeTargetRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Objects;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = LinkButton.DEFAULT_RENDERER, componentFamily = LinkButton.COMPONENT_FAMILY)
public class LinkButtonRenderer extends OutcomeTargetRenderer<LinkButton> {

    @Override
    public void encodeEnd(FacesContext context, LinkButton component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, LinkButton component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        boolean disabled = component.isDisabled();
        boolean hasIcon = LangUtils.isNotBlank(component.getIcon());
        boolean hasValue = LangUtils.isNotBlank(getValueAsString(component)) || component.hasDisplayedChildren();
        boolean isTextAndIcon = hasValue && hasIcon;

        String style = component.getStyle();
        String title = component.getTitle();
        String styleClass = getStyleClassBuilder(context)
                    .add("ui-linkbutton")
                    .add(component.getStyleClass())
                    .add(hasValue && !hasIcon, HTML.BUTTON_TEXT_ONLY_BUTTON_CLASS)
                    .add(!hasValue && hasIcon, HTML.BUTTON_ICON_ONLY_BUTTON_CLASS)
                    .add(isTextAndIcon && "left".equals(component.getIconPos()), HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS)
                    .add(isTextAndIcon && "right".equals(component.getIconPos()), HTML.BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS)
                    .add(disabled, "ui-state-disabled")
                    .build();

        writer.startElement("span", component);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        if (title != null) {
            writer.writeAttribute("title", title, "title");
        }
        renderPassThruAttributes(context, component, HTML.OUTPUT_EVENTS_WITHOUT_CLICK);

        String targetURL = getTargetURL(context, component);
        if (targetURL == null) {
            targetURL = "#";
        }

        writer.startElement("a", null);
        writer.writeAttribute("href", targetURL, null);
        renderPassThruAttributes(context, component, HTML.LINK_ATTRS_WITHOUT_EVENTS_AND_STYLE, HTML.TITLE);
        renderDomEvents(context, component, HTML.OUTPUT_EVENTS);
        renderContent(context, component);
        writer.endElement("a");

        writer.endElement("span");
    }

    protected void encodeScript(FacesContext context, LinkButton component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("LinkButton", component);
        wb.finish();
    }

    protected void renderContent(FacesContext context, LinkButton component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String icon = component.getIcon();
        if (!isValueBlank(icon)) {
            String defaultIconClass = component.getIconPos().equals("left") ? HTML.BUTTON_LEFT_ICON_CLASS : HTML.BUTTON_RIGHT_ICON_CLASS;
            String iconClass = defaultIconClass + " " + icon;

            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);

        String value = getValueAsString(component);
        if (LangUtils.isBlank(value) && component.hasDisplayedChildren()) {
            renderChildren(context, component);
        }
        else {
            renderButtonValue(writer, component.isEscape(), value, component.getTitle(), component.getAriaLabel());
        }

        writer.endElement("span");
    }

    protected String getValueAsString(LinkButton component) {
        return Objects.toString(component.getValue(), null);
    }

    @Override
    public void encodeChildren(FacesContext context, LinkButton component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
