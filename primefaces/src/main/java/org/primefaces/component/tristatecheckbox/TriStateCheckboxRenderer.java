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
package org.primefaces.component.tristatecheckbox;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = TriStateCheckbox.DEFAULT_RENDERER, componentFamily = TriStateCheckbox.COMPONENT_FAMILY)
public class TriStateCheckboxRenderer extends InputRenderer<TriStateCheckbox> {

    @Override
    public void decode(FacesContext context, TriStateCheckbox component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        String clientId = component.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");

        if (LangUtils.isBlank(submittedValue)) {
            component.setSubmittedValue(null);
        }
        if ("1".equals(submittedValue)) {
            component.setSubmittedValue(Boolean.TRUE);
        }
        else if ("2".equals(submittedValue)) {
            component.setSubmittedValue(Boolean.FALSE);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, TriStateCheckbox component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, TriStateCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);
        Object rawValue = checkbox.getValue();
        Boolean value = null;
        if (rawValue instanceof String) {
            String stringValue = (String) rawValue;
            if (LangUtils.isBlank(stringValue)) {
                value = null;
            }
            else {
                value = Boolean.valueOf(stringValue);
            }
        }
        else {
            value = (Boolean) rawValue;
        }

        boolean disabled = checkbox.isDisabled();
        boolean readonly = checkbox.isReadonly();

        String style = checkbox.getStyle();
        String styleClass = checkbox.getStyleClass();
        styleClass = styleClass == null ? HTML.CHECKBOX_CLASS : HTML.CHECKBOX_CLASS + " " + styleClass;

        writer.startElement("div", checkbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeInput(context, checkbox, clientId, value);
        encodeOutput(context, checkbox, value, disabled, readonly);
        encodeItemLabel(context, checkbox, disabled, readonly);

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, TriStateCheckbox component, String clientId,
                               Object value) throws IOException {
        int valueIndex = 0;
        if (Boolean.TRUE.equals(value)) {
            valueIndex = 1;
        }
        else if (Boolean.FALSE.equals(value)) {
            valueIndex = 2;
        }

        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";

        writer.startElement("div", component);
        writer.writeAttribute("class", HTML.CHECKBOX_INPUT_WRAPPER_CLASS, null);

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("value", Integer.toString(valueIndex), null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("readonly", "readonly", null);

        renderAccessibilityAttributes(context, component);
        renderPassThruAttributes(context, component, HTML.TAB_INDEX);

        if (component.getOnchange() != null) {
            writer.writeAttribute("onchange", component.getOnchange(), null);
        }

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeOutput(FacesContext context, TriStateCheckbox component, Boolean value,
                                boolean disabled, boolean readonly) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = createStyleClass(component, null, HTML.CHECKBOX_BOX_CLASS) ;
        styleClass = value != null ? styleClass + " ui-state-active" : styleClass;
        styleClass = disabled ? styleClass + " ui-state-disabled" : styleClass;
        styleClass = readonly ? styleClass + " ui-chkbox-readonly" : styleClass;

        //if stateIcon is defined use it instead of default icons.
        String stateOneIconClass
                = component.getStateOneIcon() != null ? TriStateCheckbox.UI_ICON + component.getStateOneIcon() : "";
        String stateTwoIconClass
                = component.getStateTwoIcon() != null ? TriStateCheckbox.UI_ICON + component.getStateTwoIcon()
                                                     : TriStateCheckbox.UI_ICON + "ui-icon-check";
        String stateThreeIconClass
                = component.getStateThreeIcon() != null ? TriStateCheckbox.UI_ICON + component.getStateThreeIcon()
                                                       : TriStateCheckbox.UI_ICON + "ui-icon-closethick";

        String statesIconsClasses = "[\"" + stateOneIconClass + "\",\"" + stateTwoIconClass + "\",\"" + stateThreeIconClass + "\"]";

        String stateOneTitle = component.getStateOneTitle() == null ? "" : component.getStateOneTitle();
        String stateTwoTitle = component.getStateTwoTitle() == null ? "" : component.getStateTwoTitle();
        String stateThreeTitle = component.getStateThreeTitle() == null ? "" : component.getStateThreeTitle();

        String statesTitles = "{\"titles\": [\""
                + EscapeUtils.forJavaScript(stateOneTitle)
                + "\",\""
                + EscapeUtils.forJavaScript(stateTwoTitle)
                + "\",\""
                + EscapeUtils.forJavaScript(stateThreeTitle)
                + "\"]}";

        String iconClass = "ui-chkbox-icon ui-c"; //HTML.CHECKBOX_ICON_CLASS;
        String activeTitle = "";
        if (value == null) {
            iconClass = iconClass + " " + stateOneIconClass;
            activeTitle = stateOneTitle;
        }
        else if (Boolean.TRUE.equals(value)) {
            iconClass = iconClass + " " + stateTwoIconClass;
            activeTitle = stateTwoTitle;
        }
        else if (Boolean.FALSE.equals(value)) {
            iconClass = iconClass + " " + stateThreeIconClass;
            activeTitle = stateThreeTitle;
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("data-iconstates", statesIconsClasses, null);
        if (LangUtils.isNotBlank(stateOneTitle) || LangUtils.isNotBlank(stateTwoTitle) || LangUtils.isNotBlank(stateThreeTitle)) {
            writer.writeAttribute("title", activeTitle, null);
            writer.writeAttribute("data-titlestates", statesTitles, null);
        }
        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");
        writer.endElement("div");
    }

    protected void encodeItemLabel(FacesContext context, TriStateCheckbox component, boolean disabled, boolean readonly) throws IOException {
        String label = component.getItemLabel();

        if (label != null) {
            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("span", null);
            String styleClass = HTML.CHECKBOX_LABEL_CLASS;
            styleClass = disabled ? styleClass + " ui-state-disabled" : styleClass;
            styleClass = readonly ? styleClass + " ui-chkbox-readonly" : styleClass;
            writer.writeAttribute("class", styleClass, null);

            if (component.isEscape()) {
                writer.writeText(label, "itemLabel");
            }
            else {
                writer.write(label);
            }
            writer.endElement("span");
        }
    }

    protected void encodeScript(FacesContext context, TriStateCheckbox component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TriStateCheckbox", component);
        encodeClientBehaviors(context, component);
        wb.finish();
    }
}
