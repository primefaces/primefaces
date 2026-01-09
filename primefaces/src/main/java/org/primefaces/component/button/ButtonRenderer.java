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
package org.primefaces.component.button;

import org.primefaces.renderkit.OutcomeTargetRenderer;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Button.DEFAULT_RENDERER, componentFamily = Button.COMPONENT_FAMILY)
public class ButtonRenderer extends OutcomeTargetRenderer<Button> {

    private static final String SB_BUILD_ONCLICK = ButtonRenderer.class.getName() + "#buildOnclick";

    @Override
    public void encodeEnd(FacesContext context, Button component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    public void encodeMarkup(FacesContext context, Button component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        Object value = component.getValue();
        String icon = component.getIcon();
        String title = component.getTitle();

        writer.startElement("button", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, "name");
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", component.resolveStyleClass(), "styleClass");

        renderPassThruAttributes(context, component, HTML.BUTTON_WITHOUT_CLICK_ATTRS);

        if (component.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        writer.writeAttribute("onclick", buildOnclick(context, component), null);

        //icon
        if (!isValueBlank(icon)) {
            String defaultIconClass = component.getIconPos().equals("left") ? HTML.BUTTON_LEFT_ICON_CLASS : HTML.BUTTON_RIGHT_ICON_CLASS;
            String iconClass = defaultIconClass + " " + icon;

            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");
        }

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);

        renderButtonValue(writer, component.isEscape(), value, title, component.getAriaLabel());

        writer.endElement("span");

        writer.endElement("button");
    }

    public void encodeScript(FacesContext context, Button component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Button", component);
        wb.finish();
    }

    protected String buildOnclick(FacesContext context, Button component) {
        String userOnclick = component.getOnclick();
        StringBuilder onclick = SharedStringBuilder.get(context, SB_BUILD_ONCLICK);
        String targetURL = getTargetURL(context, component);

        if (userOnclick != null) {
            onclick.append(userOnclick).append(";");
        }

        String onclickBehaviors = getEventBehaviors(context, component, "click", null);
        if (onclickBehaviors != null) {
            onclick.append(onclickBehaviors).append(";");
        }

        if (targetURL != null) {
            onclick.append("window.open('").append(EscapeUtils.forJavaScript(targetURL)).append("','");
            onclick.append(EscapeUtils.forJavaScript(component.getTarget())).append("')");
        }

        return onclick.toString();
    }

}
