/*
 * Copyright 2009-2014 PrimeTek.
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
import javax.faces.application.ProjectStage;
import javax.faces.application.Resource;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.primefaces.config.ConfigContainer;

import org.primefaces.context.RequestContext;

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
        ConfigContainer cc = RequestContext.getCurrentInstance().getApplicationContext().getConfig();
        ProjectStage projectStage = context.getApplication().getProjectStage();
        boolean csvEnabled = cc.isClientSideValidationEnabled();
                
        writer.startElement("head", component);
        
        //First facet
        UIComponent first = component.getFacet("first");
        if (first != null) {
            first.encodeAll(context);
        }
        
        //Theme
        String theme;
        String themeParamValue = RequestContext.getCurrentInstance().getApplicationContext().getConfig().getTheme();

        if (themeParamValue != null) {
            ELContext elContext = context.getELContext();
            ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
            ValueExpression ve = expressionFactory.createValueExpression(elContext, themeParamValue, String.class);

            theme = (String) ve.getValue(elContext);
        } 
        else {
            theme = "aristo";   //default
        }

        if (theme != null && !theme.equals("none")) {
            encodeCSS(context, "primefaces-" + theme, "theme.css");
        }
        
        if (cc.isFontAwesomeEnabled()) {
            encodeCSS(context, "primefaces", "fa/font-awesome.css");
        }
        
        //Middle facet
        UIComponent middle = component.getFacet("middle");
        if (middle != null) {
            middle.encodeAll(context);
        }
                
        //Registered Resources
        UIViewRoot viewRoot = context.getViewRoot();
        for (UIComponent resource : viewRoot.getComponentResources(context, "head")) {
            resource.encodeAll(context);
        }
        
        if(csvEnabled) {
            encodeValidationResources(context, cc.isBeanValidationAvailable());
        }

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write("if(window.PrimeFaces){");

        if (csvEnabled) {
            writer.write("PrimeFaces.settings.locale='" + context.getViewRoot().getLocale() + "';");
            writer.write("PrimeFaces.settings.validateEmptyFields=" + cc.isValidateEmptyFields() + ";");
            writer.write("PrimeFaces.settings.considerEmptyStringNull=" + cc.isInterpretEmptyStringAsNull() + ";");
        }
        
        if (cc.isLegacyWidgetNamespace()) {
            writer.write("PrimeFaces.settings.legacyWidgetNamespace=true;");
        }

        if (!projectStage.equals(ProjectStage.Production)) {
            writer.write("PrimeFaces.settings.projectStage='" + projectStage.toString() + "';");
        }

        writer.write("}");
        writer.endElement("script");
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        //Last facet
        UIComponent last = component.getFacet("last");
        if (last != null) {
            last.encodeAll(context);
        }
        
        writer.endElement("head");
    }

    protected void encodeCSS(FacesContext context, String library, String resource) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        Resource cssResource = context.getApplication().getResourceHandler().createResource(resource, library);
        if (cssResource == null) {
            throw new FacesException("Error loading css, cannot find \"" + resource + "\" resource of \"" + library + "\" library");
        }
        else {
            writer.startElement("link", null);
            writer.writeAttribute("type", "text/css", null);
            writer.writeAttribute("rel", "stylesheet", null);
            writer.writeAttribute("href", cssResource.getRequestPath(), null);
            writer.endElement("link");
        }
    }
    
    protected void encodeValidationResources(FacesContext context, boolean beanValidationEnabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Resource resource = context.getApplication().getResourceHandler().createResource("validation/validation.js", "primefaces");
        
        if(resource != null) {
            writer.startElement("script", null);
            writer.writeAttribute("type", "text/javascript", null);
            writer.writeAttribute("src", resource.getRequestPath(), null);
            writer.endElement("script");
        }
        
        if(beanValidationEnabled) {
            resource = context.getApplication().getResourceHandler().createResource("validation/beanvalidation.js", "primefaces");
        
            if(resource != null) {
                writer.startElement("script", null);
                writer.writeAttribute("type", "text/javascript", null);
                writer.writeAttribute("src", resource.getRequestPath(), null);
                writer.endElement("script");
            }
        }
    }
}
