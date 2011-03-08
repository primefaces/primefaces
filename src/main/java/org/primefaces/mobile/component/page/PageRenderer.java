/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.mobile.component.page;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.mobile.util.MobileUtils;
import org.primefaces.renderkit.CoreRenderer;

public class PageRenderer extends CoreRenderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        MobileUtils.setMobileRequest();
        
        ResponseWriter writer = context.getResponseWriter();
        Page page = (Page) component;
        UIComponent meta = page.getFacet("meta");

        writer.write("<!DOCTYPE html>\n");
        writer.startElement("html", page);
        writer.startElement("head", page);

        writer.startElement("title", page);
        writer.write(page.getTitle());
        writer.endElement("title");

        renderResource(context, "mobile/mobile.css", "javax.faces.resource.Stylesheet");
        renderResource(context, "jquery/jquery.js", "javax.faces.resource.Script");

        if(meta != null) {
            meta.encodeAll(context);
        }
        
        renderResource(context, "mobile/mobile.js", "javax.faces.resource.Script");
        renderResource(context, "core/core.js", "javax.faces.resource.Script");
        renderResource(context, "mobile/core.js", "javax.faces.resource.Script");

        writer.endElement("head");

        writer.startElement("body", page);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.endElement("body");
        writer.endElement("html");

    }

    protected void renderResource(FacesContext context, String resourceName, String renderer) throws IOException {
        UIComponent resource = context.getApplication().createComponent("javax.faces.Output");
        resource.setRendererType(renderer);

        Map<String, Object> attrs = resource.getAttributes();
        attrs.put("name", resourceName);
        attrs.put("library", "primefaces");
        attrs.put("target", "head");

        resource.encodeAll(context);
    }
}
