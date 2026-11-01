/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.tristatemanycheckbox;

import org.primefaces.cdk.api.FacesRendererInfo;
import org.primefaces.component.tristatecheckbox.TriStateCheckbox;
import org.primefaces.renderkit.SelectRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.faces.FacesException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = TriStateManyCheckbox.DEFAULT_RENDERER, componentFamily = TriStateManyCheckbox.COMPONENT_FAMILY)
@FacesRendererInfo
public class TriStateManyCheckboxRenderer extends SelectRenderer<TriStateManyCheckbox> {

    /**
     * Decodes the state of each item into a Map of the item values to the submitted states ("0", "1", "2"). A disabled
     * item keeps its state, even if a state was submitted for it (client side manipulation).
     */
    @Override
    public void decode(FacesContext context, TriStateManyCheckbox component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        List<SelectItem> selectItems = getSelectItems(context, component);
        Map<?, ?> values = getValues(component);
        Map<Object, String> submittedValue = new LinkedHashMap<>();

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            Object itemValue = selectItem.getValue();
            String state = selectItem.isDisabled() ? null : params.get(component.getItemClientId(context, i));
            if (state == null) {
                state = TriStateManyCheckbox.STATES.get(component.getState(context, values == null ? null : values.get(itemValue)));
            }
            submittedValue.put(itemValue, state);
        }

        component.setSubmittedValue(submittedValue);
    }

    /**
     * Converts the submitted states with the converter of the component, the keys (item values) stay as they are.
     */
    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Converter converter = ((UIInput) component).getConverter();
        Map<Object, Object> value = new LinkedHashMap<>();

        for (Map.Entry<?, ?> entry : ((Map<?, ?>) submittedValue).entrySet()) {
            String state = (String) entry.getValue();
            if (!TriStateManyCheckbox.STATES.contains(state)) {
                throw new ConverterException(MessageFactory.getFacesMessage(context, UIInput.CONVERSION_MESSAGE_ID,
                        FacesMessage.SEVERITY_ERROR, ComponentUtils.getLabel(context, component)));
            }
            value.put(entry.getKey(), converter == null ? state : converter.getAsObject(context, component, state));
        }

        return value;
    }

    @Override
    public void encodeEnd(FacesContext context, TriStateManyCheckbox component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, TriStateManyCheckbox component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String layout = LangUtils.isEmpty(component.getLayout()) ? "lineDirection" : component.getLayout();
        if (!"lineDirection".equals(layout) && !"pageDirection".equals(layout)) {
            throw new FacesException(layout + " is not a valid value for TriStateManyCheckbox layout.");
        }

        String styleClass = getStyleClassBuilder(context)
                .add(TriStateManyCheckbox.STYLE_CLASS)
                .add("pageDirection".equals(layout), TriStateManyCheckbox.VERTICAL_CLASS)
                .add(component.getStyleClass())
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_GROUP, null);

        List<SelectItem> selectItems = getSelectItems(context, component);
        // after a failed validation the submitted states, otherwise the states of the value
        Map<?, ?> submittedValue = (Map<?, ?>) component.getSubmittedValue();
        Map<?, ?> values = getValues(component);
        String[] icons = getIcons(component);
        String[] titles = getTitles(component);

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            int state = submittedValue != null
                    ? Math.max(0, TriStateManyCheckbox.STATES.indexOf(submittedValue.get(selectItem.getValue())))
                    : component.getState(context, values == null ? null : values.get(selectItem.getValue()));
            encodeItem(context, component, selectItem, i, state, icons[state], titles == null ? null : titles[state]);
        }

        writer.endElement("div");
    }

    protected void encodeItem(FacesContext context, TriStateManyCheckbox component, SelectItem selectItem, int index, int state,
            String iconClass, String title) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = component.getItemClientId(context, index);
        boolean disabled = component.isDisabled() || selectItem.isDisabled();
        boolean readonly = component.isReadonly();

        writer.startElement("div", getSelectItemComponent(selectItem));
        writer.writeAttribute("class", HTML.CHECKBOX_CLASS, null);

        // the input holds the state and gets the focus, the box shows the state
        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.CHECKBOX_INPUT_WRAPPER_CLASS, null);
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("value", TriStateManyCheckbox.STATES.get(state), null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("readonly", "readonly", null);
        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        if (component.getTabindex() != null) {
            writer.writeAttribute("tabindex", component.getTabindex(), null);
        }
        if (component.getOnchange() != null) {
            writer.writeAttribute("onchange", component.getOnchange(), null);
        }
        renderARIAInvalid(context, component);
        writer.endElement("input");
        writer.endElement("div");

        String boxClass = getStyleClassBuilder(context)
                .add(HTML.CHECKBOX_BOX_CLASS)
                .add(state != 0, "p-state-active")
                .add(!component.isValid(), "p-state-error")
                .add(disabled, "p-state-disabled")
                .add(readonly, "p-chkbox-readonly")
                .build();
        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);
        if (title != null) {
            writer.writeAttribute("title", title, null);
        }
        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");
        writer.endElement("div");

        String labelClass = getStyleClassBuilder(context)
                .add(HTML.CHECKBOX_LABEL_CLASS)
                .add(disabled, "p-state-disabled")
                .add(readonly, "p-chkbox-readonly")
                .build();
        writer.startElement("label", null);
        writer.writeAttribute("for", inputId, null);
        writer.writeAttribute("class", labelClass, null);
        if (selectItem.getDescription() != null) {
            writer.writeAttribute("title", selectItem.getDescription(), null);
        }
        if (selectItem.isEscape()) {
            writer.writeText(selectItem.getLabel(), null);
        }
        else {
            writer.write(selectItem.getLabel());
        }
        writer.endElement("label");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, TriStateManyCheckbox component) throws IOException {
        String[] titles = getTitles(component);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TriStateManyCheckbox", component)
                .nativeAttr("icons", toJsArray(getIcons(component)))
                .attr("readonly", component.isReadonly(), false);
        if (titles != null) {
            wb.nativeAttr("titles", toJsArray(titles));
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    /**
     * @return the style classes of the icon of each state: the icon of the state or, without, the one of the theme
     */
    protected String[] getIcons(TriStateManyCheckbox component) {
        return new String[] {
            component.getStateOneIcon() == null
                    ? HTML.CHECKBOX_UNCHECKED_ICON_CLASS
                    : TriStateManyCheckbox.ICON_CLASS + " " + component.getStateOneIcon(),
            component.getStateTwoIcon() == null
                    ? HTML.CHECKBOX_CHECKED_ICON_CLASS
                    : TriStateManyCheckbox.ICON_CLASS + " " + component.getStateTwoIcon(),
            component.getStateThreeIcon() == null
                    ? TriStateCheckbox.CROSSED_ICON_CLASS
                    : TriStateManyCheckbox.ICON_CLASS + " " + component.getStateThreeIcon()
        };
    }

    /**
     * @return the title of each state, {@code null} if no state has one
     */
    protected String[] getTitles(TriStateManyCheckbox component) {
        String[] titles = Stream.of(component.getStateOneTitle(), component.getStateTwoTitle(), component.getStateThreeTitle())
                .map(title -> title == null ? "" : title)
                .toArray(String[]::new);
        return Stream.of(titles).allMatch(LangUtils::isBlank) ? null : titles;
    }

    /**
     * @return the select items of the component, groups are not supported
     */
    @Override
    protected List<SelectItem> getSelectItems(FacesContext context, TriStateManyCheckbox component) {
        List<SelectItem> selectItems = super.getSelectItems(context, component);
        if (selectItems.stream().anyMatch(SelectItemGroup.class::isInstance)) {
            throw new FacesException("SelectItemGroup is not supported by TriStateManyCheckbox '" + component.getClientId(context) + "'");
        }
        return selectItems;
    }

    protected String toJsArray(String[] values) {
        return Stream.of(values)
                .map(value -> "\"" + EscapeUtils.forJavaScript(value) + "\"")
                .collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * @return the value of the component, a Map of the item values to their states
     */
    protected Map<?, ?> getValues(TriStateManyCheckbox component) {
        Object value = component.getValue();
        if (value == null || value instanceof Map) {
            return (Map<?, ?>) value;
        }
        throw new FacesException("Value of '" + component.getClientId() + "' must be a Map instance");
    }
}
