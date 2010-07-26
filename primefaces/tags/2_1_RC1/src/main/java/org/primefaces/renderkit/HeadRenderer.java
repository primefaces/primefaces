/*
 * Copyright 2009 Prime Technology.
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
package org.primefaces.renderkit;

import java.io.IOException;
import java.util.ListIterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.primefaces.resource.ResourceUtils;

/**
 * Temporary Custom HeadRenderer to fix an issue with Mojarra-2.0.2 that causes queued resources to be rendered after
 * resources defined by page author making it hard to override default styles of components for skinning
 */
public class HeadRenderer extends Renderer {

	@Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("head", component);
        
        //Skin
        String skin = context.getExternalContext().getInitParameter("primefaces.skin");
        if(skin == null) {
        	skin = "sam";	//default
        }
        
        if(!skin.equalsIgnoreCase("none")) {
        	String path = "/skins/" + skin + "/skin.css";
        	writer.startElement("link", null);
    		writer.writeAttribute("rel", "stylesheet", null);
    		writer.writeAttribute("type", "text/css", null);
    		writer.writeAttribute("href", ResourceUtils.getResourceURL(context, path), null);
    		writer.endElement("link");
        }
        
        //Resources
        UIViewRoot viewRoot = context.getViewRoot();
        ListIterator<UIComponent> iter = (viewRoot.getComponentResources(context, "head")).listIterator();
        while (iter.hasNext()) {
            UIComponent resource = (UIComponent)iter.next();
            resource.encodeAll(context);
        }
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //no-op
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
       
        writer.endElement("head");
    }
}
