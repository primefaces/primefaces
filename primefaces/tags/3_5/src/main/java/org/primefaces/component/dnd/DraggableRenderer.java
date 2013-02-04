/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.dnd;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class DraggableRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Draggable draggable = (Draggable) component;
        String clientId = draggable.getClientId(context);
        String target = findTarget(context, draggable);
        String dashboard = draggable.getDashboard();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.widget("Draggable", draggable.resolveWidgetVar(), clientId, true)
                .attr("target", target)
                .attr("cursor", draggable.getCursor())
                .attr("disabled", draggable.isDisabled(), false)
                .attr("axis", draggable.getAxis(), null)
                .attr("containment", draggable.getContainment(), null)
                .attr("helper", draggable.getHelper(), null)
                .attr("zIndex", draggable.getZindex(), -1)
                .attr("handle", draggable.getHandle(), null)
                .attr("opacity", draggable.getOpacity(), 1.0)
                .attr("stack", draggable.getStack(), null)
                .attr("scope", draggable.getScope(), null);
        
        if(draggable.isRevert())
            wb.attr("revert", "invalid");
        
        if(draggable.getGrid() != null)
            wb.append(",grid:[").append(draggable.getGrid()).append("]");
        
        if(draggable.isSnap()) {
            wb.attr("snap", true)
                .attr("snapTolerance", draggable.getSnapTolerance())
                .attr("snapMode", draggable.getSnapMode(), null);
        }
        
        //Dashboard support
        if(dashboard != null) {
            Dashboard db = (Dashboard) draggable.findComponent(dashboard);
            if(db == null) {
                throw new FacesException("Cannot find dashboard \"" + dashboard + "\" in view");
            }
            
            String selector = ComponentUtils.escapeJQueryId(db.getClientId(context)) + " .ui-dashboard-column";
            wb.attr("connectToSortable", selector);
        }

        startScript(writer, clientId);
        writer.write(wb.build());
        endScript(writer);

    }

    protected String findTarget(FacesContext facesContext, Draggable draggable) {
        String _for = draggable.getFor();

        if(_for != null) {
            UIComponent component = draggable.findComponent(_for);
            if(component == null)
                throw new FacesException("Cannot find component \"" + _for + "\" in view.");
            else
                return component.getClientId(facesContext);
        } else {
            return draggable.getParent().getClientId(facesContext);
        }
    }
}
