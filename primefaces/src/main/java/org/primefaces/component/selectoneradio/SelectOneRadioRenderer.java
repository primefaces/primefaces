/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.selectoneradio;

import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.GridLayoutUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.component.UISelectOne;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.model.SelectItem;
import jakarta.faces.render.Renderer;

public class SelectOneRadioRenderer extends SelectOneRenderer {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "jakarta.faces.SelectOne",
                "jakarta.faces.Radio");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectOneRadio radio = (SelectOneRadio) component;

        encodeMarkup(context, radio);
        encodeScript(context, radio);
    }

    protected void encodeMarkup(FacesContext context, SelectOneRadio radio) throws IOException {
        String layout = radio.getLayout();
        if (LangUtils.isEmpty(layout)) {
            layout = FacetUtils.shouldRenderFacet(radio.getFacet("custom")) ? "custom" : "lineDirection";
        }
        boolean custom = "custom".equals(layout);

        if (custom) {
            encodeCustomLayout(context, radio);
        }
        else if ("grid".equals(layout)) {
            throw new FacesException(layout + " is not a valid value for SelectOneRadio layout.");
        }
        else {
            encodeResponsiveLayout(context, radio, layout);
        }
    }

    protected void encodeScript(FacesContext context, SelectOneRadio radio) throws IOException {
        String layout = radio.getLayout();
        if (LangUtils.isEmpty(layout) && FacetUtils.shouldRenderFacet(radio.getFacet("custom"))) {
            layout = "custom";
        }
        boolean custom = "custom".equals(layout);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectOneRadio", radio)
                .attr("custom", custom, false)
                .attr("unselectable", radio.isUnselectable())
                .attr("readonly", radio.isReadonly(), false)
                .finish();
    }

    protected void encodeResponsiveLayout(FacesContext context, SelectOneRadio radio, String layout) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = radio.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, radio);
        String columnClassesValue = radio.getColumnClasses();
        String[] columnClasses = columnClassesValue == null ? new String[0] : columnClassesValue.split(",");
        String style = radio.getStyle();
        boolean flex = ComponentUtils.isFlex(context, radio);
        if (flex) {
            layout = "responsive";
        }
        boolean lineDirection = "lineDirection".equals(layout);
        String styleClass = getStyleClassBuilder(context)
                .add(lineDirection, "layout-line-direction")
                .add(GridLayoutUtils.getResponsiveClass(flex))
                .add(radio.getStyleClass())
                .add(SelectOneRadio.STYLE_CLASS)
                .add(radio.isReadonly(), "ui-state-readonly")
                .build();
        String labelledBy = radio.getLabelledBy();

        writer.startElement("div", radio);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("role", "radiogroup", null);
        if (labelledBy != null) {
            writer.writeAttribute(HTML.ARIA_LABELLEDBY, labelledBy, "label");
        }
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        renderARIARequired(context, radio);

        Converter converter = radio.getConverter();
        String name = radio.getClientId(context);
        String currentValue = ComponentUtils.getValueToRender(context, radio);

        int columns = radio.getColumns();
        if (lineDirection || "pageDirection".equals(layout)) {
            columns = 1;
        }

        if (columns > 0) {
            int idx = 0;
            int colMod;

            if (flex) {
                writer.startElement("div", null);
                writer.writeAttribute("class", GridLayoutUtils.getFlexGridClass(true), null);
            }

            for (int i = 0; i < selectItems.size(); i++) {
                SelectItem selectItem = selectItems.get(i);
                boolean disabled = selectItem.isDisabled() || radio.isDisabled();
                String id = name + UINamingContainer.getSeparatorChar(context) + idx;
                boolean selected = isSelected(context, radio, selectItem, currentValue);
                colMod = idx % columns;
                if (!flex && !lineDirection && colMod == 0) {
                    writer.startElement("div", null);
                    writer.writeAttribute("class", GridLayoutUtils.getFlexGridClass(false), null);
                }

                String columnClass = (colMod < columnClasses.length) ? columnClasses[colMod].trim() : "";
                if (!columnClass.contains("md-") && !columnClass.contains("col-") && !lineDirection) {
                    columnClass += (!"".equals(columnClass) ? " " : "") + GridLayoutUtils.getColumnClass(flex, columns);
                }

                writer.startElement("div", null);
                writer.writeAttribute("class", columnClass, null);
                writer.writeAttribute(HTML.ARIA_CHECKED, Boolean.toString(selected), null);
                encodeOption(context, radio, selectItem, id, name, converter, selected, disabled);
                writer.endElement("div");

                idx++;
                colMod = idx % columns;

                if (!flex && !lineDirection && colMod == 0) {
                    writer.endElement("div");
                }
            }

            if (flex || (!flex && idx != 0 && (idx % columns) != 0)) {
                writer.endElement("div");
            }
        }
        else {
            throw new FacesException("The value of columns attribute must be greater than zero.");
        }

        writer.endElement("div");
    }

    protected void encodeCustomLayout(FacesContext context, SelectOneRadio radio) throws IOException {
        UIComponent customFacet = radio.getFacet("custom");
        if (FacetUtils.shouldRenderFacet(customFacet)) {
            ResponseWriter writer = context.getResponseWriter();
            String style = radio.getStyle();
            String styleClass = getStyleClassBuilder(context)
                    .add(radio.getStyleClass())
                    .add(SelectOneRadio.STYLE_CLASS)
                    .add(radio.isReadonly(), "ui-state-readonly")
                    .build();
            String labelledBy = radio.getLabelledBy();
            writer.startElement("span", radio);
            writer.writeAttribute("id", radio.getClientId(context), "id");
            writer.writeAttribute("role", "radiogroup", null);
            if (labelledBy != null) {
                writer.writeAttribute(HTML.ARIA_LABELLEDBY, labelledBy, "label");
            }
            if (style != null) {
                writer.writeAttribute("style", style, "style");
            }
            if (styleClass != null) {
                writer.writeAttribute("class", styleClass, "styleClass");
            }

            encodeCustomLayoutHelper(context, radio, false);
            customFacet.encodeAll(context);

            writer.endElement("span");
        }
        else {
            encodeCustomLayoutHelper(context, radio, true);
        }
    }

    protected void encodeCustomLayoutHelper(FacesContext context, SelectOneRadio radio, boolean addId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("span", radio);
        if (addId) {
            writer.writeAttribute("id", radio.getClientId(context), "id");
        }
        writer.writeAttribute("class", "ui-helper-hidden", null);

        Converter converter = radio.getConverter();
        String name = radio.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, radio);
        String currentValue = ComponentUtils.getValueToRender(context, radio);

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            String id = name + UINamingContainer.getSeparatorChar(context) + i;
            boolean selected = isSelected(context, radio, selectItem, currentValue);
            boolean disabled = selectItem.isDisabled() || radio.isDisabled();
            String itemValueAsString = getOptionAsString(context, radio, converter, selectItem.getValue());
            encodeOptionInput(context, radio, id, name, selected, disabled, itemValueAsString);
        }

        writer.endElement("span");
    }

    protected void encodeOption(FacesContext context, SelectOneRadio radio, SelectItem option, String id, String name,
                                Converter converter, boolean selected, boolean disabled) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, radio, converter, option.getValue());
        String styleClass = HTML.RADIOBUTTON_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        encodeOptionInput(context, radio, id, name, selected, disabled, itemValueAsString);
        encodeOptionOutput(context, radio, selected, disabled);

        writer.endElement("div");

        encodeOptionLabel(context, radio, id, option, disabled);
    }

    protected void encodeOptionInput(FacesContext context, SelectOneRadio radio, String id, String name, boolean checked,
                                     boolean disabled, String value) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", value, null);

        renderDomEvents(context, radio, SelectOneRadio.DOM_EVENTS);

        if (radio.getTabindex() != null) {
            writer.writeAttribute("tabindex", radio.getTabindex(), null);
        }
        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }
        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }

        renderValidationMetadata(context, radio);

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeOptionLabel(FacesContext context, SelectOneRadio radio, String containerClientId, SelectItem option,
                                     boolean disabled) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String label = option.getLabel();

        writer.startElement("label", null);
        writer.writeAttribute("for", containerClientId, null);
        if (disabled) {
            writer.writeAttribute("class", "ui-state-disabled", null);
        }

        if (option.getDescription() != null) {
            writer.writeAttribute("title", option.getDescription(), null);
        }

        if (label != null) {
            if (option.isEscape()) {
                writer.writeText(label, null);
            }
            else {
                writer.write(label);
            }
        }

        writer.endElement("label");
    }

    protected void encodeOptionOutput(FacesContext context, SelectOneRadio radio, boolean selected, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String boxClass = createStyleClass(radio, null, HTML.RADIOBUTTON_BOX_CLASS);
        boxClass = selected ? boxClass + " ui-state-active" : boxClass;
        boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
        String iconClass = selected ? HTML.RADIOBUTTON_CHECKED_ICON_CLASS : HTML.RADIOBUTTON_UNCHECKED_ICON_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected boolean isSelected(FacesContext context, SelectOneRadio radio, SelectItem selectItem, String currentValue) {
        String itemStrValue = getOptionAsString(context, radio, radio.getConverter(), selectItem.getValue());
        return LangUtils.isBlank(itemStrValue)
                ? LangUtils.isBlank(currentValue)
                : itemStrValue.equals(currentValue);
    }

    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return selectOne.getClientId(context);
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public String getHighlighter() {
        return "oneradio";
    }

    @Override
    protected boolean isGrouped() {
        return true;
    }

}
