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
package org.primefaces.component.inplace;

import org.primefaces.component.password.Password;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Inplace.DEFAULT_RENDERER, componentFamily = Inplace.COMPONENT_FAMILY)
public class InplaceRenderer extends CoreRenderer<Inplace> {

    @Override
    public void decode(FacesContext context, Inplace component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Inplace component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, Inplace component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String widgetVar = component.resolveWidgetVar(context);
        String userStyle = component.getStyle();
        boolean disabled = component.isDisabled();
        boolean validationFailed = context.isValidationFailed() && !component.isValid();
        UIComponent outputFacet = component.getOutputFacet();
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(outputFacet);
        boolean withPassword = !shouldRenderFacet && isPassword(component.getChildren().get(0));
        String styleClass = getStyleClassBuilder(context)
                .add(Inplace.CONTAINER_CLASS, component.getStyleClass())
                .add(withPassword, "p-password")
                .build();
        String mode = component.getMode();

        //container
        writer.startElement("span", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "id");
        if (userStyle != null) {
            writer.writeAttribute("style", userStyle, "id");
        }

        writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);

        //output
        String outputStyleClass = disabled ? Inplace.DISABLED_DISPLAY_CLASS : Inplace.DISPLAY_CLASS;
        String outputStyle = getStyleBuilder(context)
                .add(validationFailed, "display", Inplace.DISPLAY_NONE)
                .add(!validationFailed && Inplace.MODE_OUTPUT.equals(mode), "display", Inplace.DISPLAY_INLINE)
                .add(!validationFailed && Inplace.MODE_INPUT.equals(mode), "display", Inplace.DISPLAY_NONE)
                .build();

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + "_display", "id");
        writer.writeAttribute("class", outputStyleClass, null);
        writer.writeAttribute("style", outputStyle, null);
        if (component.getTabindex() != null) {
            writer.writeAttribute("tabindex", component.getTabindex(), null);
            writer.writeAttribute("role", "button", null);
        }

        if (shouldRenderFacet) {
            outputFacet.encodeAll(context);
        }
        else {
            encodeLabel(context, component, withPassword);
        }

        writer.endElement("span");


        //input
        String inputStyle = getStyleBuilder(context)
                .add(validationFailed, "display", Inplace.DISPLAY_INLINE)
                .add(!validationFailed && Inplace.MODE_OUTPUT.equals(mode), "display", Inplace.DISPLAY_NONE)
                .add(!validationFailed && Inplace.MODE_INPUT.equals(mode), "display", Inplace.DISPLAY_INLINE)
                .build();
        UIComponent inputFacet = component.getInputFacet();

        if (!component.isDisabled()) {
            writer.startElement("span", null);
            writer.writeAttribute("id", clientId + "_content", "id");
            writer.writeAttribute("class", Inplace.CONTENT_CLASS, null);
            writer.writeAttribute("style", inputStyle, null);

            if (FacetUtils.shouldRenderFacet(inputFacet)) {
                inputFacet.encodeAll(context);
            }
            else {
                renderChildren(context, component);
            }

            if (component.isEditor()) {
                encodeEditor(context, component);
            }

            writer.endElement("span");
        }

        writer.endElement("span");
    }

    protected void encodeLabel(FacesContext context, Inplace component, boolean withPassword) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent editor = component.getChildren().get(0);
        String value = ComponentUtils.getValueToRender(context, editor);
        boolean needsWrapping = withPassword
                && LangUtils.isBlank(component.getLabel())
                && !LangUtils.isBlank(value);
        if (needsWrapping) {
            writer.startElement("span", null);
            writer.writeAttribute("class", "p-masked", null);
            writer.writeText(Constants.EMPTY_STRING, null);
        }
        else {
            writer.writeText(getLabelToRender(context, component, value), null);
        }
        if (needsWrapping) {
            writer.endElement("span");
        }
    }

    protected boolean isPassword(UIComponent editor) {
        return editor instanceof Password
                || "password".equalsIgnoreCase(String.valueOf(editor.getAttributes().get("type")));
    }

    protected String getLabelToRender(FacesContext context, Inplace component, String value) {
        String label = component.getLabel();
        if (!isValueBlank(label)) {
            return label;
        }

        if (LangUtils.isBlank(value)) {
            String emptyLabel = component.getEmptyLabel();
            if (emptyLabel != null) {
                return emptyLabel;
            }

            return Constants.EMPTY_STRING;
        }

        return value;
    }

    protected void encodeScript(FacesContext context, Inplace component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Inplace", component)
                .attr("effect", component.getEffect())
                .attr("effectSpeed", component.getEffectSpeed())
                .attr("event", component.getEvent())
                .attr("toggleable", component.isToggleable(), false)
                .attr("disabled", component.isDisabled(), false)
                .attr("editor", component.isEditor(), false);

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeEditor(FacesContext context, Inplace component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("span", null);
        writer.writeAttribute("id", component.getClientId(context) + "_editor", null);
        writer.writeAttribute("class", Inplace.EDITOR_CLASS, null);

        encodeButton(context, component.getSaveLabel(), Inplace.SAVE_BUTTON_CLASS, "ui-icon-check");
        encodeButton(context, component.getCancelLabel(), Inplace.CANCEL_BUTTON_CLASS, "ui-icon-close");

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
    public void encodeChildren(FacesContext context, Inplace component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
