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
package org.primefaces.component.contextmenu;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;

import org.primefaces.component.tieredmenu.TieredMenuRenderer;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.WidgetBuilder;

public class ContextMenuRenderer extends TieredMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ContextMenu menu = (ContextMenu) abstractMenu;
		String clientId = menu.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("ContextMenu", menu.resolveWidgetVar(), clientId);
        
        String _for = menu.getFor();
        if(_for != null) {
        	UIComponent target = SearchExpressionFacade.resolveComponent(context, menu, _for);
        	
            wb.attr("target", target.getClientId(context))
                .attr("type", target.getClass().getSimpleName());
            
            if(target instanceof Widget) {
                wb.attr("targetWidgetVar", ((Widget) target).resolveWidgetVar());
            }
        }
        
        wb.attr("nodeType", menu.getNodeType(), null)
            .attr("event", menu.getEvent(), null)
            .attr("selectionMode", menu.getSelectionMode(), "multiple")
            .callback("beforeShow", "function(event)", menu.getBeforeShow())
            .attr("targetFilter", menu.getTargetFilter(), null);

        wb.finish();
	}
	
    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException{
        ContextMenu menu = (ContextMenu) abstractMenu;
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? ContextMenu.CONTAINER_CLASS : ContextMenu.CONTAINER_CLASS + " " + styleClass;
        
        encodeMenu(context, menu, style, styleClass, "menu");
	}
}