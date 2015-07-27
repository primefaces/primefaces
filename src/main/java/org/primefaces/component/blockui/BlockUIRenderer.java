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
package org.primefaces.component.blockui;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class BlockUIRenderer extends CoreRenderer {
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		BlockUI blockUI = (BlockUI) component;
        
        encodeMarkup(context, component);
        encodeScript(context, blockUI);
	}
    
    protected void encodeScript(FacesContext context, BlockUI blockUI) throws IOException {
        String clientId = blockUI.getClientId(context);
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("BlockUI", blockUI.resolveWidgetVar(), clientId);
        
        wb.attr("block", SearchExpressionFacade.resolveClientIds(context, blockUI, blockUI.getBlock()));
        wb.attr("triggers", SearchExpressionFacade.resolveClientIds(context, blockUI, blockUI.getTrigger()), null);
        wb.attr("blocked", blockUI.isBlocked(), false);
        wb.attr("animate", blockUI.isAnimate(), true);
        wb.attr("styleClass", blockUI.getStyleClass(), null);
        
        wb.finish();
    }
    
    protected void encodeMarkup(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        BlockUI blockUI = (BlockUI) component;
        String clientId = blockUI.getClientId(context);
        
        writer.startElement("div", blockUI);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", "ui-blockui-content ui-widget ui-widget-content ui-corner-all ui-helper-hidden ui-shadow", null);
        
        renderChildren(context, blockUI);
        
        writer.endElement("div");
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
