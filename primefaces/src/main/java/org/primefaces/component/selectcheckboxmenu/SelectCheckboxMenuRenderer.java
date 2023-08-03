/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.render.Renderer;

import org.primefaces.component.column.Column;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.SelectManyRenderer;
import org.primefaces.util.*;

public class SelectCheckboxMenuRenderer extends SelectManyRenderer {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "javax.faces.SelectMany",
                "javax.faces.Checkbox");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectCheckboxMenu menu = (SelectCheckboxMenu) component;

        encodeMarkup(context, menu);
        encodeScript(context, menu);
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    protected void encodeMarkup(FacesContext context, SelectCheckboxMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, menu);
        boolean valid = menu.isValid();
        String title = menu.getTitle();

        String style = menu.getStyle();
        String styleclass = createStyleClass(menu, SelectCheckboxMenu.STYLE_CLASS);
        styleclass = menu.isMultiple() ? SelectCheckboxMenu.MULTIPLE_CLASS + " " + styleclass : styleclass;

        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleclass, "styleclass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        if (title != null) {
            writer.writeAttribute("title", title, "title");
        }

        renderARIACombobox(context, menu);
        encodeKeyboardTarget(context, menu);
        encodeInputs(context, menu, selectItems);
        if (menu.isMultiple()) {
            encodeMultipleLabel(context, menu, selectItems);
        }
        else {
            encodeLabel(context, menu, valid);
        }

        encodeMenuIcon(context, menu, valid);
        encodePanel(context, menu, selectItems);

        writer.endElement("div");
    }

    protected void encodeInputs(FacesContext context, SelectCheckboxMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = menu.getConverter();
        Object values = getValues(menu);
        Object submittedValues = getSubmittedValues(menu);

        writer.startElement("div", menu);
        writer.writeAttribute("class", "ui-helper-hidden", null);

        int idx = -1;
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            if (selectItem instanceof SelectItemGroup) {
                SelectItemGroup selectItemGroup = (SelectItemGroup) selectItem;
                String selectItemGroupLabel = selectItemGroup.getLabel() == null ? "" : selectItemGroup.getLabel();
                for (SelectItem childSelectItem : selectItemGroup.getSelectItems()) {
                    idx++;
                    encodeOption(context, menu, values, submittedValues, converter, childSelectItem, idx, selectItemGroupLabel);
                }
            }
            else {
                idx++;
                encodeOption(context, menu, values, submittedValues, converter, selectItem, idx);
            }
        }

        writer.endElement("div");
    }

    protected void encodeOption(FacesContext context, SelectCheckboxMenu menu, Object values, Object submittedValues,
                                Converter converter, SelectItem option, int idx) throws IOException {
        encodeOption(context, menu, values, submittedValues, converter, option, idx, null);
    }

    protected void encodeOption(FacesContext context, SelectCheckboxMenu menu, Object values, Object submittedValues,
                                Converter converter, SelectItem option, int idx, String selectItemGroupLabel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, menu, converter, option.getValue());
        String name = menu.getClientId(context);
        String id = name + UINamingContainer.getSeparatorChar(context) + idx;
        boolean disabled = option.isDisabled() || menu.isDisabled();
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

        boolean checked = isSelected(context, menu, itemValue, valuesArray, converter);
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

        writer.writeAttribute(HTML.ARIA_CHECKED, checked, null);
        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }
        if (option.getDescription() != null) {
            writer.writeAttribute("title", option.getDescription(), null);
        }
        if (menu.getOnchange() != null) {
            writer.writeAttribute("onchange", menu.getOnchange(), null);
        }
        renderAccessibilityAttributes(context, menu, option.isDisabled(), false);

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

    protected void encodeLabel(FacesContext context, SelectCheckboxMenu menu, boolean valid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = menu.getLabel();
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
                        if (selectedItem.isEscape()) {
                            writer.writeText(selectedItem.getLabel(), null);
                        }
                        else {
                            writer.write(selectedItem.getLabel());
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

    protected void encodeMenuIcon(FacesContext context, SelectCheckboxMenu menu, boolean valid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String iconClass = valid ? SelectCheckboxMenu.TRIGGER_CLASS : SelectCheckboxMenu.TRIGGER_CLASS + " ui-state-error";

        writer.startElement("div", menu);
        writer.writeAttribute("class", iconClass, null);

        writer.startElement("span", menu);
        writer.writeAttribute("class", "ui-icon ui-icon-triangle-1-s", null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodePanel(FacesContext context, SelectCheckboxMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String panelStyle = menu.getPanelStyle();
        String panelStyleClass = SelectCheckboxMenu.PANEL_CLASS;
        if (menu.getPanelStyleClass() != null) {
            panelStyleClass += " " + menu.getPanelStyleClass();
        }

        String maxScrollHeight = getMaxScrollHeight(menu);

        writer.startElement("div", null);
        writer.writeAttribute("id", menu.getClientId(context) + "_panel", null);
        writer.writeAttribute("class", panelStyleClass, null);
        if (panelStyle != null) {
            writer.writeAttribute("style", panelStyle, null);
        }
        writer.writeAttribute(HTML.ARIA_ROLE, "dialog", null);

        if (menu.isShowHeader()) {
            encodePanelHeader(context, menu, selectItems);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", SelectCheckboxMenu.ITEMS_WRAPPER_CLASS, null);
        writer.writeAttribute("style", "max-height:" + maxScrollHeight, null);

        if (!menu.isDynamic()) {
            encodePanelContent(context, menu, selectItems);
        }

        writer.endElement("div");

        encodePanelFooter(context, menu);
        writer.endElement("div");
    }

    private void encodePanelHeader(FacesContext context, SelectCheckboxMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = menu.getConverter();
        Object submittedValues = getSubmittedValues(menu);
        Object values = getValues(menu);
        Object valuesArray = (submittedValues != null) ? submittedValues : values;

        writer.startElement("div", null);
        writer.writeAttribute("class", SelectCheckboxMenu.HEADER_CLASS, null);

        //determine if any of the items is not selected
        boolean notChecked = selectItems.stream()
                .flatMap(selectItem -> {
                    if (selectItem instanceof SelectItemGroup) {
                        //convert children to stream
                        final SelectItemGroup selectItemGroup = (SelectItemGroup) selectItem;
                        return Stream.of(selectItemGroup.getSelectItems());
                    }
                    else {
                        return Stream.of(selectItem);
                    }
                })
                .anyMatch(selectItem -> {
                    final Object value;
                    if (submittedValues != null) {
                        //use submitted string representations of values
                        value = getOptionAsString(context, menu, converter, selectItem.getValue());
                    }
                    else {
                        //use initial values
                        value = selectItem.getValue();
                    }
                    return !isSelected(context, menu, value, valuesArray, converter);
                });

        //toggler
        encodeCheckbox(context, null, false, !notChecked, null, null);

        //filter
        if (menu.isFilter()) {
            encodeFilter(context, menu);
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

    protected void encodeFilter(FacesContext context, SelectCheckboxMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = menu.getClientId(context) + "_filter";

        writer.startElement("div", null);
        writer.writeAttribute("class", SelectCheckboxMenu.FILTER_CONTAINER_CLASS, null);

        writer.startElement("input", null);
        writer.writeAttribute("class", SelectCheckboxMenu.FILTER_CLASS, null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute(HTML.ARIA_AUTOCOMPLETE, "list", null);
        writer.writeAttribute(HTML.ARIA_CONTROLS, menu.getClientId(context) + "_table", null);
        writer.writeAttribute("aria-disabled", false, null);
        writer.writeAttribute("aria-multiline", false, null);
        writer.writeAttribute("aria-readonly", false, null);

        if (menu.getFilterPlaceholder() != null) {
            writer.writeAttribute("placeholder", menu.getFilterPlaceholder(), null);
        }

        writer.endElement("input");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-search", id);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodePanelContent(FacesContext context, SelectCheckboxMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean customContent = menu.getVar() != null;

        if (customContent) {
            List<Column> columns = menu.getColumns();

            writer.startElement("table", null);
            writer.writeAttribute("id", menu.getClientId(context) + "_table", null);
            writer.writeAttribute("class", SelectCheckboxMenu.TABLE_CLASS, null);
            writer.writeAttribute("role", "listbox", null);
            encodeColumnsHeader(context, menu, columns);
            writer.startElement("tbody", null);
            encodeOptionsAsTable(context, menu, selectItems, columns);
            writer.endElement("tbody");
            writer.endElement("table");
        }
        else {
            // Rendering was moved to the client - see renderPanelContentFromHiddenSelect as part of forms.selectcheckboxmenu.js
        }
    }

    protected void encodePanelFooter(FacesContext context, SelectCheckboxMenu menu) throws IOException {
        UIComponent facet = menu.getFacet("footer");
        if (!ComponentUtils.shouldRenderFacet(facet)) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", null);
        writer.writeAttribute("class", SelectCheckboxMenu.FOOTER_CLASS, null);
        facet.encodeAll(context);
        writer.endElement("div");
    }

    protected void encodeColumnsHeader(FacesContext context, SelectCheckboxMenu menu, List<Column> columns) throws IOException {
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
                if (ComponentUtils.shouldRenderFacet(headerFacet)) {
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

    protected void encodeOptionsAsTable(FacesContext context, SelectCheckboxMenu menu, List<SelectItem> selectItems, List<Column> columns) throws IOException {
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            if (selectItem instanceof SelectItemGroup) {
                SelectItemGroup selectItemGroup = (SelectItemGroup) selectItem;
                encodeTableOption(context, menu, selectItemGroup, columns, i);

                for (SelectItem groupSelectItem : selectItemGroup.getSelectItems()) {
                    encodeTableOption(context, menu, groupSelectItem, columns, i);
                }
            }
            else {
                encodeTableOption(context, menu, selectItem, columns, i);
            }
        }
    }

    protected void encodeTableOption(FacesContext context, SelectCheckboxMenu menu, SelectItem selectItem, List<Column> columns, int index) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean checked = false;
        String rowStyleClass;
        String itemLabel = getOptionLabel(selectItem);
        String itemValueAsString = null;
        String role;

        if (selectItem instanceof SelectItemGroup) {
            rowStyleClass = SelectCheckboxMenu.ROW_ITEM_GROUP_CLASS;
            role = "optgroup";
        }
        else {
            Converter converter = menu.getConverter();

            //checked flag
            Object submittedValues = getSubmittedValues(menu);
            Object values = getValues(menu);
            Object valuesArray;
            Object value;
            if (submittedValues != null) {
                //use submitted string representations of values
                valuesArray = submittedValues;
                value = getOptionAsString(context, menu, converter, selectItem.getValue());
            }
            else {
                //use initial values
                valuesArray = values;
                value = selectItem.getValue();
            }
            checked = isSelected(context, menu, value, valuesArray, converter);
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
            itemValueAsString = getOptionAsString(context, menu, converter, selectItem.getValue());
            role = "option";

            //init variable for column rendering
            String var = menu.getVar();
            Object varValue = selectItem.getValue();
            context.getExternalContext().getRequestMap().put(var, varValue);
        }

        //item as row
        writer.startElement("tr", getSelectItemComponent(selectItem));
        writer.writeAttribute("class", rowStyleClass, null);
        writer.writeAttribute("data-label", itemLabel, null);
        if ((itemValueAsString != null) && menu.isMultiple()) {
            writer.writeAttribute("data-item-value", itemValueAsString, null);
        }
        writer.writeAttribute("role", role, null);
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
            boolean disabled = selectItem.isDisabled() || menu.isDisabled();

            //input
            writer.startElement("td", null);
            encodeCheckbox(context, uuid, disabled, checked, selectItem.getDescription(), selectItem.getLabel());
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

    protected void encodeScript(FacesContext context, SelectCheckboxMenu menu) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectCheckboxMenu", menu)
                .callback("onShow", "function()", menu.getOnShow())
                .callback("onHide", "function()", menu.getOnHide())
                .callback("onChange", "function()", menu.getOnchange())
                .attr("scrollHeight", getMaxScrollHeight(menu), null)
                .attr("showHeader", menu.isShowHeader(), true)
                .attr("updateLabel", menu.isUpdateLabel(), false)
                .attr("labelSeparator", menu.getLabelSeparator(), ", ")
                .attr("emptyLabel", menu.getEmptyLabel())
                .attr("multiple", menu.isMultiple(), false)
                .attr("dynamic", menu.isDynamic(), false)
                .attr("renderPanelContentOnClient", menu.getVar() == null,  false)
                .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, menu, menu.getAppendTo()));

        if (menu.isFilter()) {
            wb.attr("filter", true)
                    .attr("filterMatchMode", menu.getFilterMatchMode(), null)
                    .nativeAttr("filterFunction", menu.getFilterFunction(), null)
                    .attr("caseSensitive", menu.isCaseSensitive(), false)
                    .attr("filterPlaceholder", menu.getFilterPlaceholder(), null)
                    .attr("filterNormalize", menu.isFilterNormalize(), false);
        }

        wb.attr("panelStyle", menu.getPanelStyle(), null).attr("panelStyleClass", menu.getPanelStyleClass(), null);

        encodeClientBehaviors(context, menu);

        wb.finish();
    }

    protected String getMaxScrollHeight(SelectCheckboxMenu menu) {
        try {
            return Integer.parseInt(menu.getScrollHeight()) + "px";
        }
        catch (NumberFormatException e) {
            return menu.getScrollHeight();
        }
    }

    protected String getOptionLabel(SelectItem option) {
        String itemLabel = option.getLabel();
        return isValueBlank(itemLabel) ? "&nbsp;" : itemLabel;
    }

    @Override
    protected String getSubmitParam(FacesContext context, UISelectMany selectMany) {
        return selectMany.getClientId(context);
    }

    protected void encodeKeyboardTarget(FacesContext context, SelectCheckboxMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = menu.getClientId(context) + "_focus";
        String tabindex = menu.getTabindex();

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        writer.startElement("input", menu);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("readonly", "readonly", null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_COMBOBOX, null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
        if (tabindex != null) {
            writer.writeAttribute("tabindex", tabindex, null);
        }
        writer.endElement("input");
        writer.endElement("div");
    }

    protected void encodeCheckbox(FacesContext context, String id, boolean disabled, boolean checked, String title, String ariaLabel) throws IOException {
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
