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
package org.primefaces.renderkit;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.primefaces.context.RequestContext;

public class BodyRenderer extends Renderer {
    
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("body", component);
    }
    
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        encodeOnloadScripts(context);
        writer.endElement("body");
    }
    
    protected void encodeOnloadScripts(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        RequestContext requestContext = RequestContext.getCurrentInstance();
        List<String> scripts = requestContext.getScriptsToExecute();
        
        if(!scripts.isEmpty()) {
            writer.startElement("script", null);
            writer.writeAttribute("type", "text/javascript", null);
            
            writer.write("$(function(){");

            for(int i = 0; i < scripts.size(); i++) {
                writer.write(scripts.get(i));
                writer.write(';');
            }

            writer.write("});");
            writer.endElement("script");
        }
    }
}
