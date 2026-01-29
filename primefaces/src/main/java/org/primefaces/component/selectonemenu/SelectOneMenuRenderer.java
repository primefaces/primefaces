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
package org.primefaces.component.selectonemenu;

import org.primefaces.component.column.Column;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import jakarta.faces.render.FacesRenderer;
import jakarta.faces.render.Renderer;

@FacesRenderer(rendererType = SelectOneMenu.DEFAULT_RENDERER, componentFamily = SelectOneMenu.COMPONENT_FAMILY)
public class SelectOneMenuRenderer extends SelectOneRenderer<SelectOneMenu> {

    @Override
    public void decode(FacesContext context, SelectOneMenu component) {
        if (!shouldDecode(component)) {
            return;
        }

        if (component.isEditable()) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();

            // default to user entered input
            String editorInput = params.get(component.getClientId(context) + "_editableInput");
            component.setSubmittedValue(editorInput);

            // #2862 check if it matches a label and if so use the value
            if (LangUtils.isNotBlank(editorInput)) {
                List<SelectItem> selectItems = getSelectItems(context, component);
                Converter<?> converter = component.getConverter();
                SelectItem foundItem = findSelectItemByLabel(context, component, converter, selectItems, editorInput);
                if (foundItem != null) {
                    component.setSubmittedValue(getOptionAsString(context, component, converter, foundItem.getValue()));
                }
            }

            decodeBehaviors(context, component);
        }
        else {
            super.decode(context, component);
        }
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "jakarta.faces.SelectOne",
                "jakarta.faces.Menu");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, SelectOneMenu component) throws IOException {
        if (component.isDynamicLoadRequest(context)) {
            List<SelectItem> selectItems = getSelectItems(context, component);
            String clientId = component.getClientId(context);
            Converter converter = component.getConverter();
            Object values = getValues(component);
            Object submittedValues = getSubmittedValues(component);

            encodeHiddenSelect(context, component, clientId, selectItems, values, submittedValues, converter);
            encodePanelContent(context, component, selectItems);
        }
        else {
            encodeMarkup(context, component);
            encodeScript(context, component);
        }
    }

    protected void encodeMarkup(FacesContext context, SelectOneMenu component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, component);
        String clientId = component.getClientId(context);
        Converter converter = component.getConverter();
        Object values = getValues(component);
        Object submittedValues = getSubmittedValues(component);
        boolean valid = component.isValid();
        String title = component.getTitle();

        String style = component.getStyle();
        String styleClass = createStyleClass(component, SelectOneMenu.STYLE_CLASS);
        styleClass = getStyleClassBuilder(context)
                .add(styleClass)
                .add(ComponentUtils.isRTL(context, component),  SelectOneMenu.RTL_CLASS)
                .add(component.isReadonly(), "ui-state-readonly")
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleclass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        if (title != null) {
            writer.writeAttribute("title", title, "title");
        }

        encodeInput(context, component, clientId, selectItems, values, submittedValues, converter);
        encodeLabel(context, component, selectItems);
        encodeMenuIcon(context, component, valid);
        encodePanel(context, component, selectItems);

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, SelectOneMenu component, String clientId, List<SelectItem> selectItems, Object values,
                               Object submittedValues, Converter converter) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        if (component.isEditable()) {
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
            writer.writeAttribute(HTML.ARIA_CONTROLS, clientId + "_panel", null);
            renderARIACombobox(context, component);
            renderAccessibilityAttributes(context, component);
            renderPassThruAttributes(context, component, HTML.TAB_INDEX);
            renderDomEvents(context, component, HTML.BLUR_FOCUS_EVENTS);

            writer.endElement("input");

            writer.endElement("div");
        }

        //hidden select
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        encodeHiddenSelect(context, component, clientId, selectItems, values, submittedValues, converter);

        writer.endElement("div");

    }

    protected void encodeHiddenSelect(FacesContext context, SelectOneMenu component, String clientId, List<SelectItem> selectItems,
                                      Object values, Object submittedValues, Converter converter) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";

        writer.startElement("select", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("tabindex", "-1", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
        encodeAriaLabel(writer, component);

        if (component.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        if (component.isReadonly()) {
            writer.writeAttribute("readonly", "readonly", null);
        }
        if (component.getOnkeydown() != null) {
            writer.writeAttribute("onkeydown", component.getOnkeydown(), null);
        }
        if (component.getOnkeyup() != null) {
            writer.writeAttribute("onkeyup", component.getOnkeyup(), null);
        }

        renderOnchange(context, component);

        renderValidationMetadata(context, component);

        encodeSelectItems(context, component, selectItems, values, submittedValues, converter);

        writer.endElement("select");
    }

    protected void encodeAriaLabel(ResponseWriter writer, SelectOneMenu component) throws IOException {
        String ariaLabel =  Objects.toString(component.getAttributes().get(HTML.ARIA_LABEL), null);
        if (LangUtils.isBlank(ariaLabel)) {
            String label = component.getLabel();
            ariaLabel = LangUtils.isBlank(label) ? component.getPlaceholder() : label;
        }
        if (LangUtils.isNotBlank(ariaLabel)) {
            writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);
        }
    }

    protected void encodeLabel(FacesContext context, SelectOneMenu component, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String tabIndex = LangUtils.isNotBlank(component.getTabindex()) ? component.getTabindex() : "0";

        if (component.isEditable()) {
            writer.startElement("input", null);
            writer.writeAttribute("id", component.getClientId(context) + "_editableInput", null);
            writer.writeAttribute("type", "text", null);
            writer.writeAttribute("name", component.getClientId(context) + "_editableInput", null);
            writer.writeAttribute("class", SelectOneMenu.LABEL_CLASS, null);
            writer.writeAttribute("tabindex", tabIndex, null);
            writer.writeAttribute("autocomplete", component.getAutocomplete(), null);
            encodeAriaLabel(writer, component);
            renderAccessibilityAttributes(context, component);

            String valueToRender = ComponentUtils.getValueToRender(context, component);
            for (int i = 0; i < selectItems.size(); i++) {
                SelectItem selectItem = selectItems.get(i);
                if (isSelected(context, component, valueToRender, selectItem.getValue(), null)) {
                    valueToRender = selectItem.getLabel();
                    break;
                }
            }
            writer.writeAttribute("value", valueToRender, null);

            if (component.getMaxlength() != Integer.MAX_VALUE) {
                writer.writeAttribute("maxlength", component.getMaxlength(), null);
            }

            if (component.getPlaceholder() != null) {
                writer.writeAttribute("placeholder", component.getPlaceholder(), null);
            }

            if (component.getOnkeydown() != null) {
                writer.writeAttribute("onkeydown", component.getOnkeydown(), null);
                renderDomEvent(context, component, "onkeydown", "keydown", "keydown", null);
            }
            if (component.getOnkeyup() != null) {
                writer.writeAttribute("onkeyup", component.getOnkeyup(), null);
                renderDomEvent(context, component, "onkeyup", "keyup", "keyup", null);
            }

            writer.endElement("input");
        }
        else {
            String clientId = component.getClientId(context);

            writer.startElement("span", null);
            writer.writeAttribute("id", component.getClientId(context) + "_label", null);
            writer.writeAttribute("class", SelectOneMenu.LABEL_CLASS, null);
            writer.writeAttribute("tabindex", tabIndex, null);
            if (component.getPlaceholder() != null) {
                writer.writeAttribute("data-placeholder", component.getPlaceholder(), null);
            }

            //for keyboard accessibility and ScreenReader
            writer.writeAttribute(HTML.ARIA_CONTROLS, clientId + "_panel", null);

            if (component.isDisabled()) {
                writer.writeAttribute(HTML.ARIA_DISABLED, "true", null);
            }

            encodeAriaLabel(writer, component);
            renderARIACombobox(context, component);
            renderPassThruAttributes(context, component, HTML.TAB_INDEX);
            renderDomEvents(context, component, HTML.BLUR_FOCUS_EVENTS);

            String label = component.getLabel();
            if (label != null) {
                writer.writeText(label, null);
            }
            writer.write("&nbsp;");
            writer.endElement("span");
        }
    }

    protected void encodeMenuIcon(FacesContext context, SelectOneMenu component, boolean valid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String iconClass = valid ? SelectOneMenu.TRIGGER_CLASS : SelectOneMenu.TRIGGER_CLASS + " ui-state-error";

        writer.startElement("div", null);
        writer.writeAttribute("class", iconClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-triangle-1-s ui-c", null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodePanel(FacesContext context, SelectOneMenu component, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String panelStyle = component.getPanelStyle();
        String panelStyleClass = component.getPanelStyleClass();
        panelStyleClass = panelStyleClass == null ? SelectOneMenu.PANEL_CLASS : SelectOneMenu.PANEL_CLASS + " " + panelStyleClass;

        if (ComponentUtils.isRTL(context, component)) {
            panelStyleClass = panelStyleClass + " " + SelectOneMenu.RTL_PANEL_CLASS;
        }

        String height = null;
        try {
            height = Integer.parseInt(component.getHeight()) + "px";
        }
        catch (NumberFormatException e) {
            height = component.getHeight();
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context) + "_panel", null);
        writer.writeAttribute("class", panelStyleClass, null);
        writer.writeAttribute("tabindex", "-1", null);
        if (panelStyle != null) {
            writer.writeAttribute("style", panelStyle, null);
        }

        if (component.isFilter()) {
            encodeFilter(context, component);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", SelectOneMenu.ITEMS_WRAPPER_CLASS, null);
        writer.writeAttribute("style", "max-height:" + height, null);

        if (!component.isDynamic()) {
            encodePanelContent(context, component, selectItems);
        }

        writer.endElement("div");

        encodePanelFooter(context, component);
        writer.endElement("div");
    }

    protected void encodePanelContent(FacesContext context, SelectOneMenu component, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean customContent = component.getVar() != null;

        if (customContent) {
            ComponentUtils.runWithoutFacesContextVar(context, Constants.HELPER_RENDERER, () -> {
                List<Column> columns = component.getColumns();

                writer.startElement("table", null);
                writer.writeAttribute("id", component.getClientId(context) + "_table", null);
                writer.writeAttribute("class", SelectOneMenu.TABLE_CLASS, null);
                writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_LISTBOX, null);
                writer.writeAttribute(HTML.ARIA_MULITSELECTABLE, "false", null);
                encodeColumnsHeader(context, component, columns);
                writer.startElement("tbody", null);
                writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_GROUP, null);
                encodeOptionsAsTable(context, component, selectItems, columns);
                writer.endElement("tbody");
                writer.endElement("table");
            });
        }
        else {
            // Rendering was moved to the client - see renderPanelContentFromHiddenSelect as part of forms.selectonemenu.js
        }
    }

    protected void encodePanelFooter(FacesContext context, SelectOneMenu component) throws IOException {
        UIComponent facet = component.getFacet("footer");
        if (!FacetUtils.shouldRenderFacet(facet)) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", null);
        writer.writeAttribute("class", SelectOneMenu.FOOTER_CLASS, null);
        facet.encodeAll(context);
        writer.endElement("div");
    }

    protected void encodeColumnsHeader(FacesContext context, SelectOneMenu component, List<Column> columns)
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
                String styleClass = column.getStyleClass() == null ? "ui-state-default" : "ui-state-default" + column.getStyleClass();

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

    protected void encodeOptionsAsTable(FacesContext context, SelectOneMenu component, List<SelectItem> selectItems, List<Column> columns)
            throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String var = component.getVar();
        ValueExpression value = component.getValueExpression("value");
        Class<?> valueType = value == null ? null : value.getType(context.getELContext());

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            Object itemValue = selectItem.getValue();
            String itemLabel = getOptionLabel(selectItem);

            String itemStyleClass = SelectOneMenu.ROW_CLASS;
            if (selectItem.isNoSelectionOption()) {
                itemStyleClass += " ui-noselection-option";
            }
            if (selectItem.isDisabled()) {
                itemStyleClass += " ui-state-disabled";
            }

            context.getExternalContext().getRequestMap().put(var, itemValue);

            writer.startElement("tr", getSelectItemComponent(selectItem));
            writer.writeAttribute("class", itemStyleClass, null);
            writer.writeAttribute("data-label", itemLabel, null);
            writer.writeAttribute("role", "option", null);
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

                    encodeIndexedId(context, column, i);
                    writer.endElement("td");
                }
            }

            writer.endElement("tr");
        }

        context.getExternalContext().getRequestMap().put(var, null);
    }

    protected void encodeScript(FacesContext context, SelectOneMenu component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectOneMenu", component)
                .attr("editable", component.isEditable(), false)
                .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, component, component.getAppendTo()))
                .attr("syncTooltip", component.isSyncTooltip(), false)
                .attr("alwaysDisplayLabel", component.isAlwaysDisplayLabel(), false)
                .attr("label", component.getLabel(), null)
                .attr("labelTemplate", component.getLabelTemplate(), null)
                .attr("autoWidth", component.getAutoWidth(), "auto")
                .attr("dynamic", component.isDynamic(), false)
                .attr("touchable", ComponentUtils.isTouchable(context, component),  true)
                .attr("renderPanelContentOnClient", component.getVar() == null,  false);

        if (component.isFilter()) {
            wb.attr("filter", true)
                    .attr("filterMatchMode", component.getFilterMatchMode(), null)
                    .nativeAttr("filterFunction", component.getFilterFunction(), null)
                    .attr("caseSensitive", component.isCaseSensitive(), false)
                    .attr("filterNormalize", component.isFilterNormalize(), false);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeSelectItems(FacesContext context, SelectOneMenu menu, List<SelectItem> selectItems, Object values,
                                     Object submittedValues, Converter converter) throws IOException {

        boolean isInitialDynamic = menu.isDynamic() && !menu.isDynamicLoadRequest(context);

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            boolean selected = encodeOption(context, menu, selectItem, values, submittedValues, converter, i);
            if (selected && isInitialDynamic) {
                return;
            }
        }
    }

    /**
     * Encodes one SelectItem.
     * @return true if SelectItem is selected.
     * @throws IOException
     */
    protected boolean encodeOption(FacesContext context, SelectOneMenu component, SelectItem option, Object values, Object submittedValues,
                                   Converter converter, int itemIndex) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        if (option instanceof SelectItemGroup) {
            SelectItemGroup group = (SelectItemGroup) option;

            writer.startElement("optgroup", null);
            writer.writeAttribute("label", group.getLabel(), null);
            if (group.isDisabled()) {
                writer.writeAttribute("disabled", "disabled", null);
            }
            writer.writeAttribute("data-escape", String.valueOf(group.isEscape()), null);
            if (option.getDescription() != null) {
                writer.writeAttribute("data-title", option.getDescription(), null);
            }
            for (SelectItem groupItem : group.getSelectItems()) {
                encodeOption(context, component, groupItem, values, submittedValues, converter, itemIndex);
            }
            writer.endElement("optgroup");

            return false;
        }
        else {
            String itemValueAsString = getOptionAsString(context, component, converter, option.getValue());
            boolean disabled = option.isDisabled();
            boolean isEscape = option.isEscape();
            boolean isNoSelectionOption = option.isNoSelectionOption();

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

            if (!component.isDynamic() || (component.isDynamic() && (selected || component.isDynamicLoadRequest(context) || itemIndex == 0))) {
                writer.startElement("option", getSelectItemComponent(option));
                writer.writeAttribute("value", itemValueAsString, null);
                if (disabled) {
                    writer.writeAttribute("disabled", "disabled", null);
                }
                if (selected) {
                    writer.writeAttribute("selected", "selected", null);
                }
                if (isNoSelectionOption) {
                    writer.writeAttribute("data-noselection-option", "true", null);
                }
                writer.writeAttribute("data-escape", String.valueOf(isEscape), null);
                if (option.getDescription() != null) {
                    writer.writeAttribute("data-title", option.getDescription(), null);
                }

                writer.writeText(getOptionLabel(option), null);

                writer.endElement("option");
            }

            return selected;
        }
    }

    protected String getOptionLabel(SelectItem option) {
        String itemLabel = option.getLabel();
        return isValueBlank(itemLabel) ? "&nbsp;" : itemLabel;
    }


    @Override
    public void encodeChildren(FacesContext facesContext, SelectOneMenu component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    protected String getSubmitParam(FacesContext context, SelectOneMenu component) {
        return component.getClientId(context) + "_input";
    }

    protected void encodeFilter(FacesContext context, SelectOneMenu component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = component.getClientId(context) + "_filter";

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-selectonemenu-filter-container", null);

        writer.startElement("input", null);
        writer.writeAttribute("class", "ui-selectonemenu-filter ui-inputfield ui-inputtext ui-widget ui-state-default", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute(HTML.ARIA_CONTROLS, component.getClientId(context) + "_table", null);

        if (component.getFilterPlaceholder() != null) {
            writer.writeAttribute("placeholder", component.getFilterPlaceholder(), null);
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
