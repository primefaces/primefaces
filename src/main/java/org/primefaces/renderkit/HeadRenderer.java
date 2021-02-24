/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.application.Resource;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.lifecycle.ClientWindow;
import javax.faces.render.Renderer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.clientwindow.PrimeClientWindowUtils;
import org.primefaces.clientwindow.PrimeClientWindow;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LocaleUtils;

/**
 * Renders head content based on the following order
 * - First Facet
 * - Theme CSS
 * - FontAwesome
 * - Middle Facet
 * - Registered Resources
 * - Client Validation Scripts
 * - PF Client Side Settings
 * - PF Initialization Scripts
 * - Head Content
 * - Last Facet
 */
public class HeadRenderer extends Renderer {

    private static final String LIBRARY = "primefaces";

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(context);
        PrimeApplicationContext applicationContext = requestContext.getApplicationContext();
        boolean csvEnabled = applicationContext.getConfig().isClientSideValidationEnabled();

        writer.startElement("head", component);
        writer.writeAttribute("id", component.getClientId(context), "id");

        //First facet
        UIComponent first = component.getFacet("first");
        if (ComponentUtils.shouldRenderFacet(first)) {
            first.encodeAll(context);
        }

        //Theme
        String theme;
        String themeParamValue = applicationContext.getConfig().getTheme();

        if (themeParamValue != null) {
            ELContext elContext = context.getELContext();
            ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
            ValueExpression ve = expressionFactory.createValueExpression(elContext, themeParamValue, String.class);

            theme = (String) ve.getValue(elContext);
        }
        else {
            theme = "saga";     //default
        }

        if (theme != null && !"none".equals(theme)) {
            encodeCSS(context, LIBRARY + "-" + theme, "theme.css");
        }

        //Icons
        if (applicationContext.getConfig().isPrimeIconsEnabled()) {
            encodeCSS(context, LIBRARY, "primeicons/primeicons.css");
        }

        if (applicationContext.getConfig().isFontAwesomeEnabled()) {
            encodeCSS(context, LIBRARY, "fa/font-awesome.css");
        }

        //Middle facet
        UIComponent middle = component.getFacet("middle");
        if (ComponentUtils.shouldRenderFacet(middle)) {
            middle.encodeAll(context);
        }

        //Registered Resources
        UIViewRoot viewRoot = context.getViewRoot();
        List<UIComponent> resources = viewRoot.getComponentResources(context, "head");
        for (int i = 0; i < resources.size(); i++) {
            UIComponent resource = resources.get(i);
            resource.encodeAll(context);
        }

        if (csvEnabled) {
            encodeValidationResources(context, applicationContext.getConfig().isBeanValidationEnabled());
        }

        if (applicationContext.getConfig().isClientSideLocalizationEnabled()) {
            Locale locale = LocaleUtils.getCurrentLocale(context);
            encodeJS(context, LIBRARY, "locales/locale-" + locale.getLanguage() + ".js");
        }

        encodeSettingScripts(context, applicationContext, requestContext, writer, csvEnabled);

        // encode initialization scripts
        encodeInitScripts(writer);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        //Last facet
        UIComponent last = component.getFacet("last");
        if (ComponentUtils.shouldRenderFacet(last)) {
            last.encodeAll(context);
        }

        writer.endElement("head");
    }

    protected void encodeCSS(FacesContext context, String library, String resource) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ExternalContext externalContext = context.getExternalContext();

        Resource cssResource = context.getApplication().getResourceHandler().createResource(resource, library);
        if (cssResource == null) {
            throw new FacesException("Error loading CSS, cannot find \"" + resource + "\" resource of \"" + library + "\" library");
        }
        else {
            writer.startElement("link", null);
            writer.writeAttribute("type", "text/css", null);
            writer.writeAttribute("rel", "stylesheet", null);
            writer.writeAttribute("href", externalContext.encodeResourceURL(cssResource.getRequestPath()), null);
            writer.endElement("link");
        }
    }

    protected void encodeJS(FacesContext context, String library, String script) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ExternalContext externalContext = context.getExternalContext();
        Resource resource = context.getApplication().getResourceHandler().createResource(script, library);

        if (resource == null) {
            throw new FacesException("Error loading JavaScript, cannot find \"" + script + "\" resource of \"" + library + "\" library");
        }
        else {
            writer.startElement("script", null);
            writer.writeAttribute("src", externalContext.encodeResourceURL(resource.getRequestPath()), null);
            writer.endElement("script");
        }
    }

    protected void encodeValidationResources(FacesContext context, boolean beanValidationEnabled) throws IOException {
        encodeJS(context, LIBRARY, "validation/validation.js");

        if (beanValidationEnabled) {
            encodeJS(context, LIBRARY, "validation/validation.bv.js");
        }
    }

    protected void encodeSettingScripts(FacesContext context, PrimeApplicationContext applicationContext, PrimeRequestContext requestContext,
            ResponseWriter writer, boolean csvEnabled) throws IOException {

        ProjectStage projectStage = context.getApplication().getProjectStage();

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write("if(window.PrimeFaces){");

        writer.write("PrimeFaces.settings.locale='" + LocaleUtils.getCurrentLocale(context) + "';");
        writer.write("PrimeFaces.settings.viewId='" + context.getViewRoot().getViewId() + "';");
        writer.write("PrimeFaces.settings.contextPath='" + context.getExternalContext().getRequestContextPath() + "';");

        writer.write("PrimeFaces.settings.cookiesSecure=" + (requestContext.isSecure() && applicationContext.getConfig().isCookiesSecure()) + ";");
        if (applicationContext.getConfig().getCookiesSameSite() != null) {
            writer.write("PrimeFaces.settings.cookiesSameSite='" + applicationContext.getConfig().getCookiesSameSite() + "';");
        }

        if (csvEnabled) {
            writer.write("PrimeFaces.settings.validateEmptyFields=" + applicationContext.getConfig().isValidateEmptyFields() + ";");
            writer.write("PrimeFaces.settings.considerEmptyStringNull=" + applicationContext.getConfig().isInterpretEmptyStringAsNull() + ";");
        }

        if (applicationContext.getConfig().isLegacyWidgetNamespace()) {
            writer.write("PrimeFaces.settings.legacyWidgetNamespace=true;");
        }

        if (applicationContext.getConfig().isEarlyPostParamEvaluation()) {
            writer.write("PrimeFaces.settings.earlyPostParamEvaluation=true;");
        }

        if (applicationContext.getConfig().isPartialSubmitEnabled()) {
            writer.write("PrimeFaces.settings.partialSubmit=true;");
        }

        if (projectStage != ProjectStage.Production) {
            writer.write("PrimeFaces.settings.projectStage='" + projectStage.toString() + "';");
        }

        if (applicationContext.getEnvironment().isAtLeastJsf22()) {
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
        }

        writer.write("}");
        writer.endElement("script");
    }

    protected void encodeInitScripts(ResponseWriter writer) throws IOException {
        List<String> scripts = PrimeRequestContext.getCurrentInstance().getInitScriptsToExecute();

        if (!scripts.isEmpty()) {
            writer.startElement("script", null);
            writer.writeAttribute("type", "text/javascript", null);

            boolean moveScriptsToBottom = PrimeRequestContext.getCurrentInstance().getApplicationContext().getConfig().isMoveScriptsToBottom();

            if (!moveScriptsToBottom) {
                writer.write("$(function(){");
            }

            for (int i = 0; i < scripts.size(); i++) {
                writer.write(scripts.get(i));
                writer.write(';');
            }

            if (!moveScriptsToBottom) {
                writer.write("});");
            }

            writer.endElement("script");
        }
    }
}
