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
package org.primefaces.component.selectcheckboxmenu;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;

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

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.renderkit.SelectManyRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

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
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectCheckboxMenu menu = (SelectCheckboxMenu) component;

        encodeMarkup(context, menu);
        encodeScript(context, menu);
    }

    protected void encodeMarkup(FacesContext context, SelectCheckboxMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, menu);
        boolean valid = menu.isValid();
        String title = menu.getTitle();

        String style = menu.getStyle();
        String styleclass = menu.getStyleClass();
        styleclass = styleclass == null ? SelectCheckboxMenu.STYLE_CLASS : SelectCheckboxMenu.STYLE_CLASS + " " + styleclass;
        styleclass = menu.isDisabled() ? styleclass + " ui-state-disabled" : styleclass;
        styleclass = !valid ? styleclass + " ui-state-error" : styleclass;
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
            encodeMultipleLabel(context, menu, selectItems, valid);
        }
        else {
            encodeLabel(context, menu, selectItems, valid);
        }

        encodeMenuIcon(context, menu, valid);

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
        if (option.isNoSelectionOption() && values != null && !checked) {
            return;
        }

        //input
        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("value", itemValueAsString, null);
        writer.writeAttribute("data-escaped", String.valueOf(escaped), null);
        if (selectItemGroupLabel != null) {
            writer.writeAttribute("group-label", selectItemGroupLabel, null);
        }

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

        if (itemLabel.equals("&nbsp;")) {
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

    protected void encodeLabel(FacesContext context, SelectCheckboxMenu menu, List<SelectItem> selectItems, boolean valid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = menu.getLabel();
        String labelClass = !valid ? SelectCheckboxMenu.LABEL_CLASS + " ui-state-error" : SelectCheckboxMenu.LABEL_CLASS;
        if (label == null) {
            label = "";
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", SelectCheckboxMenu.LABEL_CONTAINER_CLASS, null);
        writer.startElement("label", null);
        writer.writeAttribute("class", labelClass, null);
        writer.writeText(label, null);
        writer.endElement("label");
        writer.endElement("span");
    }

    protected void encodeMultipleLabel(FacesContext context, SelectCheckboxMenu menu, List<SelectItem> selectItems, boolean valid)
            throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = menu.getConverter();
        Object values = getValues(menu);
        Object submittedValues = getSubmittedValues(menu);
        Object valuesArray = (submittedValues != null) ? submittedValues : values;
        String listClass = menu.isDisabled() ?
                           SelectCheckboxMenu.MULTIPLE_CONTAINER_CLASS + " ui-state-disabled" : SelectCheckboxMenu.MULTIPLE_CONTAINER_CLASS;
        listClass = valid ? listClass : listClass + " ui-state-error";

        writer.startElement("ul", null);
        writer.writeAttribute("label", menu.getLabel(), null);
        writer.writeAttribute("class", listClass, null);
        if (valuesArray != null) {
            int length = Array.getLength(valuesArray);
            for (int i = 0; i < length; i++) {
                Object value = Array.get(valuesArray, i);
                String itemValueAsString = getOptionAsString(context, menu, converter, value);
                writer.startElement("li", null);
                writer.writeAttribute("class", SelectCheckboxMenu.TOKEN_DISPLAY_CLASS, null);
                writer.writeAttribute("data-item-value", itemValueAsString, null);

                writer.startElement("span", null);
                writer.writeAttribute("class", SelectCheckboxMenu.TOKEN_LABEL_CLASS, null);

                SelectItem selectedItem = null;
                for (SelectItem item : selectItems) {
                    if (item instanceof SelectItemGroup) {
                        SelectItemGroup group = (SelectItemGroup) item;
                        for (SelectItem groupItem : group.getSelectItems()) {
                            if (value.equals(groupItem.getValue())) {
                                selectedItem = groupItem;
                                break;
                            }
                        }
                    }
                    else if (Objects.equals(value, item.getValue())) {
                        selectedItem = item;
                        break;
                    }
                }

                if (selectedItem != null && selectedItem.getLabel() != null) {
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

    protected void encodeScript(FacesContext context, SelectCheckboxMenu menu) throws IOException {
        String clientId = menu.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectCheckboxMenu", menu.resolveWidgetVar(context), clientId)
                .callback("onShow", "function()", menu.getOnShow())
                .callback("onHide", "function()", menu.getOnHide())
                .callback("onChange", "function()", menu.getOnchange())
                .attr("scrollHeight", menu.getScrollHeight(), Integer.MAX_VALUE)
                .attr("showHeader", menu.isShowHeader(), true)
                .attr("updateLabel", menu.isUpdateLabel(), false)
                .attr("labelSeparator", menu.getLabelSeparator(), ", ")
                .attr("emptyLabel", menu.getEmptyLabel())
                .attr("multiple", menu.isMultiple(), false)
                .attr("dynamic", menu.isDynamic(), false)
                .attr("appendTo", SearchExpressionFacade.resolveClientId(context, menu, menu.getAppendTo(), SearchExpressionHint.RESOLVE_CLIENT_SIDE), null);

        if (menu.isFilter()) {
            wb.attr("filter", true)
                    .attr("filterMatchMode", menu.getFilterMatchMode(), null)
                    .nativeAttr("filterFunction", menu.getFilterFunction(), null)
                    .attr("caseSensitive", menu.isCaseSensitive(), false)
                    .attr("filterPlaceholder", menu.getFilterPlaceholder(), null);
        }

        wb.attr("panelStyle", menu.getPanelStyle(), null).attr("panelStyleClass", menu.getPanelStyleClass(), null);

        encodeClientBehaviors(context, menu);

        wb.finish();
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
        if (tabindex != null) {
            writer.writeAttribute("tabindex", tabindex, null);
        }
        writer.endElement("input");
        writer.endElement("div");
    }
}
