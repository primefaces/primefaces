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
package org.primefaces.renderkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Resource;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.primefaces.util.Constants;

/**
 * Renders head content based on the following order
 * - first facet if defined
 * - Theme CSS
 * - JSF-PF CSS resources
 * - middle facet if defined
 * - JSF-PF JS resources
 * - h:head content (encoded by super class at encodeChildren)
 * - last facet if defined
 */
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

        //Registered Resources
        UIViewRoot viewRoot = context.getViewRoot();
        ListIterator<UIComponent> iter = (viewRoot.getComponentResources(context, "head")).listIterator();
        List<UIComponent> styles = new ArrayList<UIComponent>();
        List<UIComponent> scripts = new ArrayList<UIComponent>();
        
        while(iter.hasNext()) {
            UIComponent resource = (UIComponent) iter.next();
            String name = (String) resource.getAttributes().get("name");

            if(name != null) {
                if(name.endsWith(".css"))
                    styles.add(resource);
                else if(name.endsWith(".js"));
                    scripts.add(resource);
            }
        }
        
        //Theme
        String theme = null;
        String themeParamValue = context.getExternalContext().getInitParameter(Constants.THEME_PARAM);

        if(themeParamValue != null) {
            ELContext elContext = context.getELContext();
            ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
            ValueExpression ve = expressionFactory.createValueExpression(elContext, themeParamValue, String.class);

            theme = (String) ve.getValue(elContext);
        } 
        else {
            theme = "aristo";   //default
        }

        if(theme != null && !theme.equals("none")) {
            encodeTheme(context, "primefaces-" + theme, "theme.css");
        }
        
        //Styles
        for(UIComponent style : styles) {
            style.encodeAll(context);
        }

        //Middle facet
        UIComponent middle = component.getFacet("middle");
        if(middle != null) {
            middle.encodeAll(context);
        }
        
        //Scripts
        for(UIComponent script : scripts) {
            script.encodeAll(context);
        }
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

    protected void encodeTheme(FacesContext context, String library, String resource) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        Resource themeResource = context.getApplication().getResourceHandler().createResource(resource, library);
        if(themeResource == null) {
            throw new FacesException("Error loading theme, cannot find \"" + resource + "\" resource of \"" + library + "\" library");
        } else {
            writer.startElement("link", null);
            writer.writeAttribute("type", "text/css", null);
            writer.writeAttribute("rel", "stylesheet", null);
            writer.writeAttribute("href", themeResource.getRequestPath(), null);
            writer.endElement("link");
        }
    }
}
