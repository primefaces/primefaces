/*
 * Copyright 2010 Prime Technology.
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
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

public class HeadRenderer extends Renderer {

    private final static Logger logger = Logger.getLogger(HeadRenderer.class.getName());

	@Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("head", component);
        
        //Skin
        String newParam = context.getExternalContext().getInitParameter("primefaces.SKIN");
        String oldParam = context.getExternalContext().getInitParameter("primefaces.skin");

        if(oldParam != null) {
            logger.info("primefaces.skin context-param is deprecated use primefaces.SKIN instead");
        }

        //Add default skin resource if user does not provide custom skin
        if(newParam == null && oldParam == null) {
            context.getViewRoot().addComponentResource(context, createDefaultSkinResource(context), "head");
        }

        //Resources
        UIViewRoot viewRoot = context.getViewRoot();
        ListIterator<UIComponent> iter = (viewRoot.getComponentResources(context, "head")).listIterator();
        while (iter.hasNext()) {
            UIComponent resource = (UIComponent) iter.next();
            resource.encodeAll(context);
            writer.write("\n");
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

    private UIComponent createDefaultSkinResource(FacesContext fc) {
        UIComponent resource = fc.getApplication().createComponent("javax.faces.Output");
        resource.setRendererType("javax.faces.resource.Stylesheet");
        
        Map<String, Object> attrs = resource.getAttributes();
        attrs.put("name", "skins/sam/skin.css");
        attrs.put("library", "primefaces");
        attrs.put("target", "head");
       
        return resource;
    }
}
