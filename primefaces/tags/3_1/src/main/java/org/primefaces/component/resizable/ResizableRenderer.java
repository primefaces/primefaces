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

        startScript(writer, clientId);

        //If it is an image wait until the image is loaded
        if(target instanceof UIGraphic)
            writer.write("$(PrimeFaces.escapeClientId('" + targetId + "')).load(function(){");
        else
            writer.write("$(function(){");
		
        writer.write("PrimeFaces.cw('Resizable','" + resizable.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",target:'" + targetId + "'");

        //Boundaries
        if(resizable.getMinWidth() != Integer.MIN_VALUE) writer.write(",minWidth:" + resizable.getMinWidth());
        if(resizable.getMaxWidth() != Integer.MAX_VALUE) writer.write(",maxWidth:" + resizable.getMaxWidth());
        if(resizable.getMinHeight() != Integer.MIN_VALUE) writer.write(",minHeight:" + resizable.getMinHeight());
        if(resizable.getMaxHeight() != Integer.MAX_VALUE) writer.write(",maxHeight:" + resizable.getMaxHeight());

        //Animation
        if(resizable.isAnimate()) {
            writer.write(",animate:true");
            writer.write(",animateEasing:'" + resizable.getEffect() + "'");
            writer.write(",animateDuration:'" + resizable.getEffectDuration() + "'");
        }

        //Config
        if(resizable.isProxy()) writer.write(",helper:'ui-resizable-proxy'");
        if(resizable.getHandles() != null) writer.write(",handles:'" + resizable.getHandles() + "'");
        if(resizable.getGrid() != 1) writer.write(",grid:" + resizable.getGrid());
        if(resizable.isAspectRatio()) writer.write(",aspectRatio:true");
        if(resizable.isGhost()) writer.write(",ghost:true");
        if(resizable.isContainment()) writer.write(",containment:PrimeFaces.escapeClientId('" + resizable.getParent().getClientId(context) +"')");

        //Client side callbacks
        if(resizable.getOnStart() != null) writer.write(",onStart:function(event, ui) {" + resizable.getOnStart() + "}");
        if(resizable.getOnResize() != null) writer.write(",onResize:function(event, ui) {" + resizable.getOnResize() + "}");
        if(resizable.getOnStop() != null) writer.write(",onStop:function(event, ui) {" + resizable.getOnStop() + "}");

        //Behaviors
        encodeClientBehaviors(context, resizable);
		
		writer.write("});});");
		
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