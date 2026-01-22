/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.MapBuilder;
import org.primefaces.util.ResourceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.FacesException;
import jakarta.faces.application.ProjectStage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.lifecycle.ClientWindow;
import jakarta.faces.render.Renderer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

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
public class HeadRenderer extends Renderer<UIComponent> {

    private static final Logger LOGGER = Logger.getLogger(HeadRenderer.class.getName());
    private static final String LIBRARY = "primefaces";

    private static final Map<String, String> THEME_MAPPING = MapBuilder.<String, String>builder()
            .put("saga", "saga-blue")
            .put("arya", "arya-blue")
            .put("vela", "vela-blue")
            .build();

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(context);
        PrimeApplicationContext applicationContext = requestContext.getApplicationContext();
        PrimeConfiguration configuration = applicationContext.getConfig();

        writer.startElement("head", component);
        writer.writeAttribute("id", component.getClientId(context), "id");

        //First facet
        UIComponent first = component.getFacet("first");
        if (FacetUtils.shouldRenderFacet(first)) {
            first.encodeAll(context);
        }

        //Theme
        String theme;
        String themeParamValue = applicationContext.getConfig().getTheme();

        if (themeParamValue != null) {
            theme = context.getApplication().evaluateExpressionGet(context, themeParamValue, String.class);
        }
        else {
            theme = "saga-blue";     //default
        }

        if (theme != null && !"none".equals(theme)) {
            if (THEME_MAPPING.containsKey(theme)) {
                theme = THEME_MAPPING.get(theme);
            }

            encodeCSS(context, LIBRARY + "-" + theme, "theme.css");
        }

        //Icons
        if (applicationContext.getConfig().isPrimeIconsEnabled()) {
            encodeCSS(context, LIBRARY, "primeicons/primeicons.css");
        }

        //Middle facet
        UIComponent middle = component.getFacet("middle");
        if (FacetUtils.shouldRenderFacet(middle)) {
            middle.encodeAll(context);
        }

        // normal CSV is a required dependency for some special components like fileupload
        encodeValidationResources(context, configuration);

        // encode client side locale
        encodeLocaleResources(context, configuration);

        //Registered Resources
        UIViewRoot viewRoot = context.getViewRoot();
        List<UIComponent> resources = new ArrayList<>(viewRoot.getComponentResources(context, "head"));
        moveResourceToTop(resources, "primeicons/primeicons.css");
        moveResourceToTop(resources, "theme.css");
        for (int i = 0; i < resources.size(); i++) {
            UIComponent resource = resources.get(i);
            LOGGER.log(Level.FINE, () -> "HeadRenderer resource: " + resource.getAttributes().get("name"));
            resource.encodeAll(context);
        }

        encodeSettingScripts(context, requestContext, configuration, writer);

        // encode initialization scripts
        encodeInitScripts(context, requestContext, configuration, writer);
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

    protected void encodeCSS(FacesContext context, String library, String resource) throws IOException {
        ResourceUtils.addStyleSheetResource(context, library, resource);
    }

    protected void encodeJS(FacesContext context, String library, String script) throws IOException {
        ResourceUtils.addJavascriptResource(context, library, script);
    }

    protected void encodeValidationResources(FacesContext context, PrimeConfiguration configuration) {
        if (configuration.isClientSideValidationEnabled()) {
            // moment is needed for Date validation
            ResourceUtils.addJavascriptResource(context, "moment/moment.js");
        }
    }

    protected void encodeLocaleResources(FacesContext context, PrimeConfiguration configuration) {
        if (!configuration.isClientSideLocalizationEnabled()) {
            return;
        }

        try {
            final Locale locale = LocaleUtils.getCurrentLocale(context);
            ResourceUtils.addJavascriptResource(context, "locales/locale-" + locale.getLanguage() + ".js");
        }
        catch (FacesException e) {
            if (context.isProjectStage(ProjectStage.Development)) {
                LOGGER.log(Level.WARNING, "Failed to load client side locale.js. {0}", e.getMessage());
            }
        }
    }

    protected void encodeSettingScripts(FacesContext context, PrimeRequestContext requestContext,
                                       PrimeConfiguration configuration, ResponseWriter writer) throws IOException {

        ProjectStage projectStage = context.getApplication().getProjectStage();
        ExternalContext externalContext = context.getExternalContext();

        writer.startElement("script", null);
        RendererUtils.encodeScriptTypeIfNecessary(context);
        writer.write("if(window.PrimeFaces){");

        writer.write("PrimeFaces.settings={");
        writer.write("locale:'" + LocaleUtils.getCurrentLocale(context) + "',");
        writer.write("viewId:'" + context.getViewRoot().getViewId() + "',");
        writer.write("contextPath:'" + EscapeUtils.forJavaScript(externalContext.getRequestContextPath()) + "',");
        writer.write("cookiesSecure:" + (requestContext.isSecure() && configuration.isCookiesSecure()));

        if (configuration.getCookiesSameSite() != null) {
            writer.write(",cookiesSameSite:'" + configuration.getCookiesSameSite() + "'");
        }

        writer.write(",validateEmptyFields:" + configuration.isValidateEmptyFields());
        writer.write(",considerEmptyStringNull:" + configuration.isInterpretEmptyStringAsNull());

        if (configuration.isEarlyPostParamEvaluation()) {
            writer.write(",earlyPostParamEvaluation:true");
        }

        if (configuration.isPartialSubmitEnabled()) {
            writer.write(",partialSubmit:true");
        }

        if (projectStage != ProjectStage.Production) {
            writer.write(",projectStage:'" + projectStage.toString() + "'");
        }

        Map<String, String> errorPages = PrimeApplicationContext.getCurrentInstance(context).getConfig().getErrorPages();
        if (!errorPages.isEmpty()) {
            int i = 0;
            writer.write(",errorPages:{");
            for (Map.Entry<String, String> entry : errorPages.entrySet()) {
                if (i > 0) {
                    writer.write(',');
                }

                String errorPageUrl = context.getExternalContext().getRequestContextPath() + entry.getValue();
                errorPageUrl = context.getApplication().evaluateExpressionGet(context, errorPageUrl, String.class);
                errorPageUrl = context.getExternalContext().encodeActionURL(errorPageUrl);

                writer.write("'" + (entry.getKey() == null ? "" : entry.getKey()) + "':'" + errorPageUrl + "'");

                i++;
            }
            writer.write("}");
        }

        writer.write("};");

        if (externalContext.getClientWindow() != null) {
            ClientWindow clientWindow = externalContext.getClientWindow();
            if (clientWindow instanceof PrimeClientWindow) {

                boolean initialRedirect = false;

                Object cookie = PrimeClientWindowUtils.getInitialRedirectCookie(context, clientWindow.getId());
                if (cookie instanceof Cookie) {
                    Cookie servletCookie = (Cookie) cookie;
                    initialRedirect = true;

                    // expire/remove cookie
                    servletCookie.setMaxAge(0);
                    ((HttpServletResponse) externalContext.getResponse()).addCookie(servletCookie);
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

    protected void encodeInitScripts(FacesContext context, PrimeRequestContext requestContext, PrimeConfiguration configuration,
                                    ResponseWriter writer) throws IOException {
        List<String> scripts = requestContext.getInitScriptsToExecute();

        if (scripts.isEmpty()) {
            return;
        }

        writer.startElement("script", null);
        RendererUtils.encodeScriptTypeIfNecessary(context);

        boolean moveScriptsToBottom = configuration.isMoveScriptsToBottom();

        if (!moveScriptsToBottom) {
            writer.write("(function(){const pfInit=()=>{");
            writer.write(String.join(";", scripts));
            writer.write("};if(window.$)$(pfInit);");
            writer.write("else if(document.readyState==='complete')pfInit();");
            writer.write("else document.addEventListener('DOMContentLoaded',pfInit)})();");
        }
        else {
            writer.write(String.join(";", scripts));
            writer.write(';');
        }

        writer.endElement("script");
    }

    /**
     * Moves a resource component to the top of the resources list based on its name.
     * This is used to ensure certain resources like theme.css and primeicons.css are loaded first.
     *
     * @param resources The list of UIComponent resources to sort
     * @param resource The resource name suffix to move to the top
     */
    protected void moveResourceToTop(List<UIComponent> resources, String resource) {
        resources.sort((a, b) -> {
            String nameA = (String) a.getAttributes().get("name");
            String nameB = (String) b.getAttributes().get("name");
            if (nameA != null && nameA.endsWith(resource)) return -1;
            if (nameB != null && nameB.endsWith(resource)) return 1;
            return 0;
        });
    }
}
