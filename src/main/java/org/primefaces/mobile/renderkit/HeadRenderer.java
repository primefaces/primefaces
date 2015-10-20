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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.ProjectStage;
import javax.faces.application.Resource;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.primefaces.config.ConfigContainer;
import org.primefaces.context.RequestContext;

public class HeadRenderer extends Renderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ConfigContainer cc = RequestContext.getCurrentInstance().getApplicationContext().getConfig();
        ProjectStage projectStage = context.getApplication().getProjectStage();
        writer.startElement("head", component);

        //First facet
        UIComponent first = component.getFacet("first");
        if(first != null) {
            first.encodeAll(context);
        }

        writer.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>");

        String theme = resolveTheme(context);
        if(theme == null) {
            renderCSS(context, "mobile/jquery-mobile.css", "primefaces");
        }
        else {
            renderCSS(context, "theme.css", "primefaces-" + theme);
            renderCSS(context, "mobile/jquery-mobile-icons.css", "primefaces");
            renderCSS(context, "mobile/jquery-mobile-structure.css", "primefaces");
        }

        renderCSS(context, "mobile/primefaces-mobile.css", "primefaces");

        if(cc.isFontAwesomeEnabled()) {
            renderCSS(context, "fa/font-awesome.css", "primefaces");
        }

        renderJS(context, "jquery/jquery.js", "primefaces");

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write("$(document).on('mobileinit', function(){");
        writer.write("$.mobile.ajaxEnabled = false;");
        writer.write("$.mobile.pushStateEnabled = false;");
        writer.write("$.mobile.page.prototype.options.domCache = true;");

        UIComponent init = component.getFacet("init");
        if(init != null) {
            init.encodeAll(context);
        }

        writer.write("});");
        writer.endElement("script");

        renderJS(context, "mobile/jquery-mobile.js", "primefaces");
        renderJS(context, "primefaces-mobile.js", "primefaces");

        //Registered Resources
        UIViewRoot viewRoot = context.getViewRoot();
        for(UIComponent resource : viewRoot.getComponentResources(context, "head")) {
            boolean shouldRender = true;
            Map<String,Object> attrs = resource.getAttributes();
            String library = (String) attrs.get("library");

            if(library != null && library.equals("primefaces")) {
                String resourceName = (String) attrs.get("name");
                if(resourceName.startsWith("jquery")||resourceName.startsWith("primefaces")) {
                    shouldRender = false;
                }
            }

            if(shouldRender) {
                resource.encodeAll(context);
            }
        }

        if (!projectStage.equals(ProjectStage.Production) || cc.isLegacyWidgetNamespace()) {
            writer.startElement("script", null);
            writer.writeAttribute("type", "text/javascript", null);
            writer.write("if(window.PrimeFaces){");

            if(cc.isLegacyWidgetNamespace()) {
                writer.write("PrimeFaces.settings.legacyWidgetNamespace = true;");
            }

            if (!projectStage.equals(ProjectStage.Production)) {
                writer.write("PrimeFaces.settings.projectStage='" + projectStage.toString() + "';");
            }

            writer.write("}");
            writer.endElement("script");
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

    protected String resolveTheme(FacesContext context) {
        String theme = null;
        String themeConfigValue = RequestContext.getCurrentInstance().getApplicationContext().getConfig().getMobileTheme();

        if(themeConfigValue != null) {
            ELContext elContext = context.getELContext();
            ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
            ValueExpression ve = expressionFactory.createValueExpression(elContext, themeConfigValue, String.class);

            theme = ve.isLiteralText() ? themeConfigValue: (String) ve.getValue(elContext);
        }

        return theme;
    }

    private void renderJS(FacesContext context, String name, String library) throws IOException  {
        ResponseWriter writer = context.getResponseWriter();
        Resource resource = context.getApplication().getResourceHandler().createResource(name, library);

        if(resource != null) {
            writer.startElement("script", null);
            writer.writeAttribute("type", "text/javascript", null);
            writer.writeAttribute("src", resource.getRequestPath(), null);
            writer.endElement("script");
        }
    }

    private void renderCSS(FacesContext context, String name, String library) throws IOException  {
        ResponseWriter writer = context.getResponseWriter();
        Resource resource = context.getApplication().getResourceHandler().createResource(name, library);

        if(resource != null) {
            writer.startElement("link", null);
            writer.writeAttribute("type", "text/css", null);
            writer.writeAttribute("rel", "stylesheet", null);
            writer.writeAttribute("href", resource.getRequestPath(), null);
            writer.endElement("link");
        }
    }
}
