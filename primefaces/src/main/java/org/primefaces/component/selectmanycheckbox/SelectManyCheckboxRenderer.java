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
package org.primefaces.component.selectmanycheckbox;

import org.primefaces.renderkit.SelectManyRenderer;
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
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import jakarta.faces.render.FacesRenderer;
import jakarta.faces.render.Renderer;

@FacesRenderer(rendererType = SelectManyCheckbox.DEFAULT_RENDERER, componentFamily = SelectManyCheckbox.COMPONENT_FAMILY)
public class SelectManyCheckboxRenderer extends SelectManyRenderer<SelectManyCheckbox> {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "jakarta.faces.SelectMany",
                "jakarta.faces.Checkbox");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, SelectManyCheckbox component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, SelectManyCheckbox component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String layout = component.getLayout();
        if (LangUtils.isEmpty(layout)) {
            layout = FacetUtils.shouldRenderFacet(component.getFacet("custom")) ? "custom" : "lineDirection";
        }
        boolean custom = ("custom".equals(layout));

        if (custom) {
            writer.startElement("span", component);
            writer.writeAttribute("id", component.getClientId(context), "id");
            writer.writeAttribute("class", "ui-helper-hidden", null);
            renderARIARequired(context, component);
            encodeCustomLayout(context, component);
            writer.endElement("span");
        }
        else if ("grid".equals(layout)) {
            throw new FacesException(layout + " is not a valid value for SelectManyCheckbox layout.");
        }
        else {
            encodeResponsiveLayout(context, component, layout);
        }
    }

    protected void encodeScript(FacesContext context, SelectManyCheckbox component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        String layout = component.getLayout();
        boolean custom = "custom".equals(layout);

        wb.init("SelectManyCheckbox", component)
                .attr("custom", custom, false).finish();
    }

    protected void encodeResponsiveLayout(FacesContext context, SelectManyCheckbox component, String layout) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        boolean flex = ComponentUtils.isFlex(context, component);
        if (flex) {
            layout = "responsive";
        }
        boolean lineDirection = "lineDirection".equals(layout);
        String styleClass = getStyleClassBuilder(context)
                .add(lineDirection, "layout-line-direction")
                .add(SelectManyCheckbox.STYLE_CLASS)
                .add(GridLayoutUtils.getResponsiveClass(flex))
                .add(component.getStyleClass())
                .build();
        int columns = component.getColumns();

        if (lineDirection || "pageDirection".equals(layout)) {
            columns = 1;
        }

        if (columns <= 0) {
            throw new FacesException("The value of columns attribute must be greater than zero.");
        }

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        renderARIARequired(context, component);

        List<SelectItem> selectItems = getSelectItems(context, component);
        Converter converter = component.getConverter();
        Object values = getValues(component);
        Object submittedValues = getSubmittedValues(component);

        int idx = 0;
        int groupIdx = 0;
        int colMod = 0;

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            if (selectItem instanceof SelectItemGroup) {
                writer.startElement("div", null);
                writer.writeAttribute("class", "ui-selectmanycheckbox-responsive-group", null);
                encodeGroupLabel(context, component, (SelectItemGroup) selectItem);
                writer.endElement("div");

                if (flex) {
                    writer.startElement("div", null);
                    writer.writeAttribute("class", GridLayoutUtils.getFlexGridClass(true), null);
                }

                for (SelectItem childSelectItem : ((SelectItemGroup) selectItem).getSelectItems()) {
                    colMod = idx % columns;
                    if (!flex && !lineDirection && colMod == 0) {
                        writer.startElement("div", null);
                        writer.writeAttribute("class", GridLayoutUtils.getFlexGridClass(false), null);
                    }

                    groupIdx++;

                    writer.startElement("div", null);
                    writer.writeAttribute("class", GridLayoutUtils.getColumnClass(flex, columns), null);
                    encodeOption(context, component, values, submittedValues, converter, childSelectItem, groupIdx);
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

                idx = 0;
            }
            else {
                colMod = idx % columns;
                if ((flex && idx == 0) || (!flex && !lineDirection && colMod == 0)) {
                    writer.startElement("div", null);
                    writer.writeAttribute("class", GridLayoutUtils.getFlexGridClass(flex), null);
                }

                writer.startElement("div", null);
                writer.writeAttribute("class", GridLayoutUtils.getColumnClass(flex, columns), null);
                encodeOption(context, component, values, submittedValues, converter, selectItem, idx);
                writer.endElement("div");

                idx++;
                colMod = idx % columns;

                if (!flex && !lineDirection && colMod == 0) {
                    writer.endElement("div");
                }
            }
        }

        if (idx != 0 && (flex || (!flex && (idx % columns) != 0))) {
            writer.endElement("div");
        }

        writer.endElement("div");
    }

    protected void encodeOptionInput(FacesContext context, SelectManyCheckbox component, String id, String name, boolean checked,
                                     boolean disabled, String value) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String tabindex = component.getTabindex();

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("value", value, null);
        if (tabindex != null) {
            writer.writeAttribute("tabindex", tabindex, null);
        }

        renderOnchange(context, component);

        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }
        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }

        renderValidationMetadata(context, component);

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeOptionLabel(FacesContext context, SelectManyCheckbox component, String containerClientId, SelectItem option,
                                     boolean disabled) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("label", null);
        if (disabled) {
            writer.writeAttribute("class", "ui-state-disabled", null);
        }

        writer.writeAttribute("for", containerClientId, null);

        if (option.getDescription() != null) {
            writer.writeAttribute("title", option.getDescription(), null);
        }

        if (option.isEscape()) {
            writer.writeText(option.getLabel(), null);
        }
        else {
            writer.write(option.getLabel());
        }

        writer.endElement("label");
    }

    protected void encodeGroupLabel(FacesContext context, SelectManyCheckbox component, SelectItemGroup group) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-selectmanycheckbox-item-group", null);

        if (group.isEscape()) {
            writer.writeText(group.getLabel(), null);
        }
        else {
            writer.write(group.getLabel());
        }

        writer.endElement("span");
    }

    protected void encodeOptionOutput(FacesContext context, SelectManyCheckbox component, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String boxClass = createStyleClass(component, null, HTML.CHECKBOX_BOX_CLASS);
        boxClass = checked ? boxClass + " ui-state-active" : boxClass;
        boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;

        String iconClass = checked ? HTML.CHECKBOX_CHECKED_ICON_CLASS : HTML.CHECKBOX_UNCHECKED_ICON_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeCustomLayout(FacesContext context, SelectManyCheckbox component) throws IOException {
        List<SelectItem> selectItems = getSelectItems(context, component);
        Converter converter = component.getConverter();
        Object values = getValues(component);
        Object submittedValues = getSubmittedValues(component);

        int idx = 0;
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            String itemValueAsString = getOptionAsString(context, component, converter, selectItem.getValue());
            String name = component.getClientId(context);
            String id = name + UINamingContainer.getSeparatorChar(context) + idx;

            Object valuesArray;
            Object itemValue;
            if (submittedValues != null) {
                valuesArray = submittedValues;
                itemValue = itemValueAsString;
            }
            else {
                valuesArray = values;
                itemValue = selectItem.getValue();
            }

            boolean selected = isSelected(context, component, itemValue, valuesArray, converter);
            if (selectItem.isNoSelectionOption() && values != null && !selected) {
                return;
            }

            boolean disabled = selectItem.isDisabled() || component.isDisabled();

            encodeOptionInput(context, component, id, name, selected, disabled, itemValueAsString);
            idx++;
        }
    }

    protected void encodeOption(FacesContext context, SelectManyCheckbox component, Object values, Object submittedValues, Converter converter,
                                SelectItem option, int idx) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, component, converter, option.getValue());
        String name = component.getClientId(context);
        String id = name + UINamingContainer.getSeparatorChar(context) + idx;
        boolean disabled = option.isDisabled() || component.isDisabled();

        Object valuesArray;
        Object itemValue;
        if (submittedValues != null) {
            valuesArray = submittedValues;
            itemValue = itemValueAsString;
        }
        else {
            valuesArray = values;
            itemValue = option.getValue();
        }

        boolean selected = isSelected(context, component, itemValue, valuesArray, converter);
        if (option.isNoSelectionOption() && values != null && !selected) {
            return;
        }

        writer.startElement("div", getSelectItemComponent(option));
        writer.writeAttribute("class", HTML.CHECKBOX_CLASS, null);

        encodeOptionInput(context, component, id, name, selected, disabled, itemValueAsString);
        encodeOptionOutput(context, component, selected, disabled);

        writer.endElement("div");
        encodeOptionLabel(context, component, id, option, disabled);
    }

    @Override
    protected String getSubmitParam(FacesContext context, SelectManyCheckbox component) {
        return component.getClientId(context);
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
