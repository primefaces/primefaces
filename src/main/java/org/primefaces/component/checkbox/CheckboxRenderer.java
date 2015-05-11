/*
 * Copyright 2009-2014 PrimeTek.
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
        String inputId = selectManyCheckbox.getCheckboxId(context);
        String clientId = checkbox.getClientId(context);
        boolean disabled = checkbox.isDisabled() || selectManyCheckbox.isDisabled();

        String style = checkbox.getStyle();
        String styleClass = checkbox.getStyleClass();
        styleClass = styleClass == null ? HTML.CHECKBOX_CLASS : HTML.CHECKBOX_CLASS + " " + styleClass;

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeOptionInput(context, selectManyCheckbox, checkbox, inputId, masterClientId, disabled);
        encodeOptionOutput(context, disabled);

        writer.endElement("div");
    }

    protected void encodeOptionInput(FacesContext context, SelectManyCheckbox selectManyCheckbox, Checkbox checkbox, String id, String name, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String tabindex = checkbox.getTabindex();
        if(tabindex == null) {
            tabindex = selectManyCheckbox.getTabindex();
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", id + "_clone", null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("class", "ui-chkbox-clone", null);
        writer.writeAttribute("data-itemindex", checkbox.getItemIndex(), null);

        if(tabindex != null) writer.writeAttribute("tabindex", tabindex, null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);

        String onchange = buildEvent(context, selectManyCheckbox, checkbox, "onchange", "change", "valueChange");
        if (!isValueBlank(onchange)) {
            writer.writeAttribute("onchange", onchange, null);
        }
        String onclick = buildEvent(context, selectManyCheckbox, checkbox, "onclick", "click", "click");
        if (!isValueBlank(onclick)) {
            writer.writeAttribute("onclick", onclick, null);
        }

        writer.endElement("input");
        writer.endElement("div");
    }

    protected String buildEvent(FacesContext context, SelectManyCheckbox selectManyCheckbox, Checkbox checkbox, String domEvent, String behaviorEvent,
            String behaviorEventAlias) {

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

    protected void encodeOptionOutput(FacesContext context, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String boxClass = HTML.CHECKBOX_BOX_CLASS;
        boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;

        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.CHECKBOX_UNCHECKED_ICON_CLASS, null);
        writer.endElement("span");

        writer.endElement("div");
    }
}
