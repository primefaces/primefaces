/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.renderkit;

import org.primefaces.clientwindow.PrimeClientWindow;
import org.primefaces.clientwindow.PrimeClientWindowUtils;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.ResourceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.lifecycle.ClientWindow;
import javax.faces.render.Renderer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Renders head content based on the following order
 * - First Facet
 * - Theme CSS
 * - PrimeIcons CSS
 * - Middle Facet
 * - Registered Resources
 * - Client Validation Scripts
 * - Locales
 * - PF Client Side Settings
 * - PF Initialization Scripts
 * - Head Content
 * - Last Facet
 */
public class HeadRenderer extends Renderer {

    private static final Logger LOGGER = Logger.getLogger(HeadRenderer.class.getName());


    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(context);
        PrimeApplicationContext applicationContext = requestContext.getApplicationContext();

        writer.startElement("head", component);
        writer.writeAttribute("id", component.getClientId(context), "id");

        //First facet
        UIComponent first = component.getFacet("first");
        if (FacetUtils.shouldRenderFacet(first)) {
            first.encodeAll(context);
        }

        //Middle facet
        UIComponent middle = component.getFacet("middle");
        if (FacetUtils.shouldRenderFacet(middle)) {
            middle.encodeAll(context);
        }

        if (applicationContext.getConfig().isClientSideValidationEnabled()) {
            // moment is needed for Date validation
            ResourceUtils.addJavascriptResource(context,  "moment/moment.js");

            // BV CSV is optional and must be enabled by config
            if (applicationContext.getConfig().isBeanValidationEnabled()) {
                ResourceUtils.addJavascriptResource(context, "validation/validation.bv.js");
            }
        }

        if (applicationContext.getConfig().isClientSideLocalizationEnabled()) {
            try {
                Locale locale = LocaleUtils.getCurrentLocale(context);
                ResourceUtils.addJavascriptResource(context, "locales/locale-" + locale.getLanguage() + ".js");
            }
            catch (FacesException e) {
                if (context.isProjectStage(ProjectStage.Development)) {
                    LOGGER.log(Level.WARNING,
                                "Failed to load client side locale.js. {0}", e.getMessage());
                }
            }
        }

        //Registered Resources
        UIViewRoot viewRoot = context.getViewRoot();
        List<UIComponent> resources = new ArrayList<>(viewRoot.getComponentResources(context, "head"));
        for (int i = 0; i < resources.size(); i++) {
            UIComponent resource = resources.get(i);
            LOGGER.log(Level.FINE, () -> "HeadRenderer resource: " + resource.getAttributes().get("name"));
            resource.encodeAll(context);
        }

        encodeSettingScripts(context, applicationContext, requestContext, writer);

        // encode initialization scripts
        encodeInitScripts(context, writer);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        //Last facet
        UIComponent last = component.getFacet("last");
        if (FacetUtils.shouldRenderFacet(last)) {
            last.encodeAll(context);
        }

        writer.endElement("head");
    }

    protected void encodeSettingScripts(FacesContext context, PrimeApplicationContext applicationContext, PrimeRequestContext requestContext,
            ResponseWriter writer) throws IOException {

        ProjectStage projectStage = context.getApplication().getProjectStage();

        writer.startElement("script", null);
        RendererUtils.encodeScriptTypeIfNecessary(context);
        writer.write("if(window.PrimeFaces){");

        writer.write("PrimeFaces.settings.locale='" + LocaleUtils.getCurrentLocale(context) + "';");
        writer.write("PrimeFaces.settings.viewId='" + context.getViewRoot().getViewId() + "';");
        writer.write("PrimeFaces.settings.contextPath='" + context.getExternalContext().getRequestContextPath() + "';");

        writer.write("PrimeFaces.settings.cookiesSecure=" + (requestContext.isSecure() && applicationContext.getConfig().isCookiesSecure()) + ";");
        if (applicationContext.getConfig().getCookiesSameSite() != null) {
            writer.write("PrimeFaces.settings.cookiesSameSite='" + applicationContext.getConfig().getCookiesSameSite() + "';");
        }

        writer.write("PrimeFaces.settings.validateEmptyFields=" + applicationContext.getConfig().isValidateEmptyFields() + ";");
        writer.write("PrimeFaces.settings.considerEmptyStringNull=" + applicationContext.getConfig().isInterpretEmptyStringAsNull() + ";");

        if (applicationContext.getConfig().isEarlyPostParamEvaluation()) {
            writer.write("PrimeFaces.settings.earlyPostParamEvaluation=true;");
        }

        if (applicationContext.getConfig().isPartialSubmitEnabled()) {
            writer.write("PrimeFaces.settings.partialSubmit=true;");
        }

        if (projectStage != ProjectStage.Production) {
            writer.write("PrimeFaces.settings.projectStage='" + projectStage.toString() + "';");
        }

        if (context.getExternalContext().getClientWindow() != null) {
            ClientWindow clientWindow = context.getExternalContext().getClientWindow();
            if (clientWindow instanceof PrimeClientWindow) {

                boolean initialRedirect = false;

                Object cookie = PrimeClientWindowUtils.getInitialRedirectCookie(context, clientWindow.getId());
                if (cookie instanceof Cookie) {
                    Cookie servletCookie = (Cookie) cookie;
                    initialRedirect = true;

                    // expire/remove cookie
                    servletCookie.setMaxAge(0);
                    ((HttpServletResponse) context.getExternalContext().getResponse()).addCookie(servletCookie);
                }
                writer.write(
                        String.format("PrimeFaces.clientwindow.init('%s', %s);",
                                PrimeClientWindowUtils.secureWindowId(clientWindow.getId()),
                                initialRedirect));
            }
        }

        writer.write("}");
        writer.endElement("script");
    }

    protected void encodeInitScripts(FacesContext context, ResponseWriter writer) throws IOException {
        List<String> scripts = PrimeRequestContext.getCurrentInstance().getInitScriptsToExecute();

        if (!scripts.isEmpty()) {
            writer.startElement("script", null);
            RendererUtils.encodeScriptTypeIfNecessary(context);

            boolean moveScriptsToBottom = PrimeRequestContext.getCurrentInstance().getApplicationContext().getConfig().isMoveScriptsToBottom();

            if (!moveScriptsToBottom) {
                writer.write("(function(){const pfInit=() => {");

                for (int i = 0; i < scripts.size(); i++) {
                    writer.write(scripts.get(i));
                    writer.write(';');
                }

                writer.write("};if(window.$){$(function(){pfInit()})}");
                writer.write("else if(document.readyState==='complete'){pfInit()}");
                writer.write("else{document.addEventListener('DOMContentLoaded', pfInit)}})();");
            }
            else {
                for (int i = 0; i < scripts.size(); i++) {
                    writer.write(scripts.get(i));
                    writer.write(';');
                }
            }

            writer.endElement("script");
        }
    }
}
