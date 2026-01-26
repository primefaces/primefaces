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
package org.primefaces.component.picklist;

import org.primefaces.component.column.Column;
import org.primefaces.model.DualListModel;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = PickList.DEFAULT_RENDERER, componentFamily = PickList.COMPONENT_FAMILY)
public class PickListRenderer extends InputRenderer<PickList> {

    @Override
    public void decode(FacesContext context, PickList component) {
        if (!shouldDecode(component)) {
            return;
        }
        String clientId = component.getClientId(context);
        Map<String, String[]> params = context.getExternalContext().getRequestParameterValuesMap();

        String sourceParamKey = clientId + "_source";
        String targetParamKey = clientId + "_target";

        String[] sourceParam = params.containsKey(sourceParamKey) ? params.get(sourceParamKey) : new String[]{};
        String[] targetParam = params.containsKey(targetParamKey) ? params.get(targetParamKey) : new String[]{};

        component.setSubmittedValue(new String[][]{sourceParam, targetParam});

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, PickList component) throws IOException {
        encodeMarkup(facesContext, component);
        encodeScript(facesContext, component);
    }

    protected void encodeMarkup(FacesContext context, PickList component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        DualListModel model = getModelValueToRender(context, component);
        String styleClass = component.getStyleClass();
        styleClass = styleClass == null ? PickList.CONTAINER_CLASS : PickList.CONTAINER_CLASS + " " + styleClass;
        String labelDisplay = component.getLabelDisplay();
        boolean vertical = component.getOrientation().equals("vertical");
        if (vertical) {
            styleClass += " ui-picklist-vertical";
        }

        if (component.isResponsive()) {
            styleClass += " ui-picklist-responsive";
        }

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }

        //Target List Reorder Buttons
        if (component.isShowSourceControls()) {
            encodeListControls(context, component, PickList.SOURCE_CONTROLS, labelDisplay);
        }

        //Source List
        encodeList(context, component, clientId + "_source", PickList.SOURCE_CLASS, model.getSource(),
                component.getSourceCaptionFacet(), component.isShowSourceFilter(), true);

        //Buttons
        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.BUTTONS_CLASS, null);
        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.BUTTONS_CELL_CLASS, null);
        if (vertical) {
            encodeButton(context, PickList.ADD_BUTTON_CLASS, PickList.VERTICAL_ADD_BUTTON_ICON_CLASS, labelDisplay);
            encodeButton(context, PickList.ADD_ALL_BUTTON_CLASS, PickList.VERTICAL_ADD_ALL_BUTTON_ICON_CLASS, labelDisplay);
            encodeButton(context, PickList.REMOVE_BUTTON_CLASS, PickList.VERTICAL_REMOVE_BUTTON_ICON_CLASS, labelDisplay);
            encodeButton(context, PickList.REMOVE_ALL_BUTTON_CLASS, PickList.VERTICAL_REMOVE_ALL_BUTTON_ICON_CLASS, labelDisplay);
        }
        else {
            encodeButton(context, PickList.ADD_BUTTON_CLASS, PickList.ADD_BUTTON_ICON_CLASS, labelDisplay);
            encodeButton(context, PickList.ADD_ALL_BUTTON_CLASS, PickList.ADD_ALL_BUTTON_ICON_CLASS, labelDisplay);
            encodeButton(context, PickList.REMOVE_BUTTON_CLASS, PickList.REMOVE_BUTTON_ICON_CLASS, labelDisplay);
            encodeButton(context, PickList.REMOVE_ALL_BUTTON_CLASS, PickList.REMOVE_ALL_BUTTON_ICON_CLASS, labelDisplay);
        }
        writer.endElement("div");
        writer.endElement("div");

        //Target List
        encodeList(context, component, clientId + "_target", PickList.TARGET_CLASS, model.getTarget(),
                component.getTargetCaptionFacet(), component.isShowTargetFilter(), false);

        //Target List Reorder Buttons
        if (component.isShowTargetControls()) {
            encodeListControls(context, component, PickList.TARGET_CONTROLS, labelDisplay);
        }

        /* For ScreenReader */
        encodeAriaRegion(context, clientId);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, PickList component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("PickList", component)
                .attr("effect", component.getEffect())
                .attr("effectSpeed", component.getEffectSpeed())
                .attr("escape", component.isEscape())
                .attr("dragDrop", component.isDragDrop(), true)
                .attr("showSourceControls", component.isShowSourceControls(), false)
                .attr("showTargetControls", component.isShowTargetControls(), false)
                .attr("disabled", component.isDisabled(), false)
                .attr("filterEvent", component.getFilterEvent(), null)
                .attr("filterDelay", component.getFilterDelay(), Integer.MAX_VALUE)
                .attr("filterMatchMode", component.getFilterMatchMode(), null)
                .attr("filterNormalize", component.isFilterNormalize(), false)
                .nativeAttr("filterFunction", component.getFilterFunction(), null)
                .attr("showCheckbox", component.isShowCheckbox(), false)
                .callback("onTransfer", "function(e)", component.getOnTransfer())
                .attr("tabindex", component.getTabindex(), "0")
                .attr("escapeValue", component.isEscapeValue())
                .attr("transferOnDblclick", component.isTransferOnDblclick(), true)
                .attr("transferOnCheckboxClick", component.isTransferOnCheckboxClick(), false);

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeListControls(FacesContext context, PickList component, String styleClass, String labelDisplay) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);
        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.BUTTONS_CELL_CLASS, null);
        encodeButton(context, PickList.MOVE_UP_BUTTON_CLASS, PickList.MOVE_UP_BUTTON_ICON_CLASS, labelDisplay);
        encodeButton(context, PickList.MOVE_TOP_BUTTON_CLASS, PickList.MOVE_TOP_BUTTON_ICON_CLASS, labelDisplay);
        encodeButton(context, PickList.MOVE_DOWN_BUTTON_CLASS, PickList.MOVE_DOWN_BUTTON_ICON_CLASS, labelDisplay);
        encodeButton(context, PickList.MOVE_BOTTOM_BUTTON_CLASS, PickList.MOVE_BOTTOM_BUTTON_ICON_CLASS, labelDisplay);
        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodeCaption(FacesContext context, UIComponent caption) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.CAPTION_CLASS, null);
        caption.encodeAll(context);
        writer.endElement("div");
    }

    protected void encodeButton(FacesContext context, String styleClass, String icon, String labelDisplay) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean tooltip = "tooltip".equals(labelDisplay);
        String buttonClass = tooltip ? HTML.BUTTON_ICON_ONLY_BUTTON_CLASS : HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS;

        writer.startElement("button", null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", buttonClass + " " + styleClass, null);

        //icon
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + icon, null);
        writer.endElement("span");

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeList(FacesContext context, PickList component, String listId, String styleClass, List model, UIComponent caption,
                              boolean filter, boolean isSource) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.LIST_WRAPPER_CLASS, null);

        if (filter) {
            encodeFilter(context, component, listId + "_filter", isSource);
        }

        if (FacetUtils.shouldRenderFacet(caption)) {
            encodeCaption(context, caption);
        }

        writer.startElement("ul", null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENU, null);

        encodeOptions(context, component, model, isSource);

        writer.endElement("ul");

        encodeListInput(context, listId);

        writer.endElement("div");
    }

    protected void encodeListInput(FacesContext context, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("select", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("multiple", "multiple", null);
        writer.writeAttribute("class", "ui-helper-hidden", null);

        //items generated on client side
        writer.endElement("select");
    }

    @SuppressWarnings("unchecked")
    protected void encodeOptions(FacesContext context, PickList component, List model, boolean isSource) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = component.getVar();
        Converter converter = component.getConverter();
        boolean showCheckbox = component.isShowCheckbox();
        boolean checkboxChecked = component.isTransferOnCheckboxClick() && !isSource;

        for (Iterator it = model.iterator(); it.hasNext(); ) {
            Object item = it.next();
            context.getExternalContext().getRequestMap().put(var, item);
            String itemValue = converter != null ?
                               converter.getAsString(context, component, component.getItemValue()) : component.getItemValue().toString();
            String itemLabel = component.getItemLabel();
            String itemClass = component.isItemDisabled() ? PickList.ITEM_CLASS + " " + PickList.ITEM_DISABLED_CLASS : PickList.ITEM_CLASS;

            writer.startElement("li", null);
            writer.writeAttribute("class", itemClass, null);
            writer.writeAttribute("data-item-value", itemValue, null);
            writer.writeAttribute("data-item-label", itemLabel, null);
            writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENUITEM, null);

            if (component.getChildCount() > 0) {
                writer.startElement("table", null);
                writer.writeAttribute(HTML.ARIA_ROLE, "presentation", null);

                writer.startElement("tbody", null);
                writer.startElement("tr", null);

                if (showCheckbox) {
                    writer.startElement("td", null);
                    RendererUtils.encodeCheckbox(context, checkboxChecked);
                    writer.endElement("td");
                }

                for (UIComponent kid : component.getChildren()) {
                    if (kid instanceof Column && kid.isRendered()) {
                        Column column = (Column) kid;

                        writer.startElement("td", null);
                        if (column.getStyle() != null) {
                            writer.writeAttribute("style", column.getStyle(), null);
                        }
                        if (column.getStyleClass() != null) {
                            writer.writeAttribute("class", column.getStyleClass(), null);
                        }

                        renderChildren(context, column);
                        writer.endElement("td");
                    }
                }

                writer.endElement("tr");
                writer.endElement("tbody");
                writer.endElement("table");
            }
            else {
                if (showCheckbox) {
                    RendererUtils.encodeCheckbox(context, checkboxChecked);
                }

                if (component.isEscape()) {
                    writer.writeText(itemLabel, null);
                }
                else {
                    writer.write(itemLabel);
                }
            }

            writer.endElement("li");
        }

        context.getExternalContext().getRequestMap().remove(var);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        try {
            PickList pickList = (PickList) component;
            String[][] value = (String[][]) submittedValue;
            String[] sourceValue = value[0];
            String[] targetValue = value[1];
            DualListModel model = new DualListModel();

            pickList.populateModel(context, sourceValue, model.getSource());
            pickList.populateModel(context, targetValue, model.getTarget());

            return model;
        }
        catch (Exception exception) {
            throw new ConverterException(exception);
        }
    }

    protected void encodeFilter(FacesContext context, PickList picklist, String name, boolean isSource) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String styleClass = PickList.FILTER_CLASS + (isSource ? " ui-source-filter-input" : " ui-target-filter-input");
        String placeholder = isSource ? picklist.getSourceFilterPlaceholder() : picklist.getTargetFilterPlaceholder();
        String ariaLabel = LangUtils.isNotBlank(placeholder) ? placeholder : null;

        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.FILTER_CONTAINER, null);

        writer.startElement("input", null);
        writer.writeAttribute("id", name, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("class", styleClass, null);
        if (LangUtils.isNotBlank(ariaLabel)) {
            writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);
        }

        if (LangUtils.isNotBlank(placeholder)) {
            writer.writeAttribute("placeholder", placeholder, null);
        }
        writer.endElement("input");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-search", null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeAriaRegion(FacesContext context, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_ariaRegion", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        writer.writeAttribute(HTML.ARIA_ROLE, "region", null);
        writer.writeAttribute(HTML.ARIA_LIVE, "polite", null);
        writer.writeAttribute(HTML.ARIA_ATOMIC, "true", null);
        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, PickList component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    protected DualListModel getModelValueToRender(FacesContext context, PickList component) {
        Object submittedValue = component.getSubmittedValue();
        if (submittedValue != null && submittedValue instanceof String[][]) {
            return (DualListModel) getConvertedValue(context, component, submittedValue);
        }

        return (DualListModel) component.getValue();
    }
}
