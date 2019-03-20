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
package org.primefaces.component.checkbox;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.radiobutton.RadioButtonRenderer;
import org.primefaces.component.selectmanycheckbox.SelectManyCheckbox;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;

public class CheckboxRenderer extends InputRenderer {

    private static final String SB_BUILD_EVENT = RadioButtonRenderer.class.getName() + "#buildEvent";

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Checkbox checkbox = (Checkbox) component;
        SelectManyCheckbox selectManyCheckbox = (SelectManyCheckbox) SearchExpressionFacade.resolveComponent(
                context, checkbox, checkbox.getFor());

        encodeMarkup(context, checkbox, selectManyCheckbox);
    }

    protected void encodeMarkup(FacesContext context, Checkbox checkbox, SelectManyCheckbox selectManyCheckbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String masterClientId = selectManyCheckbox.getClientId(context);
        String clientId = checkbox.getClientId(context);
        boolean disabled = checkbox.isDisabled() || selectManyCheckbox.isDisabled();

        String style = checkbox.getStyle();
        String styleClass = checkbox.getStyleClass();
        styleClass = styleClass == null ? HTML.CHECKBOX_CLASS : HTML.CHECKBOX_CLASS + " " + styleClass;

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeOptionInput(context, selectManyCheckbox, checkbox, clientId, masterClientId, disabled);
        encodeOptionOutput(context, disabled, selectManyCheckbox);

        writer.endElement("div");
    }

    protected void encodeOptionInput(FacesContext context, SelectManyCheckbox selectManyCheckbox, Checkbox checkbox, String id, String name,
                                     boolean disabled) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String tabindex = checkbox.getTabindex();
        if (tabindex == null) {
            tabindex = selectManyCheckbox.getTabindex();
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("class", "ui-chkbox-clone", null);
        writer.writeAttribute("data-itemindex", checkbox.getItemIndex(), null);

        if (tabindex != null) {
            writer.writeAttribute("tabindex", tabindex, null);
        }

        String onchange = buildEvent(context, selectManyCheckbox, checkbox, "onchange", "change", "valueChange");
        if (!isValueBlank(onchange)) {
            writer.writeAttribute("onchange", onchange, null);
        }
        String onclick = buildEvent(context, selectManyCheckbox, checkbox, "onclick", "click", "click");
        if (!isValueBlank(onclick)) {
            writer.writeAttribute("onclick", onclick, null);
        }

        renderAccessibilityAttributes(context, selectManyCheckbox, disabled, selectManyCheckbox.isReadonly());
        renderValidationMetadata(context, selectManyCheckbox);

        writer.endElement("input");
        writer.endElement("div");
    }

    protected String buildEvent(FacesContext context, SelectManyCheckbox selectManyCheckbox, Checkbox checkbox, String domEvent,
                                String behaviorEvent, String behaviorEventAlias) {

        String manyCheckboxEvent = buildDomEvent(context, selectManyCheckbox, domEvent, behaviorEvent, behaviorEventAlias, null);
        String checkboxEvent = buildDomEvent(context, checkbox, domEvent, behaviorEvent, behaviorEventAlias, null);

        StringBuilder eventBuilder = SharedStringBuilder.get(context, SB_BUILD_EVENT);
        if (manyCheckboxEvent != null) {
            eventBuilder.append(manyCheckboxEvent);
        }
        if (checkboxEvent != null) {
            eventBuilder.append(checkboxEvent);
        }

        return eventBuilder.toString();
    }

    protected void encodeOptionOutput(FacesContext context, boolean disabled, SelectManyCheckbox selectManyCheckbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String boxClass = HTML.CHECKBOX_BOX_CLASS;
        boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
        boxClass = !selectManyCheckbox.isValid() ? boxClass + " ui-state-error" : boxClass;

        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.CHECKBOX_UNCHECKED_ICON_CLASS, null);
        writer.endElement("span");

        writer.endElement("div");
    }

    @Override
    public String getHighlighter() {
        return "manychkbox";
    }

    @Override
    protected boolean isGrouped() {
        return true;
    }
}
