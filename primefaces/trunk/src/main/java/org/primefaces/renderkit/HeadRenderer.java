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
 * - First Facet
 * - Theme CSS
 * - Registered Resources
 * - Head Content
 * - Last Facet
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
        
        //Registered Resources
        UIViewRoot viewRoot = context.getViewRoot();
        for (UIComponent resource : viewRoot.getComponentResources(context, "head")) {
            resource.encodeAll(context);
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
        } 
        else {
            writer.startElement("link", null);
            writer.writeAttribute("type", "text/css", null);
            writer.writeAttribute("rel", "stylesheet", null);
            writer.writeAttribute("href", themeResource.getRequestPath(), null);
            writer.endElement("link");
        }
    }
}
