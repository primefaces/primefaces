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

import org.primefaces.renderkit.CoreRenderer;

public class DroppableRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Droppable droppable = (Droppable) component;
        String target = findTarget(context, droppable).getClientId(context);
        String clientId = droppable.getClientId(context);

        startScript(writer, clientId);

        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('Droppable','" + droppable.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",target:'" + target + "'");

        if(droppable.isDisabled()) writer.write(",disabled:true");
        if(droppable.getHoverStyleClass() != null) writer.write(",hoverClass:'" + droppable.getHoverStyleClass() + "'");
        if(droppable.getActiveStyleClass() != null) writer.write(",activeClass:'" + droppable.getActiveStyleClass() + "'");
        if(droppable.getOnDrop() != null) writer.write(",onDrop:" + droppable.getOnDrop());
        if(droppable.getAccept() != null) writer.write(",accept:'" + droppable.getAccept() + "'");
        if(droppable.getScope() != null) writer.write(",scope:'" + droppable.getScope() + "'");
        if(droppable.getTolerance() != null) writer.write(",tolerance:'" + droppable.getTolerance() + "'");

        encodeClientBehaviors(context, droppable);
        
        writer.write("});});");

        endScript(writer);
    }

    protected UIComponent findTarget(FacesContext facesContext, Droppable droppable) {
        String _for = droppable.getFor();

        if(_for != null) {
            UIComponent component = droppable.findComponent(_for);
            if (component == null)
                throw new FacesException("Cannot find component \"" + _for + "\" in view.");
            else
                return component;
        } else {
            return droppable.getParent();
        }
    }
}
