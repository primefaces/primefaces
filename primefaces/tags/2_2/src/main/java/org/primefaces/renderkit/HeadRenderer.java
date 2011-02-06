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
package org.primefaces.renderkit;

import java.io.IOException;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;
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

public class HeadRenderer extends Renderer {

    private final static Logger logger = Logger.getLogger(HeadRenderer.class.getName());

	@Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("head", component);

        //Resources
        UIViewRoot viewRoot = context.getViewRoot();
        ListIterator<UIComponent> iter = (viewRoot.getComponentResources(context, "head")).listIterator();
        while (iter.hasNext()) {
            writer.write("\n");
            UIComponent resource = (UIComponent) iter.next();
            resource.encodeAll(context);
        }
        
        //Theme
        String theme = null;
        String oldestThemeParamValue = context.getExternalContext().getInitParameter("primefaces.skin");
        String oldThemeParamValue = context.getExternalContext().getInitParameter("primefaces.SKIN");
        String themeParamValue = context.getExternalContext().getInitParameter("primefaces.THEME");

        if(oldestThemeParamValue != null) {
            logger.info("primefaces.skin is deprecated, use primefaces.THEME instead.");

            theme = oldestThemeParamValue;
        }
        else if(oldThemeParamValue != null) {
            logger.info("primefaces.SKIN is deprecated, use primefaces.THEME instead.");
            
            theme = oldThemeParamValue;
        }
        else if(themeParamValue != null) {
            ELContext elContext = context.getELContext();
            ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
            ValueExpression ve = expressionFactory.createValueExpression(elContext, themeParamValue, String.class);

            theme = (String) ve.getValue(elContext);
        }

        if(theme == null || theme.equalsIgnoreCase("sam")) {
            encodeTheme(context, "primefaces", "themes/sam/theme.css");
        }
        else if(!theme.equalsIgnoreCase("none")) {
            encodeTheme(context, "primefaces-" + theme, "theme.css");
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

    private UIComponent createThemeResource(FacesContext fc, String library, String resourceName) {
        UIComponent resource = fc.getApplication().createComponent("javax.faces.Output");
        resource.setRendererType("javax.faces.resource.Stylesheet");
        
        Map<String, Object> attrs = resource.getAttributes();
        attrs.put("name", resourceName);
        attrs.put("library", library);
        attrs.put("target", "head");
       
        return resource;
    }

    protected void encodeTheme(FacesContext context, String library, String resource) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.write("\n");

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
