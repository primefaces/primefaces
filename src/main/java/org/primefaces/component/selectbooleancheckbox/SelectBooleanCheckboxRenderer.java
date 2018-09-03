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
package org.primefaces.component.selectbooleancheckbox;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class SelectBooleanCheckboxRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SelectBooleanCheckbox checkbox = (SelectBooleanCheckbox) component;

        if (checkbox.isDisabled()) {
            return;
        }

        decodeBehaviors(context, checkbox);

        String clientId = checkbox.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");

        if (submittedValue != null && isChecked(submittedValue)) {
            checkbox.setSubmittedValue(true);
        }
        else {
            checkbox.setSubmittedValue(false);
        }
    }

    protected boolean isChecked(String value) {
        return value.equalsIgnoreCase("on") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectBooleanCheckbox checkbox = (SelectBooleanCheckbox) component;

        encodeMarkup(context, checkbox);
        encodeScript(context, checkbox);
    }

    protected void encodeMarkup(FacesContext context, SelectBooleanCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);
        boolean checked = Boolean.valueOf(ComponentUtils.getValueToRender(context, checkbox));
        boolean disabled = checkbox.isDisabled();
        String title = checkbox.getTitle();

        String style = checkbox.getStyle();
        String styleClass = checkbox.getStyleClass();
        styleClass = styleClass == null ? HTML.CHECKBOX_CLASS : HTML.CHECKBOX_CLASS + " " + styleClass;
        styleClass = "ui-selectbooleancheckbox " + styleClass;

        writer.startElement("div", checkbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        if (title != null) {
            writer.writeAttribute("title", title, "title");
        }

        encodeInput(context, checkbox, clientId, checked, disabled);
        encodeOutput(context, checkbox, checked, disabled);
        encodeItemLabel(context, checkbox, clientId);

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, SelectBooleanCheckbox checkbox, String clientId, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";
        String labelledBy = checkbox.getLabelledBy();

        writer.startElement("div", checkbox);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("aria-hidden", "true", null);

        if (labelledBy != null) {
            writer.writeAttribute("aria-labelledby", labelledBy, null);
        }

        if (checked) {
            writer.writeAttribute("checked", "checked", null);
            writer.writeAttribute("aria-checked", "true", null);
        }
        else {
            writer.writeAttribute("aria-checked", "false", null);
        }

        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        if (checkbox.getTabindex() != null) {
            writer.writeAttribute("tabindex", checkbox.getTabindex(), null);
        }

        if (PrimeApplicationContext.getCurrentInstance(context).getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, checkbox);
        }

        renderOnchange(context, checkbox);
        renderDomEvents(context, checkbox, HTML.BLUR_FOCUS_EVENTS);

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeOutput(FacesContext context, SelectBooleanCheckbox checkbox, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = HTML.CHECKBOX_BOX_CLASS;
        styleClass = checked ? styleClass + " ui-state-active" : styleClass;
        styleClass = !checkbox.isValid() ? styleClass + " ui-state-error" : styleClass;
        styleClass = disabled ? styleClass + " ui-state-disabled" : styleClass;

        String iconClass = checked ? HTML.CHECKBOX_CHECKED_ICON_CLASS : HTML.CHECKBOX_UNCHECKED_ICON_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeItemLabel(FacesContext context, SelectBooleanCheckbox checkbox, String clientId) throws IOException {
        String label = checkbox.getItemLabel();

        if (label != null) {
            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("span", null);
            writer.writeAttribute("class", HTML.CHECKBOX_LABEL_CLASS, null);
            writer.writeText(label, "itemLabel");
            writer.endElement("span");
        }
    }

    protected void encodeScript(FacesContext context, SelectBooleanCheckbox checkbox) throws IOException {
        String clientId = checkbox.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectBooleanCheckbox", checkbox.resolveWidgetVar(), clientId).finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return ((submittedValue instanceof Boolean) ? submittedValue : Boolean.valueOf(submittedValue.toString()));
    }

    @Override
    public String getHighlighter() {
        return "booleanchkbox";
    }
}
