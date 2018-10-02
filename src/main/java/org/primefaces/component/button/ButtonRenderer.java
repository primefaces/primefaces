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
package org.primefaces.component.button;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.OutcomeTargetRenderer;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.WidgetBuilder;

public class ButtonRenderer extends OutcomeTargetRenderer {

    private static final String SB_BUILD_ONCLICK = ButtonRenderer.class.getName() + "#buildOnclick";

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Button button = (Button) component;

        encodeMarkup(context, button);
        encodeScript(context, button);
    }

    public void encodeMarkup(FacesContext context, Button button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);
        String value = (String) button.getValue();
        String icon = button.resolveIcon();

        writer.startElement("button", button);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, "name");
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", button.resolveStyleClass(), "styleClass");

        renderPassThruAttributes(context, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);

        if (button.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        writer.writeAttribute("onclick", buildOnclick(context, button), null);

        //icon
        if (!isValueBlank(icon)) {
            String defaultIconClass = button.getIconPos().equals("left") ? HTML.BUTTON_LEFT_ICON_CLASS : HTML.BUTTON_RIGHT_ICON_CLASS;
            String iconClass = defaultIconClass + " " + icon;

            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");
        }

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);

        if (value == null) {
            writer.write("ui-button");
        }
        else {
            if (button.isEscape()) {
                writer.writeText(value, "value");
            }
            else {
                writer.write(value);
            }
        }

        writer.endElement("span");

        writer.endElement("button");
    }

    public void encodeScript(FacesContext context, Button button) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Button", button.resolveWidgetVar(), button.getClientId(context));
        wb.finish();
    }

    protected String buildOnclick(FacesContext context, Button button) {
        String userOnclick = button.getOnclick();
        StringBuilder onclick = SharedStringBuilder.get(context, SB_BUILD_ONCLICK);
        String targetURL = getTargetURL(context, button);

        if (userOnclick != null) {
            onclick.append(userOnclick).append(";");
        }

        String onclickBehaviors = getEventBehaviors(context, button, "click", null);
        if (onclickBehaviors != null) {
            onclick.append(onclickBehaviors).append(";");
        }

        if (targetURL != null) {
            onclick.append("window.open('").append(EscapeUtils.forJavaScript(targetURL)).append("','");
            onclick.append(EscapeUtils.forJavaScript(button.getTarget())).append("')");
        }

        return onclick.toString();
    }

}
