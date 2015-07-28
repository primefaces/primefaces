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
package org.primefaces.component.selectmanycheckbox;

import java.io.IOException;
import java.util.List;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.SelectManyRenderer;
import org.primefaces.util.GridLayoutUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class SelectManyCheckboxRenderer extends SelectManyRenderer {

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectMany", "javax.faces.Checkbox").getConvertedValue(context, component, submittedValue);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectManyCheckbox checkbox = (SelectManyCheckbox) component;

        encodeMarkup(context, checkbox);
        encodeScript(context, checkbox);
    }

    protected void encodeMarkup(FacesContext context, SelectManyCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String layout = checkbox.getLayout();
        if(layout == null) {
            layout = "lineDirection";
        }
        boolean custom = (layout.equals("custom"));
                
        if(custom) {
            writer.startElement("span", checkbox);
            writer.writeAttribute("id", checkbox.getClientId(context), "id");
            writer.writeAttribute("class", "ui-helper-hidden", null);
            encodeCustomLayout(context, checkbox);
            writer.endElement("span");
        }
        else if(layout.equals("responsive")) {
            encodeResponsiveLayout(context, checkbox);
        }
        else {
            encodeTabularLayout(context, checkbox, layout);
        }
    }

    protected void encodeScript(FacesContext context, SelectManyCheckbox checkbox) throws IOException {
        String clientId = checkbox.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        String layout = checkbox.getLayout();
        boolean custom = (layout != null && layout.equals("custom"));
        
        wb.initWithDomReady("SelectManyCheckbox", checkbox.resolveWidgetVar(), clientId)
            .attr("custom", custom, false).finish();
    }
    
    protected void encodeResponsiveLayout(FacesContext context, SelectManyCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);
        String style = checkbox.getStyle();
        String styleClass = checkbox.getStyleClass();
        styleClass = (styleClass == null) ? SelectManyCheckbox.STYLE_CLASS : SelectManyCheckbox.STYLE_CLASS + " " + styleClass;
        styleClass = styleClass + " ui-grid ui-grid-responsive";
        int columns = checkbox.getColumns();
        
        if(columns <= 0) {
            throw new FacesException("The value of columns attribute must be greater than zero.");
        }
        
        writer.startElement("div", checkbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        List<SelectItem> selectItems = getSelectItems(context, checkbox);
        Converter converter = checkbox.getConverter();
        Object values = getValues(checkbox);
        Object submittedValues = getSubmittedValues(checkbox);
        int idx = 0, colMod;
        for(SelectItem selectItem : selectItems) {
            colMod = idx % columns;
            if(colMod == 0) {
                writer.startElement("div", null);
                writer.writeAttribute("class", "ui-grid-row", null);
            }

            writer.startElement("div", null);
            writer.writeAttribute("class", GridLayoutUtils.getColumnClass(columns), null);
            encodeOption(context, checkbox, values, submittedValues, converter, selectItem, idx);
            writer.endElement("div");
            
            idx++;
            colMod = idx % columns;

            if(colMod == 0) {
                writer.endElement("div");
            }
        }
        
        writer.endElement("div");
    }
    
    protected void encodeTabularLayout(FacesContext context, SelectManyCheckbox checkbox, String layout) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);
        String style = checkbox.getStyle();
        String styleClass = checkbox.getStyleClass();
        styleClass = styleClass == null ? SelectManyCheckbox.STYLE_CLASS : SelectManyCheckbox.STYLE_CLASS + " " + styleClass;
        
        writer.startElement("table", checkbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeSelectItems(context, checkbox, layout);

        writer.endElement("table");
    }
    
    protected void encodeOptionInput(FacesContext context, SelectManyCheckbox checkbox, String id, String name, boolean checked, boolean disabled, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("value", value, null);

        renderOnchange(context, checkbox);
        
        if(checked) writer.writeAttribute("checked", "checked", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, checkbox);
        }

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeOptionLabel(FacesContext context, SelectManyCheckbox checkbox, String containerClientId, SelectItem option, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("label", null);
        writer.writeAttribute("for", containerClientId, null);
        if(disabled)
            writer.writeAttribute("class", "ui-state-disabled", null);
        
        if(option.isEscape())
            writer.writeText(option.getLabel(),null);
        else
            writer.write(option.getLabel());
        
        writer.endElement("label");
    }

    protected void encodeOptionOutput(FacesContext context, SelectManyCheckbox checkbox, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String boxClass = HTML.CHECKBOX_BOX_CLASS;
        boxClass = checked ? boxClass + " ui-state-active" : boxClass;
        boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
        boxClass = !checkbox.isValid() ? boxClass + " ui-state-error" : boxClass;
        
        String iconClass = checked ? HTML.CHECKBOX_CHECKED_ICON_CLASS : HTML.CHECKBOX_UNCHECKED_ICON_CLASS;
        
        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
    }
    
    protected void encodeSelectItems(FacesContext context, SelectManyCheckbox checkbox, String layout) throws IOException {
        if(layout.equals("lineDirection"))
            encodeLineLayout(context, checkbox);
        else if(layout.equals("pageDirection"))
            encodePageLayout(context, checkbox);
        else if(layout.equals("grid"))
            encodeGridLayout(context, checkbox);
        else 
            throw new FacesException("Invalid '" + layout + "' type for component '" + checkbox.getClientId(context) + "'.");  
    }
    
    protected void encodeLineLayout(FacesContext context, SelectManyCheckbox checkbox) throws IOException{
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, checkbox);
        Converter converter = checkbox.getConverter();
        Object values = getValues(checkbox);
        Object submittedValues = getSubmittedValues(checkbox);
        
        writer.startElement("tr", null);
        int idx = 0;
        for(SelectItem selectItem : selectItems) {
            encodeOption(context, checkbox, values, submittedValues, converter, selectItem, idx);
            idx++;
        }
        writer.endElement("tr");
    }
    
    protected void encodePageLayout(FacesContext context, SelectManyCheckbox checkbox) throws IOException{
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, checkbox);
        Converter converter = checkbox.getConverter();
        Object values = getValues(checkbox);
        Object submittedValues = getSubmittedValues(checkbox);
        
        int idx = 0;
        for(SelectItem selectItem : selectItems) {
            writer.startElement("tr", null);
            
            encodeOption(context, checkbox, values, submittedValues, converter, selectItem, idx);
            
            writer.endElement("tr");
            idx++;        
        }           
    }
    
    protected void encodeGridLayout(FacesContext context, SelectManyCheckbox checkbox) throws IOException{
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, checkbox);
        Converter converter = checkbox.getConverter();
        Object values = getValues(checkbox);
        Object submittedValues = getSubmittedValues(checkbox);
        int columns = checkbox.getColumns();
        
        if(columns != 0){
            int idx = 0, colMod;
            for(SelectItem selectItem : selectItems) {
                colMod = idx % columns;
                if(colMod == 0)
                    writer.startElement("tr", null);
            
                encodeOption(context, checkbox, values, submittedValues, converter, selectItem, idx);

                idx++;
                colMod = idx % columns;
                
                if(colMod == 0)
                    writer.endElement("tr");
            }
        }
        else {
            throw new FacesException("The value of columns attribute must be greater than zero.");
        }
    }
    
    protected void encodeCustomLayout(FacesContext context, SelectManyCheckbox checkbox) throws IOException{
        List<SelectItem> selectItems = getSelectItems(context, checkbox);
        Converter converter = checkbox.getConverter();
        Object values = getValues(checkbox);
        Object submittedValues = getSubmittedValues(checkbox);

        int idx = 0;
        for(SelectItem selectItem : selectItems) {
            String itemValueAsString = getOptionAsString(context, checkbox, converter, selectItem.getValue());
            String name = checkbox.getClientId(context);
            String id = name + UINamingContainer.getSeparatorChar(context) + idx;

            Object valuesArray;
            Object itemValue;
            if(submittedValues != null) {
                valuesArray = submittedValues;
                itemValue = itemValueAsString;
            } else {
                valuesArray = values;
                itemValue = selectItem.getValue();
            }

            boolean selected = isSelected(context, checkbox, itemValue, valuesArray, converter);
            if(selectItem.isNoSelectionOption() && values != null && !selected) {
                return;
            }
            
            encodeOptionInput(context, checkbox, id, name, selected, true, itemValueAsString);
            idx++;
        }
    }
    
    protected void encodeOption(FacesContext context, UIInput component, Object values, Object submittedValues, Converter converter, SelectItem option, int idx) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        SelectManyCheckbox checkbox = (SelectManyCheckbox) component;
        String itemValueAsString = getOptionAsString(context, component, converter, option.getValue());
        String name = checkbox.getClientId(context);
        String id = name + UINamingContainer.getSeparatorChar(context) + idx;
        boolean disabled = option.isDisabled() || checkbox.isDisabled();

        Object valuesArray;
        Object itemValue;
        if(submittedValues != null) {
            valuesArray = submittedValues;
            itemValue = itemValueAsString;
        } else {
            valuesArray = values;
            itemValue = option.getValue();
        }
        
        boolean selected = isSelected(context, component, itemValue, valuesArray, converter);
        if(option.isNoSelectionOption() && values != null && !selected) {
            return;
        }
        
        writer.startElement("td", null);
        
        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.CHECKBOX_CLASS, null);

        encodeOptionInput(context, checkbox, id, name, selected, disabled, itemValueAsString);
        encodeOptionOutput(context, checkbox, selected, disabled);

        writer.endElement("div");
        writer.endElement("td");

        writer.startElement("td", null);
        encodeOptionLabel(context, checkbox, id, option, disabled);
        writer.endElement("td");
    }
    
    @Override
    protected String getSubmitParam(FacesContext context, UISelectMany selectMany) {
        return selectMany.getClientId(context);
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
