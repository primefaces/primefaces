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
package org.primefaces.component.radiobutton;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;

public class RadioButtonRenderer extends InputRenderer {

    private static final String SB_BUILD_EVENT = RadioButtonRenderer.class.getName() + "#buildEvent";

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        RadioButton radioButton = (RadioButton) component;
        SelectOneRadio selectOneRadio = (SelectOneRadio) SearchExpressionFacade.resolveComponent(
                context, radioButton, radioButton.getFor());

        encodeMarkup(context, radioButton, selectOneRadio);
    }

    protected void encodeMarkup(FacesContext context, RadioButton radio, SelectOneRadio selectOneRadio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String masterClientId = selectOneRadio.getClientId(context);
        String inputId = selectOneRadio.getRadioButtonId(context);
        String clientId = radio.getClientId(context);
        boolean disabled = radio.isDisabled() || selectOneRadio.isDisabled();

        String style = radio.getStyle();
        String defaultStyleClass = selectOneRadio.isPlain() ? HTML.RADIOBUTTON_NATIVE_CLASS : HTML.RADIOBUTTON_CLASS;
        String styleClass = radio.getStyleClass();
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass + " " + styleClass;

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeOptionInput(context, selectOneRadio, radio, inputId, masterClientId, disabled);
        encodeOptionOutput(context, disabled, selectOneRadio);

        writer.endElement("div");
    }

    protected void encodeOptionInput(FacesContext context, SelectOneRadio radio, RadioButton button, String id, String name,
                                     boolean disabled) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String tabindex = button.getTabindex();
        if (tabindex == null) {
            tabindex = radio.getTabindex();
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", id + "_clone", null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("class", "ui-radio-clone", null);
        writer.writeAttribute("data-itemindex", button.getItemIndex(), null);

        renderAccessibilityAttributes(context, radio, disabled, radio.isReadonly());

        if (tabindex != null) {
            writer.writeAttribute("tabindex", tabindex, null);
        }

        String onchange = buildEvent(context, radio, button, "onchange", "change", "valueChange");
        if (!isValueBlank(onchange)) {
            writer.writeAttribute("onchange", onchange, null);
        }
        String onclick = buildEvent(context, radio, button, "onclick", "click", "click");
        if (!isValueBlank(onclick)) {
            writer.writeAttribute("onclick", onclick, null);
        }

        writer.endElement("input");
        writer.endElement("div");
    }

    protected String buildEvent(FacesContext context, SelectOneRadio radio, RadioButton button, String domEvent, String behaviorEvent,
                                String behaviorEventAlias) {

        String radioEvent = buildDomEvent(context, radio, domEvent, behaviorEvent, behaviorEventAlias, null);
        String buttonEvent = buildDomEvent(context, button, domEvent, behaviorEvent, behaviorEventAlias, null);

        StringBuilder eventBuilder = SharedStringBuilder.get(context, SB_BUILD_EVENT);
        if (radioEvent != null) {
            eventBuilder.append(radioEvent);
        }
        if (buttonEvent != null) {
            eventBuilder.append(buttonEvent);
        }

        return eventBuilder.toString();
    }

    protected void encodeOptionOutput(FacesContext context, boolean disabled, SelectOneRadio selectOneRadio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String boxClass = HTML.RADIOBUTTON_BOX_CLASS;
        boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
        boxClass = !selectOneRadio.isValid() ? boxClass + " ui-state-error" : boxClass;

        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.RADIOBUTTON_UNCHECKED_ICON_CLASS, null);
        writer.endElement("span");

        writer.endElement("div");
    }

}
