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
package org.primefaces.component.tooltip;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class TooltipRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Tooltip tooltip = (Tooltip) component;
        String target = SearchExpressionFacade.resolveClientIds(
        				context, component, tooltip.getFor());
		
        encodeMarkup(context, tooltip, target);
		encodeScript(context, tooltip, target);
	}
    
    protected void encodeMarkup(FacesContext context, Tooltip tooltip, String target) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        if(target != null) {
            String styleClass = tooltip.getStyleClass();
            styleClass = styleClass == null ? Tooltip.CONTAINER_CLASS : Tooltip.CONTAINER_CLASS + " " + styleClass;

            writer.startElement("div", tooltip);
            writer.writeAttribute("id", tooltip.getClientId(context), null);
            writer.writeAttribute("class", styleClass, "styleClass");

            if(tooltip.getStyle() != null) 
                writer.writeAttribute("style", tooltip.getStyle(), "style");

            if(tooltip.getChildCount() > 0) {
                renderChildren(context, tooltip);
            }
            else {
                String valueToRender = ComponentUtils.getValueToRender(context, tooltip);
                if(valueToRender != null) {
                    if(tooltip.isEscape())
                        writer.writeText(valueToRender, "value");
                    else
                        writer.write(valueToRender);
                }
            }


            writer.endElement("div");
        }
    }

	protected void encodeScript(FacesContext context, Tooltip tooltip, String target) throws IOException {
        String clientId = tooltip.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Tooltip", tooltip.resolveWidgetVar(), clientId)            
            .attr("showEvent", tooltip.getShowEvent(), null)
            .attr("hideEvent", tooltip.getHideEvent(), null)
            .attr("showEffect", tooltip.getShowEffect(), null)
            .attr("hideEffect", tooltip.getHideEffect(), null)
            .attr("showDelay", tooltip.getShowDelay(), 150)
            .attr("hideDelay", tooltip.getHideDelay(), 0)
            .attr("target", target, null)
            .attr("globalSelector", tooltip.getGlobalSelector(), null)
            .attr("escape", tooltip.isEscape(), true)
            .attr("trackMouse", tooltip.isTrackMouse(), false)
            .callback("beforeShow", "function()", tooltip.getBeforeShow())
            .callback("onShow", "function()", tooltip.getOnShow())
            .callback("onHide", "function()", tooltip.getOnHide());
        
		wb.finish();
	}

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}