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
package org.primefaces.component.selectbooleanbutton;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class SelectBooleanButtonRenderer extends InputRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		SelectBooleanButton button = (SelectBooleanButton) component;

        if (button.isDisabled()) {
            return;
        }

        decodeBehaviors(context, button);

		String clientId = button.getClientId(context);
		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId + "_input");

        if (submittedValue != null && submittedValue.equalsIgnoreCase("on")) {
            button.setSubmittedValue(true);
        }
        else {
            button.setSubmittedValue(false);
        }
	}
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectBooleanButton button = (SelectBooleanButton) component;

        encodeMarkup(context, button);
        encodeScript(context, button);
    }

    protected void encodeMarkup(FacesContext context, SelectBooleanButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);
        boolean checked = Boolean.valueOf(ComponentUtils.getValueToRender(context, button));
        boolean disabled = button.isDisabled();
        String inputId = clientId + "_input";
        String label = checked ? button.getOnLabel() : button.getOffLabel();
        String icon = checked ? button.getOnIcon() : button.getOffIcon();
        String title = button.getTitle();
        String style = button.getStyle();
        
        //button        
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("type", "button", null);
		writer.writeAttribute("class", button.resolveStyleClass(checked, disabled), null);
        if (disabled) writer.writeAttribute("disabled", "disabled", null);
        if (title != null) writer.writeAttribute("title", title, null);
        if (style != null) writer.writeAttribute("style", style, "style");
        
        
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        
        //input
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);
        
        if (checked) writer.writeAttribute("checked", "checked", null);
        if (disabled) writer.writeAttribute("disabled", "disabled", null);
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, button);
        }
        
        renderOnchange(context, button);
        renderDomEvents(context, button, HTML.BLUR_FOCUS_EVENTS);

        // tabindex
        if (button.getTabindex() != null) {
            writer.writeAttribute("tabindex", button.getTabindex(), null);
        }
        
        writer.endElement("input");
        
        writer.endElement("div");
        
        //icon
        if (icon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + icon, null);
            writer.endElement("span");
        }
        
        //label
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.writeText(label, "value");
        writer.endElement("span");
        
        writer.endElement("div");
    }
    
    protected void encodeScript(FacesContext context, SelectBooleanButton button) throws IOException {
        String clientId = button.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectBooleanButton", button.resolveWidgetVar(), clientId)
            .attr("onLabel", escapeText(button.getOnLabel()))
            .attr("offLabel", escapeText(button.getOffLabel()))
            .attr("onIcon", button.getOnIcon(), null)
            .attr("offIcon", button.getOffIcon(), null);
        
        wb.finish();
    }
    
    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return ((submittedValue instanceof Boolean) ? submittedValue : Boolean.valueOf(submittedValue.toString()));
    }

    @Override
    protected String getHighlighter() {
        return "booleanbutton";
    }
}
