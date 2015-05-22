/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.selectoneradio;

import java.io.IOException;
import java.util.List;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.component.radiobutton.RadioButton;
import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.GridLayoutUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class SelectOneRadioRenderer extends SelectOneRenderer {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectOne", "javax.faces.Radio").getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectOneRadio radio = (SelectOneRadio) component;

        encodeMarkup(context, radio);
        encodeScript(context, radio);
    }

    protected void encodeMarkup(FacesContext context, SelectOneRadio radio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String layout = radio.getLayout();
        if (layout == null) {
            layout = "lineDirection";
        }
        boolean custom = layout.equals("custom");

        if(custom) {
            writer.startElement("span", radio);
            writer.writeAttribute("id", radio.getClientId(context), "id");
            writer.writeAttribute("class", "ui-helper-hidden", null);
            encodeCustomLayout(context, radio);
            writer.endElement("span");
        } 
        else if(layout.equals("responsive")) {
            encodeResponsiveLayout(context, radio);
        } else {
            encodeTabularLayout(context, radio, layout);
        }
    }

    protected void encodeScript(FacesContext context, SelectOneRadio radio) throws IOException {
        String clientId = radio.getClientId(context);
        String layout = radio.getLayout();
        boolean custom = layout != null && layout.equals("custom");

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("SelectOneRadio", radio.resolveWidgetVar(), clientId)
                .attr("custom", custom, false).finish();
    }
    
    protected void encodeResponsiveLayout(FacesContext context, SelectOneRadio radio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = radio.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, radio);
        String style = radio.getStyle();
        String styleClass = radio.getStyleClass();
        String defaultStyleClass = radio.isPlain() ? SelectOneRadio.NATIVE_STYLE_CLASS : SelectOneRadio.STYLE_CLASS;
        defaultStyleClass = defaultStyleClass + " ui-grid ui-grid-responsive";
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass + " " + styleClass;
        
        writer.startElement("div", radio);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        Converter converter = radio.getConverter();
        String name = radio.getClientId(context);
        Object value = radio.getSubmittedValue();
        int columns = radio.getColumns();
        if(value == null) {
            value = radio.getValue();
        }
        Class type = value == null ? String.class : value.getClass();

        if (columns > 0) {
            int idx = 0, colMod;

            for (SelectItem selectItem : selectItems) {
                boolean disabled = selectItem.isDisabled() || radio.isDisabled();
                String id = name + UINamingContainer.getSeparatorChar(context) + idx;
                Object coercedItemValue = coerceToModelType(context, selectItem.getValue(), type);
                boolean selected = (coercedItemValue != null) && coercedItemValue.equals(value);

                colMod = idx % columns;
                if (colMod == 0) {
                    writer.startElement("div", null);
                    writer.writeAttribute("class", "ui-grid-row", null);
                }

                writer.startElement("div", null);
                writer.writeAttribute("class", GridLayoutUtils.getColumnClass(columns), null);
                encodeOption(context, radio, selectItem, id, name, converter, selected, disabled);
                writer.endElement("div");
                
                idx++;
                colMod = idx % columns;

                if (colMod == 0) {
                    writer.endElement("div");
                }
            }
        } else {
            throw new FacesException("The value of columns attribute must be greater than zero.");
        }

        writer.endElement("div");
    }

    protected void encodeTabularLayout(FacesContext context, SelectOneRadio radio, String layout) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = radio.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, radio);
        String style = radio.getStyle();
        String styleClass = radio.getStyleClass();
        String defaultStyleClass = radio.isPlain() ? SelectOneRadio.NATIVE_STYLE_CLASS : SelectOneRadio.STYLE_CLASS;
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass + " " + styleClass;
        
        writer.startElement("table", radio);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeSelectItems(context, radio, selectItems, layout);

        writer.endElement("table");
    }

    protected void encodeSelectItems(FacesContext context, SelectOneRadio radio, List<SelectItem> selectItems, String layout) throws IOException {
        if (layout.equals("lineDirection")) {
            encodeLineLayout(context, radio, selectItems);
        } else if (layout.equals("pageDirection")) {
            encodePageLayout(context, radio, selectItems);
        } else if (layout.equals("grid")) {
            encodeGridLayout(context, radio, selectItems);
        } else {
            throw new FacesException("Invalid '" + layout + "' type for component '" + radio.getClientId(context) + "'.");
        }
    }

    protected void encodeCustomLayout(FacesContext context, SelectOneRadio radio) throws IOException {
        Converter converter = radio.getConverter();
        String name = radio.getClientId(context);
        Object value = radio.getSubmittedValue();
        List<SelectItem> selectItems = getSelectItems(context, radio);
        if (value == null) {
            value = radio.getValue();
        }
        Class type = value == null ? String.class : value.getClass();

        int idx = 0;
        for (SelectItem selectItem : selectItems) {
            String id = name + UINamingContainer.getSeparatorChar(context) + idx;
            Object coercedItemValue = coerceToModelType(context, selectItem.getValue(), type);
            boolean selected = (coercedItemValue != null) && coercedItemValue.equals(value);
            String itemValueAsString = getOptionAsString(context, radio, converter, selectItem.getValue());

            encodeOptionInput(context, radio, id, name, selected, true, itemValueAsString);
            idx++;
        }
    }

    protected void encodeLineLayout(FacesContext context, SelectOneRadio radio, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = radio.getConverter();
        String name = radio.getClientId(context);
        Object value = radio.getSubmittedValue();
        if (value == null) {
            value = radio.getValue();
        }
        Class type = value == null ? String.class : value.getClass();

        writer.startElement("tr", null);
        int idx = 0;
        for (SelectItem selectItem : selectItems) {
            boolean disabled = selectItem.isDisabled() || radio.isDisabled();
            String id = name + UINamingContainer.getSeparatorChar(context) + idx;
            Object coercedItemValue = coerceToModelType(context, selectItem.getValue(), type);
            boolean selected = (coercedItemValue != null) && coercedItemValue.equals(value);

            encodeOption(context, radio, selectItem, id, name, converter, selected, disabled);
            idx++;
        }
        writer.endElement("tr");
    }

    protected void encodePageLayout(FacesContext context, SelectOneRadio radio, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = radio.getConverter();
        String name = radio.getClientId(context);
        Object value = radio.getSubmittedValue();
        if (value == null) {
            value = radio.getValue();
        }
        Class type = value == null ? String.class : value.getClass();

        int idx = 0;
        for (SelectItem selectItem : selectItems) {
            boolean disabled = selectItem.isDisabled() || radio.isDisabled();
            String id = name + UINamingContainer.getSeparatorChar(context) + idx;
            Object coercedItemValue = coerceToModelType(context, selectItem.getValue(), type);
            boolean selected = (coercedItemValue != null) && coercedItemValue.equals(value);

            writer.startElement("tr", null);
            encodeOption(context, radio, selectItem, id, name, converter, selected, disabled);
            writer.endElement("tr");
            idx++;
        }
    }

    protected void encodeGridLayout(FacesContext context, SelectOneRadio radio, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = radio.getConverter();
        String name = radio.getClientId(context);
        Object value = radio.getSubmittedValue();
        int columns = radio.getColumns();
        if (value == null) {
            value = radio.getValue();
        }
        Class type = value == null ? String.class : value.getClass();

        if (columns > 0) {
            int idx = 0, colMod;

            for (SelectItem selectItem : selectItems) {
                boolean disabled = selectItem.isDisabled() || radio.isDisabled();
                String id = name + UINamingContainer.getSeparatorChar(context) + idx;
                Object coercedItemValue = coerceToModelType(context, selectItem.getValue(), type);
                boolean selected = (coercedItemValue != null) && coercedItemValue.equals(value);

                colMod = idx % columns;
                if (colMod == 0) {
                    writer.startElement("tr", null);
                }

                encodeOption(context, radio, selectItem, id, name, converter, selected, disabled);
                idx++;
                colMod = idx % columns;

                if (colMod == 0) {
                    writer.endElement("tr");
                }
            }
        } else {
            throw new FacesException("The value of columns attribute must be greater than zero.");
        }
    }
    
    protected void encodeResponsiveItems(FacesContext context, SelectOneRadio radio, List<SelectItem> selectItems) throws IOException {
        
    }

    protected void encodeOption(FacesContext context, SelectOneRadio radio, SelectItem option, String id, String name, Converter converter, boolean selected, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, radio, converter, option.getValue());
        String styleClass = radio.isPlain() ? HTML.RADIOBUTTON_NATIVE_CLASS : HTML.RADIOBUTTON_CLASS;

        writer.startElement("td", null);

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        encodeOptionInput(context, radio, id, name, selected, disabled, itemValueAsString);
        encodeOptionOutput(context, radio, selected, disabled);

        writer.endElement("div");
        writer.endElement("td");

        writer.startElement("td", null);
        encodeOptionLabel(context, radio, id, option, disabled);
        writer.endElement("td");
    }

    protected void encodeOptionInput(FacesContext context, SelectOneRadio radio, String id, String name, boolean checked, boolean disabled, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", value, null);

        renderDomEvents(context, radio, SelectOneRadio.SUPPORTED_EVENTS);

        if (radio.getTabindex() != null) {
            writer.writeAttribute("tabindex", radio.getTabindex(), null);
        }
        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }
        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }

        if (RequestContext.getCurrentInstance().getApplicationContext().getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, radio);
        }

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeOptionLabel(FacesContext context, SelectOneRadio radio, String containerClientId, SelectItem option, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = option.getLabel();

        writer.startElement("label", null);
        writer.writeAttribute("for", containerClientId, null);
        if (disabled) {
            writer.writeAttribute("class", "ui-state-disabled", null);
        }

        if (label != null) {
            if (option.isEscape()) {
                writer.writeText(label, null);
            } else {
                writer.write(label);
            }
        }

        writer.endElement("label");
    }

    protected void encodeOptionOutput(FacesContext context, SelectOneRadio radio, boolean selected, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String boxClass = HTML.RADIOBUTTON_BOX_CLASS;
        boxClass = selected ? boxClass + " ui-state-active" : boxClass;
        boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
        boxClass = !radio.isValid() ? boxClass + " ui-state-error" : boxClass;
        String iconClass = selected ? HTML.RADIOBUTTON_CHECKED_ICON_CLASS : HTML.RADIOBUTTON_UNCHECKED_ICON_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeRadioButton(FacesContext context, SelectOneRadio radio, RadioButton button) throws IOException {

    }

    protected Class getValueType(FacesContext context, UIInput input) {
        ValueExpression ve = input.getValueExpression("value");
        Class type = ve == null ? String.class : ve.getType(context.getELContext());

        return type == null ? String.class : type;
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
