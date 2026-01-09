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
package org.primefaces.component.selectcheckboxmenu;

import org.primefaces.component.column.Column;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.SelectManyRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

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

@FacesRenderer(rendererType = SelectCheckboxMenu.DEFAULT_RENDERER, componentFamily = SelectCheckboxMenu.COMPONENT_FAMILY)
public class SelectCheckboxMenuRenderer extends SelectManyRenderer<SelectCheckboxMenu> {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "jakarta.faces.SelectMany",
                "jakarta.faces.Checkbox");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeChildren(FacesContext facesContext, SelectCheckboxMenu component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public void encodeEnd(FacesContext context, SelectCheckboxMenu component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    protected void encodeMarkup(FacesContext context, SelectCheckboxMenu component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, component);
        boolean valid = component.isValid();
        String title = component.getTitle();

        String style = component.getStyle();
        String styleclass = createStyleClass(component, SelectCheckboxMenu.STYLE_CLASS);
        styleclass = component.isMultiple() ? SelectCheckboxMenu.MULTIPLE_CLASS + " " + styleclass : styleclass;

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleclass, "styleclass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        if (title != null) {
            writer.writeAttribute("title", title, "title");
        }

        encodeKeyboardTarget(context, component);
        encodeInputs(context, component, selectItems);
        if (component.isMultiple()) {
            encodeMultipleLabel(context, component, selectItems);
        }
        else {
            encodeLabel(context, component, valid);
        }

        encodeMenuIcon(context, component, valid);
        encodePanel(context, component, selectItems);

        writer.endElement("div");
    }

    protected void encodeInputs(FacesContext context, SelectCheckboxMenu component, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = component.getConverter();
        Object values = getValues(component);
        Object submittedValues = getSubmittedValues(component);

        writer.startElement("div", component);
        writer.writeAttribute("class", "ui-helper-hidden", null);

        int idx = -1;
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            if (selectItem instanceof SelectItemGroup) {
                SelectItemGroup selectItemGroup = (SelectItemGroup) selectItem;
                String selectItemGroupLabel = selectItemGroup.getLabel() == null ? Constants.EMPTY_STRING : selectItemGroup.getLabel();
                for (SelectItem childSelectItem : selectItemGroup.getSelectItems()) {
                    idx++;
                    encodeOption(context, component, values, submittedValues, converter, childSelectItem, idx, selectItemGroupLabel);
                }
            }
            else {
                idx++;
                encodeOption(context, component, values, submittedValues, converter, selectItem, idx);
            }
        }

        writer.endElement("div");
    }

    protected void encodeOption(FacesContext context, SelectCheckboxMenu component, Object values, Object submittedValues,
                                Converter converter, SelectItem option, int idx) throws IOException {
        encodeOption(context, component, values, submittedValues, converter, option, idx, null);
    }

    protected void encodeOption(FacesContext context, SelectCheckboxMenu component, Object values, Object submittedValues,
                                Converter converter, SelectItem option, int idx, String selectItemGroupLabel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, component, converter, option.getValue());
        String name = component.getClientId(context);
        String id = name + UINamingContainer.getSeparatorChar(context) + idx;
        boolean disabled = option.isDisabled() || component.isDisabled();
        boolean escaped = option.isEscape();
        String itemLabel = option.getLabel();
        itemLabel = isValueBlank(itemLabel) ? "&nbsp;" : itemLabel;

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

        boolean checked = isSelected(context, component, itemValue, valuesArray, converter);
        //"SelectItem" with 'noSelectionOption="true" doesn't make sense for "SelectCheckboxMenu"...just in case
        if (option.isNoSelectionOption() && values != null && !checked) {
            return;
        }

        //input
        writer.startElement("input", getSelectItemComponent(option));
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("value", itemValueAsString, null);
        writer.writeAttribute("data-escaped", String.valueOf(escaped), null);
        if (selectItemGroupLabel != null) {
            writer.writeAttribute("data-group-label", selectItemGroupLabel, null);
        }
        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }
        if (option.getDescription() != null) {
            writer.writeAttribute("title", option.getDescription(), null);
        }
        if (component.getOnchange() != null) {
            writer.writeAttribute("onchange", component.getOnchange(), null);
        }
        renderAccessibilityAttributes(context, component, option.isDisabled(), false);

        writer.endElement("input");

        //label
        writer.startElement("label", null);
        writer.writeAttribute("for", id, null);
        if (disabled) {
            writer.writeAttribute("class", "ui-state-disabled", null);
        }

        if ("&nbsp;".equals(itemLabel)) {
            writer.write(itemLabel);
        }
        else {
            if (escaped) {
                writer.writeText(itemLabel, "value");
            }
            else {
                writer.write(itemLabel);
            }
        }

        writer.endElement("label");
    }

    protected void encodeLabel(FacesContext context, SelectCheckboxMenu component, boolean valid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = component.getLabel();
        String labelClass = !valid ? SelectCheckboxMenu.LABEL_CLASS + " ui-state-error" : SelectCheckboxMenu.LABEL_CLASS;
        if (label == null) {
            label = Constants.EMPTY_STRING;
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", SelectCheckboxMenu.LABEL_CONTAINER_CLASS, null);
        writer.startElement("label", null);
        writer.writeAttribute("class", labelClass, null);
        writer.writeText(label, null);
        writer.endElement("label");
        writer.endElement("span");
    }

    protected void encodeMultipleLabel(FacesContext context, SelectCheckboxMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = menu.getConverter();
        Object values = getValues(menu);
        Object submittedValues = getSubmittedValues(menu);
        Object valuesArray = (submittedValues != null) ? submittedValues : values;
        String listClass = createStyleClass(menu, null, SelectCheckboxMenu.MULTIPLE_CONTAINER_CLASS);
        String label = menu.getEmptyLabel() == null ? menu.getLabel() : menu.getEmptyLabel();

        writer.startElement("ul", null);
        writer.writeAttribute("data-label", label, null);
        writer.writeAttribute("class", listClass, null);
        if (valuesArray != null) {
            int length = Array.getLength(valuesArray);
            for (int i = 0; i < length; i++) {
                Object value = Array.get(valuesArray, i);

                SelectItem selectedItem = null;
                for (int j = 0; j < selectItems.size(); j++) {
                    SelectItem item = selectItems.get(j);
                    if (item instanceof SelectItemGroup) {
                        SelectItemGroup group = (SelectItemGroup) item;
                        for (SelectItem groupItem : group.getSelectItems()) {
                            if (isSelectValueEqual(context, menu, value, groupItem.getValue(), converter)) {
                                selectedItem = groupItem;
                                break;
                            }
                        }
                    }
                    else if (isSelectValueEqual(context, menu, value, item.getValue(), converter)) {
                        selectedItem = item;
                        break;
                    }
                }

                // #5956 Do not render a chip for the value if no matching option exists
                if (selectedItem != null) {
                    String itemValueAsString;
                    if (value == null || value instanceof String) {
                        itemValueAsString = (String) value;
                    }
                    else {
                        itemValueAsString = getOptionAsString(context, menu, converter, value);
                    }

                    writer.startElement("li", null);
                    writer.writeAttribute("class", SelectCheckboxMenu.TOKEN_DISPLAY_CLASS, null);
                    writer.writeAttribute("data-item-value", itemValueAsString, null);

                    writer.startElement("span", null);
                    writer.writeAttribute("class", SelectCheckboxMenu.TOKEN_LABEL_CLASS, null);

                    if (selectedItem.getLabel() != null) {
                        String selectedItemLabel = isValueBlank(selectedItem.getLabel()) ? "&nbsp;" : selectedItem.getLabel();
                        if (selectedItem.isEscape() && !"&nbsp;".equals(selectedItemLabel)) {
                            writer.writeText(selectedItemLabel, null);
                        }
                        else {
                            writer.write(selectedItemLabel);
                        }
                    }
                    else {
                        writer.writeText(value, null);
                    }

                    writer.endElement("span");

                    writer.startElement("span", null);
                    writer.writeAttribute("class", SelectCheckboxMenu.TOKEN_ICON_CLASS, null);
                    writer.endElement("span");

                    writer.endElement("li");
                }
            }
        }

        writer.endElement("ul");
    }

    protected void encodeMenuIcon(FacesContext context, SelectCheckboxMenu component, boolean valid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String iconClass = valid ? SelectCheckboxMenu.TRIGGER_CLASS : SelectCheckboxMenu.TRIGGER_CLASS + " ui-state-error";

        writer.startElement("div", component);
        writer.writeAttribute("class", iconClass, null);

        writer.startElement("span", component);
        writer.writeAttribute("class", "ui-icon ui-icon-triangle-1-s", null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodePanel(FacesContext context, SelectCheckboxMenu component, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String panelStyle = component.getPanelStyle();
        String panelStyleClass = SelectCheckboxMenu.PANEL_CLASS;
        if (component.getPanelStyleClass() != null) {
            panelStyleClass += " " + component.getPanelStyleClass();
        }

        String maxScrollHeight = getMaxScrollHeight(component);

        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context) + "_panel", null);
        writer.writeAttribute("class", panelStyleClass, null);
        writer.writeAttribute(HTML.ARIA_ROLE, "dialog", null);
        if (panelStyle != null) {
            writer.writeAttribute("style", panelStyle, null);
        }

        if (component.isShowHeader()) {
            encodePanelHeader(context, component, selectItems);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", SelectCheckboxMenu.ITEMS_WRAPPER_CLASS, null);
        writer.writeAttribute("style", "max-height:" + maxScrollHeight, null);

        if (!component.isDynamic()) {
            encodePanelContent(context, component, selectItems);
        }

        writer.endElement("div");

        encodePanelFooter(context, component);
        writer.endElement("div");
    }

    private void encodePanelHeader(FacesContext context, SelectCheckboxMenu component, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = component.getConverter();
        Object submittedValues = getSubmittedValues(component);
        Object values = getValues(component);
        Object valuesArray = (submittedValues != null) ? submittedValues : values;

        writer.startElement("div", null);
        writer.writeAttribute("class", SelectCheckboxMenu.HEADER_CLASS, null);

        if (component.isShowSelectAll()) {
            // determine if any of the items is not selected
            boolean notChecked = selectItems.stream().flatMap(selectItem -> {
                if (selectItem instanceof SelectItemGroup) {
                    // convert children to stream
                    final SelectItemGroup selectItemGroup = (SelectItemGroup) selectItem;
                    return Stream.of(selectItemGroup.getSelectItems());
                }
                else {
                    return Stream.of(selectItem);
                }
            }).anyMatch(selectItem -> {
                final Object value;
                if (submittedValues != null) {
                    // use submitted string representations of values
                    value = getOptionAsString(context, component, converter, selectItem.getValue());
                }
                else {
                    // use initial values
                    value = selectItem.getValue();
                }
                return !isSelected(context, component, value, valuesArray, converter);
            });

            // toggler
            encodeCheckbox(context, null, false, !notChecked, null, null, -1, -1);
        }

        //filter
        if (component.isFilter()) {
            encodeFilter(context, component);
        }

        //closer
        writer.startElement("a", null);
        writer.writeAttribute("class", SelectCheckboxMenu.CLOSER_CLASS, null);
        writer.writeAttribute("href", "#", null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-circle-close", null);
        writer.endElement("span");

        writer.endElement("a");

        writer.endElement("div");
    }

    protected void encodeFilter(FacesContext context, SelectCheckboxMenu component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = component.getClientId(context) + "_filter";

        writer.startElement("div", null);
        writer.writeAttribute("class", SelectCheckboxMenu.FILTER_CONTAINER_CLASS, null);

        writer.startElement("input", null);
        writer.writeAttribute("class", SelectCheckboxMenu.FILTER_CLASS, null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute(HTML.ARIA_AUTOCOMPLETE, "list", null);
        writer.writeAttribute(HTML.ARIA_CONTROLS, component.getClientId(context) + "_table", null);
        writer.writeAttribute("aria-disabled", false, null);
        writer.writeAttribute("aria-multiline", false, null);
        writer.writeAttribute("aria-readonly", false, null);

        if (component.getFilterPlaceholder() != null) {
            writer.writeAttribute("placeholder", component.getFilterPlaceholder(), null);
        }

        writer.endElement("input");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-search", id);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodePanelContent(FacesContext context, SelectCheckboxMenu component, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean customContent = component.getVar() != null;

        if (customContent) {
            ComponentUtils.runWithoutFacesContextVar(context, Constants.HELPER_RENDERER, () -> {
                List<Column> columns = component.getColumns();

                writer.startElement("table", null);
                writer.writeAttribute("id", component.getClientId(context) + "_list", null);
                writer.writeAttribute("class", SelectCheckboxMenu.TABLE_CLASS, null);
                writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_LISTBOX, null);
                writer.writeAttribute(HTML.ARIA_MULITSELECTABLE, "true", null);
                encodeColumnsHeader(context, component, columns);
                writer.startElement("tbody", null);
                writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_GROUP, null);
                encodeOptionsAsTable(context, component, selectItems, columns);
                writer.endElement("tbody");
                writer.endElement("table");
            });
        }
        else {
            // Rendering was moved to the client - see renderItems as part of forms.selectcheckboxmenu.js
        }
    }

    protected void encodePanelFooter(FacesContext context, SelectCheckboxMenu component) throws IOException {
        UIComponent facet = component.getFacet("footer");
        if (!FacetUtils.shouldRenderFacet(facet)) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", null);
        writer.writeAttribute("class", SelectCheckboxMenu.FOOTER_CLASS, null);
        facet.encodeAll(context);
        writer.endElement("div");
    }

    protected void encodeColumnsHeader(FacesContext context, SelectCheckboxMenu component, List<Column> columns) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean hasHeader = false;

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if (column.isRendered() && (column.getHeaderText() != null || column.getFacet("header") != null)) {
                hasHeader = true;
                break;
            }
        }

        if (hasHeader) {
            writer.startElement("thead", null);

            //empty column for input
            writer.startElement("th", null);
            writer.endElement("th");

            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                if (!column.isRendered()) {
                    continue;
                }

                String headerText = column.getHeaderText();
                String styleClass = column.getStyleClass() == null ? "ui-state-default" : "ui-state-default " + column.getStyleClass();

                writer.startElement("th", null);
                writer.writeAttribute("class", styleClass, null);

                if (column.getStyle() != null) {
                    writer.writeAttribute("style", column.getStyle(), null);
                }

                UIComponent headerFacet = column.getFacet("header");
                if (FacetUtils.shouldRenderFacet(headerFacet)) {
                    headerFacet.encodeAll(context);
                }
                else if (headerText != null) {
                    writer.writeText(headerText, null);
                }

                writer.endElement("th");
            }
            writer.endElement("thead");
        }
    }

    protected void encodeOptionsAsTable(FacesContext context, SelectCheckboxMenu component, List<SelectItem> selectItems, List<Column> columns)
            throws IOException {

        int idx = -1;
        int totalItems = selectItems.size();
        for (int i = 0; i < totalItems; i++) {
            SelectItem selectItem = selectItems.get(i);
            if (selectItem instanceof SelectItemGroup) {
                SelectItemGroup selectItemGroup = (SelectItemGroup) selectItem;
                encodeTableOption(context, component, selectItemGroup, columns, idx, totalItems);

                for (SelectItem groupSelectItem : selectItemGroup.getSelectItems()) {
                    idx++;
                    encodeTableOption(context, component, groupSelectItem, columns, idx, totalItems);
                }
            }
            else {
                encodeTableOption(context, component, selectItem, columns, idx, totalItems);
            }
        }
    }

    protected void encodeTableOption(FacesContext context, SelectCheckboxMenu component, SelectItem selectItem, List<Column> columns, int index, int totalItems)
        throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean checked = false;
        String rowStyleClass;
        String itemLabel = getOptionLabel(selectItem);
        String itemValueAsString = null;

        if (selectItem instanceof SelectItemGroup) {
            rowStyleClass = SelectCheckboxMenu.ROW_ITEM_GROUP_CLASS;
        }
        else {
            Converter converter = component.getConverter();

            //checked flag
            Object submittedValues = getSubmittedValues(component);
            Object values = getValues(component);
            Object valuesArray;
            Object value;
            if (submittedValues != null) {
                //use submitted string representations of values
                valuesArray = submittedValues;
                value = getOptionAsString(context, component, converter, selectItem.getValue());
            }
            else {
                //use initial values
                valuesArray = values;
                value = selectItem.getValue();
            }
            checked = isSelected(context, component, value, valuesArray, converter);
            //"SelectItem" with 'noSelectionOption="true" doesn't make sense for "SelectCheckboxMenu"...just in case
            if (selectItem.isNoSelectionOption() && !checked) {
                return;
            }

            //attribute values
            rowStyleClass = SelectCheckboxMenu.ROW_ITEM_CLASS;
            rowStyleClass += checked ? " ui-selectcheckboxmenu-checked" : " ui-selectcheckboxmenu-unchecked";
            if (selectItem.isNoSelectionOption()) {
                rowStyleClass += " ui-noselection-option";
            }
            if (selectItem.isDisabled()) {
                rowStyleClass += " ui-state-disabled";
            }
            itemValueAsString = getOptionAsString(context, component, converter, selectItem.getValue());

            //init variable for column rendering
            String var = component.getVar();
            Object varValue = selectItem.getValue();
            context.getExternalContext().getRequestMap().put(var, varValue);
        }

        //item as row
        writer.startElement("tr", getSelectItemComponent(selectItem));
        writer.writeAttribute("class", rowStyleClass, null);
        writer.writeAttribute("data-label", itemLabel, null);
        if ((itemValueAsString != null) && component.isMultiple()) {
            writer.writeAttribute("data-item-value", itemValueAsString, null);
        }
        if (selectItem.getDescription() != null) {
            writer.writeAttribute("title", selectItem.getDescription(), null);
        }

        if (selectItem instanceof SelectItemGroup) {
            //one additional column for input
            int colspan = 1 + columns.size();

            //group label
            writer.startElement("td", null);
            writer.writeAttribute("colspan", colspan, null);
            writer.writeText(itemLabel, null);
            writer.endElement("td");
        }
        else {
            String uuid = UUID.randomUUID().toString();
            boolean disabled = selectItem.isDisabled() || component.isDisabled();

            //input
            writer.startElement("td", null);
            encodeCheckbox(context, uuid, disabled, checked, selectItem.getDescription(), selectItem.getLabel(), index, totalItems);
            writer.endElement("td");

            //columns
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                if (!column.isRendered()) {
                    continue;
                }
                String style = column.getStyle();
                String styleClass = column.getStyleClass();

                writer.startElement("td", null);
                if (style != null) {
                    writer.writeAttribute("style", style, null);
                }
                if (styleClass != null) {
                    writer.writeAttribute("class", styleClass, null);
                }

                encodeIndexedId(context, column, index);
                writer.endElement("td");
            }
        }

        writer.endElement("tr");
    }

    protected void encodeScript(FacesContext context, SelectCheckboxMenu component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectCheckboxMenu", component)
                .callback("onShow", "function()", component.getOnShow())
                .callback("onHide", "function()", component.getOnHide())
                .callback("onChange", "function()", component.getOnchange())
                .attr("scrollHeight", getMaxScrollHeight(component), null)
                .attr("showHeader", component.isShowHeader(), true)
                .attr("updateLabel", component.isUpdateLabel(), false)
                .attr("labelSeparator", component.getLabelSeparator(), ", ")
                .attr("emptyLabel", component.getEmptyLabel())
                .attr("selectedLabel", component.getSelectedLabel(), null)
                .attr("multiple", component.isMultiple(), false)
                .attr("dynamic", component.isDynamic(), false)
                .attr("renderPanelContentOnClient", component.getVar() == null,  false)
                .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, component, component.getAppendTo()));

        if (component.isFilter()) {
            wb.attr("filter", true)
                    .attr("filterMatchMode", component.getFilterMatchMode(), null)
                    .nativeAttr("filterFunction", component.getFilterFunction(), null)
                    .attr("caseSensitive", component.isCaseSensitive(), false)
                    .attr("filterPlaceholder", component.getFilterPlaceholder(), null)
                    .attr("filterNormalize", component.isFilterNormalize(), false);
        }

        wb.attr("panelStyle", component.getPanelStyle(), null).attr("panelStyleClass", component.getPanelStyleClass(), null);

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected String getMaxScrollHeight(SelectCheckboxMenu component) {
        try {
            return Integer.parseInt(component.getScrollHeight()) + "px";
        }
        catch (NumberFormatException e) {
            return component.getScrollHeight();
        }
    }

    protected String getOptionLabel(SelectItem option) {
        String itemLabel = option.getLabel();
        return isValueBlank(itemLabel) ? "&nbsp;" : itemLabel;
    }

    @Override
    protected String getSubmitParam(FacesContext context, SelectCheckboxMenu component) {
        return component.getClientId(context);
    }

    protected void encodeKeyboardTarget(FacesContext context, SelectCheckboxMenu component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = component.getClientId(context) + "_focus";
        String tabindex = component.isDisabled() ? "-1" : Objects.toString(component.getTabindex(), "0");

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        writer.startElement("input", component);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("tabindex", tabindex, null);
        renderARIACombobox(context, component);
        renderAccessibilityAttributes(context, component);
        writer.endElement("input");
        writer.endElement("div");
    }

    protected void encodeCheckbox(FacesContext context, String id, boolean disabled, boolean checked, String title, String ariaLabel, int index, int totalItems)
        throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String boxClass = HTML.CHECKBOX_BOX_CLASS;
        if (disabled) {
            boxClass += " ui-state-disabled";
        }
        if (checked) {
            boxClass += " ui-state-active";
        }
        String iconClass = checked ? HTML.CHECKBOX_CHECKED_ICON_CLASS : HTML.CHECKBOX_UNCHECKED_ICON_CLASS;
        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.CHECKBOX_CLASS, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.CHECKBOX_INPUT_WRAPPER_CLASS, null);

        //input
        writer.startElement("input", null);
        writer.writeAttribute("type", "checkbox", null);
        if (id != null) {
            writer.writeAttribute("id", id, null);
        }
        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }
        if (title != null) {
            writer.writeAttribute("title", title, null);
        }
        if (ariaLabel != null) {
            writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);
        }
        if (totalItems > -1) {
            writer.writeAttribute("class", SelectCheckboxMenu.CHECKBOX_INPUT_CLASS, null);
            writer.writeAttribute(HTML.ARIA_SELECTED, String.valueOf(checked), null);
            writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_OPTION, null);
            writer.writeAttribute(HTML.ARIA_SET_SIZE, totalItems, null);
            writer.writeAttribute(HTML.ARIA_SET_POSITION, index + 1, null);
        }
        writer.endElement("input");

        writer.endElement("div");

        //box with icon
        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");

        writer.endElement("div");
    }
}
