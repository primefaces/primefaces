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
package org.primefaces.component.tristatecheckbox;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

public class TriStateCheckboxRenderer extends InputRenderer {

    @Override
    public void decode(final FacesContext context, final UIComponent component) {
        TriStateCheckbox checkbox = (TriStateCheckbox) component;

        if (!shouldDecode(checkbox)) {
            return;
        }

        decodeBehaviors(context, checkbox);

        String clientId = checkbox.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");

        if (submittedValue != null) {
            checkbox.setSubmittedValue(submittedValue);
        }
    }

    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
        TriStateCheckbox checkbox = (TriStateCheckbox) component;

        encodeMarkup(context, checkbox);
        encodeScript(context, checkbox);
    }

    protected void encodeMarkup(final FacesContext context, final TriStateCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);
        String valueToRenderer = ComponentUtils.getValueToRender(context, checkbox);

        int valCheck = LangUtils.isValueBlank(valueToRenderer) ? 0 : Integer.parseInt(valueToRenderer);

        if (valCheck > 2 || valCheck < 0) {
            valCheck = 0;
        }

        boolean disabled = checkbox.isDisabled();

        String style = checkbox.getStyle();
        String styleClass = checkbox.getStyleClass();
        styleClass = styleClass == null ? HTML.CHECKBOX_CLASS : HTML.CHECKBOX_CLASS + " " + styleClass;

        writer.startElement("div", checkbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeInput(context, checkbox, clientId, valCheck);
        encodeOutput(context, checkbox, valCheck, disabled);
        encodeItemLabel(context, checkbox);

        writer.endElement("div");
    }

    protected void encodeInput(final FacesContext context, final TriStateCheckbox checkbox, final String clientId,
                               final int valCheck) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";

        writer.startElement("div", checkbox);
        writer.writeAttribute("class", HTML.CHECKBOX_INPUT_WRAPPER_CLASS, null);

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("value", valCheck, null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("readonly", "readonly", null);

        renderAccessibilityAttributes(context, checkbox);
        renderPassThruAttributes(context, checkbox, HTML.TAB_INDEX);

        if (checkbox.getOnchange() != null) {
            writer.writeAttribute("onchange", checkbox.getOnchange(), null);
        }

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeOutput(final FacesContext context, final TriStateCheckbox checkbox, final int valCheck,
                                final boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = HTML.CHECKBOX_BOX_CLASS;
        styleClass = (valCheck == 1 || valCheck == 2) ? styleClass + " ui-state-active" : styleClass;
        styleClass = !checkbox.isValid() ? styleClass + " ui-state-error" : styleClass;
        styleClass = disabled ? styleClass + " ui-state-disabled" : styleClass;

        //if stateIcon is defined use it insted of default icons.
        String stateOneIconClass
                = checkbox.getStateOneIcon() != null ? TriStateCheckbox.UI_ICON + checkbox.getStateOneIcon() : "";
        String stateTwoIconClass
                = checkbox.getStateTwoIcon() != null ? TriStateCheckbox.UI_ICON + checkbox.getStateTwoIcon()
                                                     : TriStateCheckbox.UI_ICON + "ui-icon-check";
        String stateThreeIconClass
                = checkbox.getStateThreeIcon() != null ? TriStateCheckbox.UI_ICON + checkbox.getStateThreeIcon()
                                                       : TriStateCheckbox.UI_ICON + "ui-icon-closethick";

        String statesIconsClasses = "[\"" + stateOneIconClass + "\",\"" + stateTwoIconClass + "\",\"" + stateThreeIconClass + "\"]";

        String stateOneTitle = checkbox.getStateOneTitle() == null ? "" : checkbox.getStateOneTitle();
        String stateTwoTitle = checkbox.getStateTwoTitle() == null ? "" : checkbox.getStateTwoTitle();
        String stateThreeTitle = checkbox.getStateThreeTitle() == null ? "" : checkbox.getStateThreeTitle();

        String statesTitles = "{\"titles\": [\""
                + EscapeUtils.forJavaScript(stateOneTitle)
                + "\",\""
                + EscapeUtils.forJavaScript(stateTwoTitle)
                + "\",\""
                + EscapeUtils.forJavaScript(stateThreeTitle)
                + "\"]}";

        String iconClass = "ui-chkbox-icon ui-c"; //HTML.CHECKBOX_ICON_CLASS;
        String activeTitle = "";
        if (valCheck == 0) {
            iconClass = iconClass + " " + stateOneIconClass;
            activeTitle = stateOneTitle;
        }
        else if (valCheck == 1) {
            iconClass = iconClass + " " + stateTwoIconClass;
            activeTitle = stateTwoTitle;
        }
        else if (valCheck == 2) {
            iconClass = iconClass + " " + stateThreeIconClass;
            activeTitle = stateThreeTitle;
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("data-iconstates", statesIconsClasses, null);
        if (!LangUtils.isValueBlank(stateOneTitle) || !LangUtils.isValueBlank(stateTwoTitle) || !LangUtils.isValueBlank(stateThreeTitle)) {
            writer.writeAttribute("title", activeTitle, null);
            writer.writeAttribute("data-titlestates", statesTitles, null);
        }
        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");
        writer.endElement("div");
    }

    protected void encodeItemLabel(final FacesContext context, final TriStateCheckbox checkbox) throws IOException {
        String label = checkbox.getItemLabel();

        if (label != null) {
            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("span", null);
            writer.writeAttribute("class", HTML.CHECKBOX_LABEL_CLASS, null);
            writer.writeText(label, "itemLabel");
            writer.endElement("span");
        }
    }

    protected void encodeScript(final FacesContext context, final TriStateCheckbox checkbox) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TriStateCheckbox", checkbox.resolveWidgetVar(context), checkbox.getClientId());
        encodeClientBehaviors(context, checkbox);
        wb.finish();
    }
}
