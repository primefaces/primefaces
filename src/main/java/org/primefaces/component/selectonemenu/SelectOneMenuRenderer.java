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
package org.primefaces.component.selectonemenu;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.render.Renderer;

import org.primefaces.component.column.Column;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class SelectOneMenuRenderer extends SelectOneRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SelectOneMenu menu = (SelectOneMenu) component;
        if (!shouldDecode(menu)) {
            return;
        }

        if (menu.isEditable()) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();

            // default to user entered input
            String editorInput = params.get(menu.getClientId(context) + "_editableInput");
            menu.setSubmittedValue(editorInput);

            // #2862 check if it matches a label and if so use the value
            List<SelectItem> selectItems = getSelectItems(context, menu);
            for (int i = 0; i < selectItems.size(); i++) {
                SelectItem item = selectItems.get(i);
                if (item.getLabel().equalsIgnoreCase(editorInput)) {
                    menu.setSubmittedValue(getOptionAsString(context, menu, menu.getConverter(), item.getValue()));
                    break;
                }
            }

            decodeBehaviors(context, menu);
        }
        else {
            super.decode(context, component);
        }
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "javax.faces.SelectOne",
                "javax.faces.Menu");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectOneMenu menu = (SelectOneMenu) component;

        if (menu.isDynamicLoadRequest(context)) {
            List<SelectItem> selectItems = getSelectItems(context, menu);
            String clientId = menu.getClientId(context);
            Converter converter = menu.getConverter();
            Object values = getValues(menu);
            Object submittedValues = getSubmittedValues(menu);

            encodeHiddenSelect(context, menu, clientId, selectItems, values, submittedValues, converter);
            encodePanelContent(context, menu, selectItems);
        }
        else {
            encodeMarkup(context, menu);
            encodeScript(context, menu);
        }
    }

    protected void encodeMarkup(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, menu);
        String clientId = menu.getClientId(context);
        Converter converter = menu.getConverter();
        Object values = getValues(menu);
        Object submittedValues = getSubmittedValues(menu);
        boolean valid = menu.isValid();
        String title = menu.getTitle();

        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? SelectOneMenu.STYLE_CLASS : SelectOneMenu.STYLE_CLASS + " " + styleClass;
        styleClass = !valid ? styleClass + " ui-state-error" : styleClass;
        styleClass = menu.isDisabled() ? styleClass + " ui-state-disabled" : styleClass;

        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleclass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        if (title != null) {
            writer.writeAttribute("title", title, "title");
        }
        renderARIACombobox(context, menu);

        encodeInput(context, menu, clientId, selectItems, values, submittedValues, converter);
        encodeLabel(context, menu, selectItems);
        encodeMenuIcon(context, menu, valid);
        encodePanel(context, menu, selectItems);

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, SelectOneMenu menu, String clientId, List<SelectItem> selectItems, Object values,
                               Object submittedValues, Converter converter) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String focusId = clientId + "_focus";

        //input for accessibility
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", focusId, null);
        writer.writeAttribute("name", focusId, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);

        //for keyboard accessibility and ScreenReader
        renderAccessibilityAttributes(context, menu);
        renderPassThruAttributes(context, menu, HTML.TAB_INDEX);
        renderDomEvents(context, menu, HTML.BLUR_FOCUS_EVENTS);

        writer.endElement("input");

        writer.endElement("div");

        //hidden select
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        encodeHiddenSelect(context, menu, clientId, selectItems, values, submittedValues, converter);

        writer.endElement("div");

    }

    protected void encodeHiddenSelect(FacesContext context, SelectOneMenu menu, String clientId, List<SelectItem> selectItems,
                                      Object values, Object submittedValues, Converter converter) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";

        writer.startElement("select", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("tabindex", "-1", null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
        if (menu.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        if (menu.getOnkeydown() != null) {
            writer.writeAttribute("onkeydown", menu.getOnkeydown(), null);
        }
        if (menu.getOnkeyup() != null) {
            writer.writeAttribute("onkeyup", menu.getOnkeyup(), null);
        }

        renderOnchange(context, menu);

        renderValidationMetadata(context, menu);

        encodeSelectItems(context, menu, selectItems, values, submittedValues, converter);

        writer.endElement("select");
    }

    protected void encodeLabel(FacesContext context, SelectOneMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (menu.isEditable()) {
            writer.startElement("input", null);
            writer.writeAttribute("type", "text", null);
            writer.writeAttribute("name", menu.getClientId(context) + "_editableInput", null);
            writer.writeAttribute("class", SelectOneMenu.LABEL_CLASS, null);

            if (menu.getTabindex() != null) {
                writer.writeAttribute("tabindex", menu.getTabindex(), null);
            }

            if (menu.isDisabled()) {
                writer.writeAttribute("disabled", "disabled", null);
            }

            String valueToRender = ComponentUtils.getValueToRender(context, menu);
            for (int i = 0; i < selectItems.size(); i++) {
                SelectItem selectItem = selectItems.get(i);
                if (isSelected(context, menu, valueToRender, selectItem.getValue(), null)) {
                    valueToRender = selectItem.getLabel();
                    break;
                }
            }
            writer.writeAttribute("value", valueToRender, null);

            if (menu.getMaxlength() != Integer.MAX_VALUE) {
                writer.writeAttribute("maxlength", menu.getMaxlength(), null);
            }

            if (menu.getPlaceholder() != null) {
                writer.writeAttribute("placeholder", menu.getPlaceholder(), null);
            }

            if (menu.getOnkeydown() != null) {
                writer.writeAttribute("onkeydown", menu.getOnkeydown(), null);
                renderDomEvent(context, menu, "onkeydown", "keydown", "keydown", null);
            }
            if (menu.getOnkeyup() != null) {
                writer.writeAttribute("onkeyup", menu.getOnkeyup(), null);
                renderDomEvent(context, menu, "onkeyup", "keyup", "keyup", null);
            }

            writer.endElement("input");
        }
        else {
            writer.startElement("label", null);
            writer.writeAttribute("id", menu.getClientId(context) + "_label", null);
            writer.writeAttribute("class", SelectOneMenu.LABEL_CLASS, null);
            if (menu.getPlaceholder() != null) {
                writer.writeAttribute("data-placeholder", menu.getPlaceholder(), null);
            }
            String label = menu.getLabel();
            if (label != null) {
                writer.writeText(label, null);
            }
            writer.write("&nbsp;");
            writer.endElement("label");
        }
    }

    protected void encodeMenuIcon(FacesContext context, SelectOneMenu menu, boolean valid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String iconClass = valid ? SelectOneMenu.TRIGGER_CLASS : SelectOneMenu.TRIGGER_CLASS + " ui-state-error";

        writer.startElement("div", null);
        writer.writeAttribute("class", iconClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-triangle-1-s ui-c", null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodePanel(FacesContext context, SelectOneMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String panelStyle = menu.getPanelStyle();
        String panelStyleClass = menu.getPanelStyleClass();
        panelStyleClass = panelStyleClass == null ? SelectOneMenu.PANEL_CLASS : SelectOneMenu.PANEL_CLASS + " " + panelStyleClass;

        String height = null;
        try {
            height = Integer.parseInt(menu.getHeight()) + "px";
        }
        catch (NumberFormatException e) {
            height = menu.getHeight();
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", menu.getClientId(context) + "_panel", null);
        writer.writeAttribute("class", panelStyleClass, null);
        if (panelStyle != null) {
            writer.writeAttribute("style", panelStyle, null);
        }

        if (menu.isFilter()) {
            encodeFilter(context, menu);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", SelectOneMenu.ITEMS_WRAPPER_CLASS, null);
        writer.writeAttribute("style", "max-height:" + height, null);

        if (!menu.isDynamic()) {
            encodePanelContent(context, menu, selectItems);
        }

        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodePanelContent(FacesContext context, SelectOneMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean customContent = menu.getVar() != null;

        if (customContent) {
            List<Column> columns = menu.getColumns();

            writer.startElement("table", null);
            writer.writeAttribute("class", SelectOneMenu.TABLE_CLASS, null);
            encodeColumnsHeader(context, menu, columns);
            writer.startElement("tbody", null);
            encodeOptionsAsTable(context, menu, selectItems, columns);
            writer.endElement("tbody");
            writer.endElement("table");
        }
        else {
            writer.startElement("ul", null);
            writer.writeAttribute("id", menu.getClientId(context) + "_items", null);
            writer.writeAttribute("class", SelectOneMenu.LIST_CLASS, null);
            writer.writeAttribute("role", "listbox", null);
            encodeOptionsAsList(context, menu, selectItems);
            writer.endElement("ul");
        }
    }

    protected void encodeColumnsHeader(FacesContext context, SelectOneMenu menu, List<Column> columns)
            throws IOException {

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
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                if (!column.isRendered()) {
                    continue;
                }

                String headerText = column.getHeaderText();
                UIComponent headerFacet = column.getFacet("header");
                String styleClass = column.getStyleClass() == null ? "ui-state-default" : "ui-state-default " + column.getStyleClass();

                writer.startElement("th", null);
                writer.writeAttribute("class", styleClass, null);

                if (column.getStyle() != null) {
                    writer.writeAttribute("style", column.getStyle(), null);
                }

                if (headerFacet != null) {
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

    protected void encodeOptionsAsTable(FacesContext context, SelectOneMenu menu, List<SelectItem> selectItems, List<Column> columns)
            throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String var = menu.getVar();
        ValueExpression value = menu.getValueExpression("value");
        Class<?> valueType = value == null ? null : value.getType(context.getELContext());

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            Object itemValue = selectItem.getValue();
            String itemLabel = selectItem.getLabel();
            itemLabel = isValueBlank(itemLabel) ? "&nbsp;" : itemLabel;

            String itemStyleClass = SelectOneMenu.ROW_CLASS;
            if (selectItem.isNoSelectionOption()) {
                itemStyleClass = itemStyleClass + " ui-noselection-option";
            }

            context.getExternalContext().getRequestMap().put(var, itemValue);

            writer.startElement("tr", null);
            writer.writeAttribute("class", itemStyleClass, null);
            writer.writeAttribute("data-label", itemLabel, null);
            if (selectItem.getDescription() != null) {
                writer.writeAttribute("title", selectItem.getDescription(), null);
            }

            if (itemValue == null || (valueType != null && !valueType.isAssignableFrom(itemValue.getClass()))) {
                writer.startElement("td", null);
                writer.writeAttribute("colspan", columns.size(), null);
                writer.writeText(selectItem.getLabel(), null);
                writer.endElement("td");
            }
            else {
                for (int j = 0; j < columns.size(); j++) {
                    Column column = columns.get(j);
                    if (column.isRendered()) {
                        String style = column.getStyle();
                        String styleClass = column.getStyleClass();

                        writer.startElement("td", null);
                        if (style != null) {
                            writer.writeAttribute("style", style, null);
                        }
                        if (styleClass != null) {
                            writer.writeAttribute("class", styleClass, null);
                        }

                        renderChildren(context, column);
                        writer.endElement("td");
                    }
                }
            }

            writer.endElement("tr");
        }

        context.getExternalContext().getRequestMap().put(var, null);
    }

    protected void encodeOptionsAsList(FacesContext context, SelectOneMenu menu, List<SelectItem> selectItems) throws IOException {
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);

            if (selectItem instanceof SelectItemGroup) {
                SelectItemGroup group = (SelectItemGroup) selectItem;

                encodeItem(context, menu, group, SelectOneMenu.ITEM_GROUP_CLASS);
                encodeOptionsAsList(context, menu, Arrays.asList(group.getSelectItems()));
            }
            else {
                encodeItem(context, menu, selectItem, SelectOneMenu.ITEM_CLASS);
            }
        }
    }

    protected void encodeItem(FacesContext context, SelectOneMenu menu, SelectItem selectItem, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemLabel = selectItem.getLabel();
        itemLabel = isValueBlank(itemLabel) ? "&nbsp;" : itemLabel;
        String itemStyleClass = styleClass;
        if (selectItem.isNoSelectionOption()) {
            itemStyleClass = itemStyleClass + " ui-noselection-option";
        }

        writer.startElement("li", null);
        writer.writeAttribute("class", itemStyleClass, null);
        writer.writeAttribute("data-label", itemLabel, null);
        writer.writeAttribute("tabindex", "-1", null);
        writer.writeAttribute("role", "option", null);
        if (selectItem.getDescription() != null) {
            writer.writeAttribute("title", selectItem.getDescription(), null);
        }

        if (itemLabel.equals("&nbsp;")) {
            writer.write(itemLabel);
        }
        else {
            if (selectItem.isEscape()) {
                writer.writeText(itemLabel, "value");
            }
            else {
                writer.write(itemLabel);
            }
        }

        writer.endElement("li");
    }

    protected void encodeScript(FacesContext context, SelectOneMenu menu) throws IOException {
        String clientId = menu.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectOneMenu", menu.resolveWidgetVar(), clientId)
                .attr("effect", menu.getEffect(), null)
                .attr("effectSpeed", menu.getEffectSpeed(), null)
                .attr("editable", menu.isEditable(), false)
                .attr("appendTo", SearchExpressionFacade.resolveClientId(context, menu, menu.getAppendTo()), null)
                .attr("syncTooltip", menu.isSyncTooltip(), false)
                .attr("label", menu.getLabel(), null)
                .attr("labelTemplate", menu.getLabelTemplate(), null)
                .attr("autoWidth", menu.isAutoWidth(), true)
                .attr("dynamic", menu.isDynamic(), false);

        if (menu.isFilter()) {
            wb.attr("filter", true)
                    .attr("filterMatchMode", menu.getFilterMatchMode(), null)
                    .nativeAttr("filterFunction", menu.getFilterFunction(), null)
                    .attr("caseSensitive", menu.isCaseSensitive(), false);
        }

        encodeClientBehaviors(context, menu);

        wb.finish();
    }

    protected void encodeSelectItems(FacesContext context, SelectOneMenu menu, List<SelectItem> selectItems, Object values,
                                     Object submittedValues, Converter converter) throws IOException {

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            encodeOption(context, menu, selectItem, values, submittedValues, converter, i);
        }
    }

    protected void encodeOption(FacesContext context, SelectOneMenu menu, SelectItem option, Object values, Object submittedValues,
                                Converter converter, int itemIndex) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        if (option instanceof SelectItemGroup) {
            SelectItemGroup group = (SelectItemGroup) option;
            for (SelectItem groupItem : group.getSelectItems()) {
                encodeOption(context, menu, groupItem, values, submittedValues, converter, itemIndex);
            }
        }
        else {
            String itemValueAsString = getOptionAsString(context, menu, converter, option.getValue());
            boolean disabled = option.isDisabled();
            boolean isEscape = option.isEscape();

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

            boolean selected = isSelected(context, menu, itemValue, valuesArray, converter);

            if (!menu.isDynamic() || (menu.isDynamic() && (selected || menu.isDynamicLoadRequest(context) || itemIndex == 0))) {
                writer.startElement("option", null);
                writer.writeAttribute("value", itemValueAsString, null);
                if (disabled) {
                    writer.writeAttribute("disabled", "disabled", null);
                }
                if (selected) {
                    writer.writeAttribute("selected", "selected", null);
                }
                writer.writeAttribute("data-escape", String.valueOf(isEscape), null);

                if (!isValueBlank(option.getLabel())) {
                    if (isEscape) {
                        writer.writeText(option.getLabel(), "value");
                    }
                    else {
                        writer.write(option.getLabel());
                    }
                }

                writer.endElement("option");
            }
        }
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return selectOne.getClientId(context) + "_input";
    }

    protected void encodeFilter(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = menu.getClientId(context) + "_filter";

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-selectonemenu-filter-container", null);

        writer.startElement("input", null);
        writer.writeAttribute("class", "ui-selectonemenu-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);

        if (menu.getFilterPlaceholder() != null) {
            writer.writeAttribute("placeholder", menu.getFilterPlaceholder(), null);
        }

        writer.endElement("input");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-search", id);
        writer.endElement("span");

        writer.endElement("div");
    }

    @Override
    public String getHighlighter() {
        return "onemenu";
    }
}
