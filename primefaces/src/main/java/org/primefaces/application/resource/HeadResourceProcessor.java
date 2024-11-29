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
package org.primefaces.application.resource;

import org.primefaces.config.PrimeConfiguration;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.util.Constants;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.MapBuilder;
import org.primefaces.util.ResourceUtils;

import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * PhaseListener implementation that processes and adds required PrimeFaces resources to the head section during the RENDER_RESPONSE phase.
 * This ensures core resources like themes, icons, and validation/localization scripts are added before component-specific resources.
 *
 * @since 15.0.0
 */
public class HeadResourceProcessor implements PhaseListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(HeadResourceProcessor.class.getName());
    private static final Map<String, String> THEME_MAPPING = MapBuilder.<String, String>builder()
            .put("saga", "saga-blue")
            .put("arya", "arya-blue")
            .put("vela", "vela-blue")
            .build();

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();

        /*
         * Check if this is an AJAX request by checking postback or partial view context.
         * For AJAX requests, we skip loading core resources since they should already be loaded.
         */
        if (context.getPartialViewContext().isAjaxRequest()) {
            return;
        }

        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(context);
        PrimeApplicationContext applicationContext = requestContext.getApplicationContext();
        PrimeConfiguration configuration = applicationContext.getConfig();

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
            theme = "saga-blue";     //default
        }

        if (theme != null && !"none".equals(theme)) {
            if (THEME_MAPPING.containsKey(theme)) {
                theme = THEME_MAPPING.get(theme);
            }

            ResourceUtils.addStyleSheetResource(context, Constants.LIBRARY + "-" + theme, "theme.css");
        }

        // Icons
        if (configuration.isPrimeIconsEnabled()) {
            ResourceUtils.addStyleSheetResource(context, "primeicons/primeicons.css");
        }

        /*
         * Core resources (jQuery + core.js) are only needed for non-AJAX requests when validation
         * or localization is enabled. This prevents duplicate loading of resources and optimizes
         * page load time.
         */
        if (configuration.isClientSideValidationEnabled() || configuration.isClientSideLocalizationEnabled()) {
            /*
             * jQuery must be loaded before core.js and any validation/localization resources
             * to ensure proper dependency loading order
             */
            ResourceUtils.addJavascriptResource(context, "jquery/jquery.js");
            ResourceUtils.addJavascriptResource(context, "core.js");
        }

        /*
         * For non-AJAX requests, load validation and localization resources if configured.
         * These depend on core resources being loaded first to function properly.
         */
        encodeValidationResources(context, configuration);
        encodeLocalizationResources(context, configuration);
    }

    @Override
    public void afterPhase(PhaseEvent event) {
       // no-op
    }

    /**
     * Encodes validation resources if client side validation is enabled.
     * Adds moment.js for date validation and bean validation resources if enabled.
     *
     * @param context The FacesContext instance
     * @param configuration The PrimeConfiguration instance containing validation settings
     */
    protected void encodeValidationResources(FacesContext context, PrimeConfiguration configuration) {
        if (configuration.isClientSideValidationEnabled()) {
            // moment is needed for Date validation
            ResourceUtils.addJavascriptResourceToBody(context,  "moment/moment.js");

            // BV CSV is optional and must be enabled by config
            if (configuration.isBeanValidationEnabled()) {
                ResourceUtils.addJavascriptResourceToBody(context, "validation/validation.bv.js");
            }
        }
    }

    /**
     * Encodes localization resources if client side localization is enabled.
     * Attempts to load the appropriate locale JavaScript file based on the current locale.
     * Logs a warning in Development stage if locale file fails to load.
     *
     * @param context The FacesContext instance
     * @param configuration The PrimeConfiguration instance containing localization settings
     */
    private void encodeLocalizationResources(FacesContext context, PrimeConfiguration configuration) {
        if (configuration.isClientSideLocalizationEnabled()) {
            try {
                Locale locale = LocaleUtils.getCurrentLocale(context);
                ResourceUtils.addJavascriptResourceToBody(context, "locales/locale-" + locale.getLanguage() + ".js");
            }
            catch (FacesException e) {
                if (context.isProjectStage(ProjectStage.Development)) {
                    LOGGER.log(Level.WARNING,
                                "Failed to load client side locale.js. {0}", e.getMessage());
                }
            }
        }
    }
}