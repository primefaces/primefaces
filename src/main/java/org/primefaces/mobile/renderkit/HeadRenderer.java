/*
 * Copyright 2009-2013 PrimeTek.
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
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

public class HeadRenderer extends Renderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("head", component);
        
        //First facet
        UIComponent first = component.getFacet("first");
        if(first != null) {
            first.encodeAll(context);
        }
        
        renderResource(context, "mobile/jquery-mobile.css", "javax.faces.resource.Stylesheet", "primefaces");
        renderResource(context, "jquery/jquery.js", "javax.faces.resource.Script", "primefaces");
        
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write("$(document).bind('mobileinit', function(){");
        writer.write("$.mobile.ajaxEnabled = false;");               
        writer.write("$.mobile.pushStateEnabled = false;");        
        writer.write("$.mobile.page.prototype.options.domCache = true;");        
        writer.write("});");        
        writer.endElement("script");
        
        renderResource(context, "mobile/jquery-mobile.js", "javax.faces.resource.Script", "primefaces");
        renderResource(context, "primefaces-mobile.js", "javax.faces.resource.Script", "primefaces");
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        //Last facet
        UIComponent last = component.getFacet("last");
        if(last != null) {
            last.encodeAll(context);
        }
        
        writer.endElement("head");
    }

    protected void renderResource(FacesContext context, String resourceName, String renderer, String library) throws IOException {
        UIComponent resource = context.getApplication().createComponent("javax.faces.Output");
        resource.setRendererType(renderer);

        Map<String, Object> attrs = resource.getAttributes();
        attrs.put("name", resourceName);
        attrs.put("library", library);
        attrs.put("target", "head");        

        resource.encodeAll(context);
    } 
}
