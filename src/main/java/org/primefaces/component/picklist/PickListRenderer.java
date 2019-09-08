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
package org.primefaces.component.picklist;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.component.column.Column;
import org.primefaces.model.DualListModel;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class PickListRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        PickList pickList = (PickList) component;
        if (!shouldDecode(pickList)) {
            return;
        }
        String clientId = pickList.getClientId(context);
        Map<String, String[]> params = context.getExternalContext().getRequestParameterValuesMap();

        String sourceParamKey = clientId + "_source";
        String targetParamKey = clientId + "_target";

        String[] sourceParam = params.containsKey(sourceParamKey) ? params.get(sourceParamKey) : new String[]{};
        String[] targetParam = params.containsKey(targetParamKey) ? params.get(targetParamKey) : new String[]{};

        pickList.setSubmittedValue(new String[][]{sourceParam, targetParam});

        decodeBehaviors(context, pickList);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        PickList pickList = (PickList) component;

        encodeMarkup(facesContext, pickList);
        encodeScript(facesContext, pickList);
    }

    protected void encodeMarkup(FacesContext context, PickList pickList) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = pickList.getClientId(context);
        DualListModel model = getModelValueToRender(context, pickList);
        String styleClass = pickList.getStyleClass();
        styleClass = styleClass == null ? PickList.CONTAINER_CLASS : PickList.CONTAINER_CLASS + " " + styleClass;
        String labelDisplay = pickList.getLabelDisplay();
        boolean vertical = pickList.getOrientation().equals("vertical");
        if (vertical) {
            styleClass += " ui-picklist-vertical";
        }

        if (pickList.isResponsive()) {
            styleClass += " ui-picklist-responsive";
        }

        writer.startElement("div", pickList);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
        if (pickList.getStyle() != null) {
            writer.writeAttribute("style", pickList.getStyle(), null);
        }

        //Target List Reorder Buttons
        if (pickList.isShowSourceControls()) {
            encodeListControls(context, pickList, PickList.SOURCE_CONTROLS, labelDisplay);
        }

        //Source List
        encodeList(context, pickList, clientId + "_source", PickList.SOURCE_CLASS, model.getSource(),
                pickList.getFacet("sourceCaption"), pickList.isShowSourceFilter(), true);

        //Buttons
        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.BUTTONS_CLASS, null);
        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.BUTTONS_CELL_CLASS, null);
        if (vertical) {
            encodeButton(context, pickList.getAddLabel(), PickList.ADD_BUTTON_CLASS, PickList.VERTICAL_ADD_BUTTON_ICON_CLASS, labelDisplay);
            encodeButton(context, pickList.getAddAllLabel(), PickList.ADD_ALL_BUTTON_CLASS, PickList.VERTICAL_ADD_ALL_BUTTON_ICON_CLASS, labelDisplay);
            encodeButton(context, pickList.getRemoveLabel(), PickList.REMOVE_BUTTON_CLASS, PickList.VERTICAL_REMOVE_BUTTON_ICON_CLASS, labelDisplay);
            encodeButton(context, pickList.getRemoveAllLabel(), PickList.REMOVE_ALL_BUTTON_CLASS, PickList.VERTICAL_REMOVE_ALL_BUTTON_ICON_CLASS, labelDisplay);
        }
        else {
            encodeButton(context, pickList.getAddLabel(), PickList.ADD_BUTTON_CLASS, PickList.ADD_BUTTON_ICON_CLASS, labelDisplay);
            encodeButton(context, pickList.getAddAllLabel(), PickList.ADD_ALL_BUTTON_CLASS, PickList.ADD_ALL_BUTTON_ICON_CLASS, labelDisplay);
            encodeButton(context, pickList.getRemoveLabel(), PickList.REMOVE_BUTTON_CLASS, PickList.REMOVE_BUTTON_ICON_CLASS, labelDisplay);
            encodeButton(context, pickList.getRemoveAllLabel(), PickList.REMOVE_ALL_BUTTON_CLASS, PickList.REMOVE_ALL_BUTTON_ICON_CLASS, labelDisplay);
        }
        writer.endElement("div");
        writer.endElement("div");

        //Target List
        encodeList(context, pickList, clientId + "_target", PickList.TARGET_CLASS, model.getTarget(),
                pickList.getFacet("targetCaption"), pickList.isShowTargetFilter(), false);

        //Target List Reorder Buttons
        if (pickList.isShowTargetControls()) {
            encodeListControls(context, pickList, PickList.TARGET_CONTROLS, labelDisplay);
        }

        /* For ScreenReader */
        encodeAriaRegion(context, clientId);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, PickList pickList) throws IOException {
        String clientId = pickList.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("PickList", pickList.resolveWidgetVar(context), clientId)
                .attr("effect", pickList.getEffect())
                .attr("effectSpeed", pickList.getEffectSpeed())
                .attr("escape", pickList.isEscape())
                .attr("showSourceControls", pickList.isShowSourceControls(), false)
                .attr("showTargetControls", pickList.isShowTargetControls(), false)
                .attr("disabled", pickList.isDisabled(), false)
                .attr("filterEvent", pickList.getFilterEvent(), null)
                .attr("filterDelay", pickList.getFilterDelay(), Integer.MAX_VALUE)
                .attr("filterMatchMode", pickList.getFilterMatchMode(), null)
                .nativeAttr("filterFunction", pickList.getFilterFunction(), null)
                .attr("showCheckbox", pickList.isShowCheckbox(), false)
                .callback("onTransfer", "function(e)", pickList.getOnTransfer())
                .attr("tabindex", pickList.getTabindex(), "0")
                .attr("escapeValue", pickList.isEscapeValue());

        encodeClientBehaviors(context, pickList);

        wb.finish();
    }

    protected void encodeListControls(FacesContext context, PickList pickList, String styleClass, String labelDisplay) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);
        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.BUTTONS_CELL_CLASS, null);
        encodeButton(context, pickList.getMoveUpLabel(), PickList.MOVE_UP_BUTTON_CLASS, PickList.MOVE_UP_BUTTON_ICON_CLASS, labelDisplay);
        encodeButton(context, pickList.getMoveTopLabel(), PickList.MOVE_TOP_BUTTON_CLASS, PickList.MOVE_TOP_BUTTON_ICON_CLASS, labelDisplay);
        encodeButton(context, pickList.getMoveDownLabel(), PickList.MOVE_DOWN_BUTTON_CLASS, PickList.MOVE_DOWN_BUTTON_ICON_CLASS, labelDisplay);
        encodeButton(context, pickList.getMoveBottomLabel(), PickList.MOVE_BOTTOM_BUTTON_CLASS, PickList.MOVE_BOTTOM_BUTTON_ICON_CLASS, labelDisplay);
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

    protected void encodeButton(FacesContext context, String title, String styleClass, String icon, String labelDisplay) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean tooltip = labelDisplay.equals("tooltip");
        String buttonClass = tooltip ? HTML.BUTTON_ICON_ONLY_BUTTON_CLASS : HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS;

        writer.startElement("button", null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", buttonClass + " " + styleClass, null);

        if (tooltip) {
            writer.writeAttribute("title", title, null);
        }

        //icon
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + icon, null);
        writer.endElement("span");

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.writeText(title, null);
        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeList(FacesContext context, PickList pickList, String listId, String styleClass, List model, UIComponent caption,
                              boolean filter, boolean isSource) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.LIST_WRAPPER_CLASS, null);

        // only render required on target list
        if (!isSource) {
            renderARIARequired(context, pickList);
        }

        if (filter) {
            encodeFilter(context, pickList, listId + "_filter", isSource);
        }

        if (caption != null) {
            encodeCaption(context, caption);
            styleClass += " ui-corner-bottom";
        }
        else {
            styleClass += " ui-corner-all";
        }

        writer.startElement("ul", null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "menu", null);

        encodeOptions(context, pickList, model);

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
    protected void encodeOptions(FacesContext context, PickList pickList, List model) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = pickList.getVar();
        Converter converter = pickList.getConverter();
        boolean showCheckbox = pickList.isShowCheckbox();

        for (Iterator it = model.iterator(); it.hasNext(); ) {
            Object item = it.next();
            context.getExternalContext().getRequestMap().put(var, item);
            String itemValue = converter != null ?
                               converter.getAsString(context, pickList, pickList.getItemValue()) : pickList.getItemValue().toString();
            String itemLabel = pickList.getItemLabel();
            String itemClass = pickList.isItemDisabled() ? PickList.ITEM_CLASS + " " + PickList.ITEM_DISABLED_CLASS : PickList.ITEM_CLASS;

            writer.startElement("li", null);
            writer.writeAttribute("class", itemClass, null);
            writer.writeAttribute("data-item-value", itemValue, null);
            writer.writeAttribute("data-item-label", itemLabel, null);
            writer.writeAttribute("role", "menuitem", null);

            if (pickList.getChildCount() > 0) {
                writer.startElement("table", null);
                writer.writeAttribute("role", "presentation", null);

                writer.startElement("tbody", null);
                writer.startElement("tr", null);

                if (showCheckbox) {
                    writer.startElement("td", null);
                    RendererUtils.encodeCheckbox(context, false);
                    writer.endElement("td");
                }

                for (UIComponent kid : pickList.getChildren()) {
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
                    RendererUtils.encodeCheckbox(context, false);
                }

                if (pickList.isEscape()) {
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

    protected void encodeFilter(FacesContext context, PickList pickList, String name, boolean isSource) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String styleClass = PickList.FILTER_CLASS + (isSource ? " ui-source-filter-input" : " ui-target-filter-input");

        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.FILTER_CONTAINER, null);

        writer.startElement("input", null);
        writer.writeAttribute("id", name, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("class", styleClass, null);
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
        writer.writeAttribute("role", "region", null);
        writer.writeAttribute(HTML.ARIA_LIVE, "polite", null);
        writer.writeAttribute(HTML.ARIA_ATOMIC, "true", null);
        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    protected DualListModel getModelValueToRender(FacesContext context, PickList pickList) {
        Object submittedValue = pickList.getSubmittedValue();
        if (submittedValue != null) {
            return (DualListModel) getConvertedValue(context, pickList, submittedValue);
        }

        return (DualListModel) pickList.getValue();
    }
}
