/**
 * Copyright 2009-2018 PrimeTek.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            target.add(value);
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

                url = context.getApplication().getViewHandler().getBookmarkableURL(context, toViewId, params, isIncludeViewParams);

            }
            finally {
                if (clientWindowRenderingModeEnabled && clientWindow != null) {
                    ((ClientWindow) clientWindow).enableClientWindowRenderMode(context);
                }
            }

            if (outcomeTarget.getFragment() != null) {
                url += "#" + outcomeTarget.getFragment();
            }
        }

        return url;
    }
}
