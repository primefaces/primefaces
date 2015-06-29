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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.overlaypanel.OverlayPanel;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.WidgetBuilder;

public class OverlayPanelRenderer extends org.primefaces.component.overlaypanel.OverlayPanelRenderer {
    
    @Override
    protected void encodeMarkup(FacesContext context, OverlayPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);
        String style = panel.getStyle();
        String styleClass = panel.getStyleClass();
        
        writer.startElement("div", panel);
        writer.writeAttribute("id", clientId, "id");
        if(style != null) writer.writeAttribute("style", style, "style");
        if(styleClass != null) writer.writeAttribute("class", style, "styleClass");
        
        if(!panel.isDynamic()) {
            renderChildren(context, panel);
        }
        
        renderDynamicPassThruAttributes(context, panel);
        
        writer.endElement("div");
    }
    
    @Override
    protected void encodeScript(FacesContext context, OverlayPanel panel) throws IOException {
        UIComponent target = SearchExpressionFacade.resolveComponent(context, panel, panel.getFor());
        String targetClientId = (target == null) ? null: target.getClientId(context);
        String clientId = panel.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("OverlayPanel", panel.resolveWidgetVar(), clientId)
            .attr("targetId", targetClientId, null)
            .attr("showEvent", panel.getShowEvent(), null)
            .attr("hideEvent", panel.getHideEvent(), null)
            .attr("showEffect", panel.getShowEffect(), null)
            .callback("onShow", "function()", panel.getOnShow())
            .callback("onHide", "function()", panel.getOnHide())
            .attr("at", panel.getAt(), null)
            .attr("dynamic", panel.isDynamic(), false)
            .attr("dismissable", panel.isDismissable(), true);

        wb.finish();
    }
}
