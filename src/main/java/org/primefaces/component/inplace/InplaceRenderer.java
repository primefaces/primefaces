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
package org.primefaces.component.inplace;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

public class InplaceRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Inplace inplace = (Inplace) component;

        encodeMarkup(context, inplace);
        encodeScript(context, inplace);
    }

    protected void encodeMarkup(FacesContext context, Inplace inplace) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = inplace.getClientId(context);
        String widgetVar = inplace.resolveWidgetVar();

        String userStyleClass = inplace.getStyleClass();
        String userStyle = inplace.getStyle();
        String styleClass = userStyleClass == null ? Inplace.CONTAINER_CLASS : Inplace.CONTAINER_CLASS + " " + userStyleClass;
        boolean disabled = inplace.isDisabled();
        String displayClass = disabled ? Inplace.DISABLED_DISPLAY_CLASS : Inplace.DISPLAY_CLASS;

        boolean validationFailed = context.isValidationFailed() && !inplace.isValid();
        String displayStyle = validationFailed ? Inplace.DISPLAY_NONE : Inplace.DISPLAY_INLINE;
        String contentStyle = validationFailed ? Inplace.DISPLAY_INLINE : Inplace.DISPLAY_NONE;

        UIComponent outputFacet = inplace.getFacet("output");
        UIComponent inputFacet = inplace.getFacet("input");

        //container
        writer.startElement("span", inplace);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "id");
        if (userStyle != null) {
            writer.writeAttribute("style", userStyle, "id");
        }

        writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);

        //display
        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + "_display", "id");
        writer.writeAttribute("class", displayClass, null);
        writer.writeAttribute("style", "display:" + displayStyle, null);

        if (outputFacet != null) {
            outputFacet.encodeAll(context);
        }
        else {
            writer.writeText(getLabelToRender(context, inplace), null);
        }

        writer.endElement("span");

        //content
        if (!inplace.isDisabled()) {
            writer.startElement("span", null);
            writer.writeAttribute("id", clientId + "_content", "id");
            writer.writeAttribute("class", Inplace.CONTENT_CLASS, null);
            writer.writeAttribute("style", "display:" + contentStyle, null);

            if (inputFacet != null) {
                inputFacet.encodeAll(context);
            }
            else {
                renderChildren(context, inplace);
            }

            if (inplace.isEditor()) {
                encodeEditor(context, inplace);
            }

            writer.endElement("span");
        }

        writer.endElement("span");
    }

    protected String getLabelToRender(FacesContext context, Inplace inplace) {
        String label = inplace.getLabel();
        String emptyLabel = inplace.getEmptyLabel();

        if (!isValueBlank(label)) {
            return label;
        }
        else {
            String value = ComponentUtils.getValueToRender(context, inplace.getChildren().get(0));

            if (LangUtils.isValueBlank(value)) {
                if (emptyLabel != null) {
                    return emptyLabel;
                }
                else {
                    return Constants.EMPTY_STRING;
                }
            }
            else {
                return value;
            }
        }
    }

    protected void encodeScript(FacesContext context, Inplace inplace) throws IOException {
        String clientId = inplace.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Inplace", inplace.resolveWidgetVar(), clientId)
                .attr("effect", inplace.getEffect())
                .attr("effectSpeed", inplace.getEffectSpeed())
                .attr("event", inplace.getEvent())
                .attr("toggleable", inplace.isToggleable(), false)
                .attr("disabled", inplace.isDisabled(), false)
                .attr("editor", inplace.isEditor(), false);

        encodeClientBehaviors(context, inplace);

        wb.finish();
    }

    protected void encodeEditor(FacesContext context, Inplace inplace) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("span", null);
        writer.writeAttribute("id", inplace.getClientId(context) + "_editor", null);
        writer.writeAttribute("class", Inplace.EDITOR_CLASS, null);

        encodeButton(context, inplace.getSaveLabel(), Inplace.SAVE_BUTTON_CLASS, "ui-icon-check");
        encodeButton(context, inplace.getCancelLabel(), Inplace.CANCEL_BUTTON_CLASS, "ui-icon-close");

        writer.endElement("span");
    }

    protected void encodeButton(FacesContext context, String title, String styleClass, String icon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("button", null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", HTML.BUTTON_ICON_ONLY_BUTTON_CLASS + " " + styleClass, null);
        writer.writeAttribute("title", title, null);

        //icon
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + icon, null);
        writer.endElement("span");

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.write("ui-button");
        writer.endElement("span");

        writer.endElement("button");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
