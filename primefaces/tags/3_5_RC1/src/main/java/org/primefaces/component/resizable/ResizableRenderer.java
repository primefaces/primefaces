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
package org.primefaces.component.resizable;

import java.io.IOException;
import javax.faces.FacesException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIGraphic;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ResizableRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Resizable resizable = (Resizable) component;
        String clientId = resizable.getClientId(context);
		UIComponent target = findTarget(context, resizable);
        String targetId = target.getClientId(context);
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.widget("Resizable", resizable.resolveWidgetVar(), clientId, false)
            .attr("target", targetId)
            .attr("minWidth", resizable.getMinWidth(), Integer.MIN_VALUE)
            .attr("maxWidth", resizable.getMaxWidth(), Integer.MAX_VALUE)
            .attr("minHeight", resizable.getMinHeight(), Integer.MIN_VALUE)
            .attr("maxHeight", resizable.getMaxHeight(), Integer.MAX_VALUE);
        
        if(resizable.isAnimate()) {
            wb.attr("animate", true)
                .attr("animateEasing", resizable.getEffect())
                .attr("animateDuration", resizable.getEffectDuration());
        }
        
        if(resizable.isProxy()) {
            wb.attr("helper", "ui-resizable-proxy");
        }
        
        wb.attr("handles", resizable.getHandles(), null)
            .attr("grid", resizable.getGrid(), 1)
            .attr("aspectRatio", resizable.isAspectRatio(), false)
            .attr("ghost", resizable.isGhost(), false);
        
        if(resizable.isContainment()) {
            wb.attr("containment", "PrimeFaces.escapeClientId('" + resizable.getParent().getClientId(context) + "')");
        }
        
        wb.callback("onStart", "function(event,ui)", resizable.getOnStart())
            .callback("onResize", "function(event,ui)", resizable.getOnResize())
            .callback("onStop", "function(event,ui)", resizable.getOnStop());
            
        encodeClientBehaviors(context, resizable, wb);

        startScript(writer, clientId);

        if(target instanceof UIGraphic)
            writer.write("$(PrimeFaces.escapeClientId('" + targetId + "')).load(function(){");
        else
            writer.write("$(function(){");
        
        writer.write(wb.build());
        
        writer.write("});");
        endScript(writer);
	}

    protected UIComponent findTarget(FacesContext context, Resizable resizable) {
        String _for = resizable.getFor();

        if (_for != null) {
            UIComponent component = resizable.findComponent(_for);
            if (component == null)
                throw new FacesException("Cannot find component \"" + _for + "\" in view.");
            else
                return component;
        } else {
            return resizable.getParent();
        }
    }
}