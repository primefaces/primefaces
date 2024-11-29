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
import org.primefaces.util.MapBuilder;
import org.primefaces.util.ResourceUtils;

import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
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
    }

    @Override
    public void afterPhase(PhaseEvent event) {
       // no-op
    }
}