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

import java.util.*;

import javax.faces.FacesException;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;
import javax.faces.flow.FlowHandler;
import javax.faces.lifecycle.ClientWindow;

import org.primefaces.component.api.UIOutcomeTarget;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.util.LangUtils;

public class OutcomeTargetRenderer extends CoreRenderer {

    protected NavigationCase findNavigationCase(FacesContext context, UIOutcomeTarget outcomeTarget) {
        ConfigurableNavigationHandler navigationHandler = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();
        String outcome = outcomeTarget.getOutcome();

        if (outcome == null) {
            outcome = context.getViewRoot().getViewId();
        }

        if (PrimeApplicationContext.getCurrentInstance(context).getEnvironment().isAtLeastJsf22()) {
            if (outcomeTarget instanceof UIComponent) {
                String toFlowDocumentId = (String) ((UIComponent) outcomeTarget).getAttributes().get(ActionListener.TO_FLOW_DOCUMENT_ID_ATTR_NAME);

                if (toFlowDocumentId != null) {
                    return navigationHandler.getNavigationCase(context, null, outcome, toFlowDocumentId);
                }
            }
        }

        return navigationHandler.getNavigationCase(context, null, outcome);
    }

    protected boolean isExpression(String text) {
        return text.contains("#{") || text.contains("${");
    }

    protected boolean containsEL(List<String> values) {
        if (!values.isEmpty()) {
            // Both MyFaces and Mojarra use ArrayLists. Therefore, index loop can be used.
            for (int i = 0; i < values.size(); i++) {
                if (isExpression(values.get(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    protected List<String> evaluateValueExpressions(FacesContext context, List<String> values) {
        // note that we have to create a new List here, because if we
        // change any value on the given List, it will be changed in the
        // NavigationCase too and the EL expression won't be evaluated again
        List<String> target = new ArrayList<>(values.size());
        for (String value : values) {
            if (isExpression(value)) {
                // evaluate the ValueExpression
                value = context.getApplication().evaluateExpressionGet(context, value, String.class);
            }
            if (!LangUtils.isValueEmpty(value)) {
                target.add(value);
            }
        }
        return target;
    }

    /**
     * Find all parameters to include by looking at nested uiparams and params of navigation case
     */
    protected Map<String, List<String>> getParams(FacesContext context, NavigationCase navCase, UIOutcomeTarget outcomeTarget) {
        //UI Params
        Map<String, List<String>> params = outcomeTarget.getParams();

        //NavCase Params
        Map<String, List<String>> navCaseParams = navCase.getParameters();
        if (navCaseParams != null && !navCaseParams.isEmpty()) {
            if (params == null) {
                params = new LinkedHashMap<>();
            }

            for (Map.Entry<String, List<String>> entry : navCaseParams.entrySet()) {
                String key = entry.getKey();

                //UIParams take precedence
                if (!params.containsKey(key)) {
                    List<String> values = entry.getValue();
                    if (containsEL(values)) {
                        params.put(key, evaluateValueExpressions(context, values));
                    }
                    else {
                        params.put(key, values);
                    }
                }
            }
        }

        if (PrimeApplicationContext.getCurrentInstance(context).getEnvironment().isAtLeastJsf22()) {
            String toFlowDocumentId = navCase.getToFlowDocumentId();
            if (toFlowDocumentId != null) {
                if (params == null) {
                    params = new LinkedHashMap<>();
                }

                List<String> flowDocumentIdValues = new ArrayList<>();
                flowDocumentIdValues.add(toFlowDocumentId);
                params.put(FlowHandler.TO_FLOW_DOCUMENT_ID_REQUEST_PARAM_NAME, flowDocumentIdValues);

                if (!FlowHandler.NULL_FLOW.equals(toFlowDocumentId)) {
                    List<String> flowIdValues = new ArrayList<>();
                    flowIdValues.add(navCase.getFromOutcome());
                    params.put(FlowHandler.FLOW_ID_REQUEST_PARAM_NAME, flowIdValues);
                }
            }
        }

        return params;
    }

    protected boolean isIncludeViewParams(UIOutcomeTarget outcomeTarget, NavigationCase navCase) {
        return outcomeTarget.isIncludeViewParams() || navCase.isIncludeViewParams();
    }

    protected String getTargetURL(FacesContext context, UIOutcomeTarget outcomeTarget) {
        String url;

        boolean clientWindowRenderingModeEnabled = false;
        Object clientWindow = null;
        try {
            if (PrimeApplicationContext.getCurrentInstance(context).getEnvironment().isAtLeastJsf22()
                        && outcomeTarget.isDisableClientWindow()) {

                clientWindow = context.getExternalContext().getClientWindow();

                if (clientWindow != null) {
                    clientWindowRenderingModeEnabled = ((ClientWindow) clientWindow).isClientWindowRenderModeEnabled(context);

                    if (clientWindowRenderingModeEnabled) {
                        ((ClientWindow) clientWindow).disableClientWindowRenderMode(context);
                    }
                }
            }

            String href = outcomeTarget.getHref();
            if (href != null) {
                url = "#".equals(href) ? "#" : context.getExternalContext().encodeRedirectURL(href, outcomeTarget.getParams());
            }
            else {
                NavigationCase navCase = findNavigationCase(context, outcomeTarget);

                if (navCase == null) {
                    throw new FacesException("Could not resolve NavigationCase for outcome: " + outcomeTarget.getOutcome());
                }

                String toViewId = navCase.getToViewId(context);
                boolean isIncludeViewParams = isIncludeViewParams(outcomeTarget, navCase);
                Map<String, List<String>> params = getParams(context, navCase, outcomeTarget);

                if (params == null) {
                    params = Collections.emptyMap();
                }

                url = context.getApplication().getViewHandler().getBookmarkableURL(context, toViewId, params, isIncludeViewParams);

                if (outcomeTarget.getFragment() != null) {
                    url += "#" + outcomeTarget.getFragment();
                }
            }
        }
        finally {
            if (clientWindowRenderingModeEnabled && clientWindow != null) {
                ((ClientWindow) clientWindow).enableClientWindowRenderMode(context);
            }
        }

        return url;
    }
}
