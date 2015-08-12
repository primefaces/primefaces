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
import org.primefaces.context.RequestContext;

public class OutcomeTargetRenderer extends CoreRenderer {
    
    protected NavigationCase findNavigationCase(FacesContext context, UIOutcomeTarget outcomeTarget) {
        ConfigurableNavigationHandler navigationHandler = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();
        String outcome = outcomeTarget.getOutcome();
        
        if (outcome == null) {
            outcome = context.getViewRoot().getViewId();
        }
        
        if (RequestContext.getCurrentInstance().getApplicationContext().getConfig().isAtLeastJSF22()) {
            if (outcomeTarget instanceof UIComponent) {
                String toFlowDocumentId = (String) ((UIComponent) outcomeTarget).getAttributes().get(ActionListener.TO_FLOW_DOCUMENT_ID_ATTR_NAME);

                if (toFlowDocumentId != null) {
                    return navigationHandler.getNavigationCase(context, null, outcome, toFlowDocumentId);
                }
            }
        }
        
        return navigationHandler.getNavigationCase(context, null, outcome);
    }

    /**
     * Find all parameters to include by looking at nested uiparams and params of navigation case
     */
    protected Map<String, List<String>> getParams(NavigationCase navCase, UIOutcomeTarget outcomeTarget) {
        //UI Params
        Map<String, List<String>> params = outcomeTarget.getParams();       

        //NavCase Params
        Map<String, List<String>> navCaseParams = navCase.getParameters();
        if (navCaseParams != null && !navCaseParams.isEmpty()) {
            if (params == null) {
                params = new LinkedHashMap<String, List<String>>();
            }
            
            for (Map.Entry<String,List<String>> entry : navCaseParams.entrySet()) {
                String key = entry.getKey();

                //UIParams take precedence
                if (!params.containsKey(key)) {
                    params.put(key, entry.getValue());
                }
            }
        }

        if (RequestContext.getCurrentInstance().getApplicationContext().getConfig().isAtLeastJSF22()) {
            String toFlowDocumentId = navCase.getToFlowDocumentId();
            if (toFlowDocumentId != null) {
                if (params == null) {
                    params = new LinkedHashMap<String, List<String>>();
                }

                List<String> flowDocumentIdValues = new ArrayList<String>();
                flowDocumentIdValues.add(toFlowDocumentId);
                params.put(FlowHandler.TO_FLOW_DOCUMENT_ID_REQUEST_PARAM_NAME, flowDocumentIdValues);
                
                if (!FlowHandler.NULL_FLOW.equals(toFlowDocumentId)) {
                    List<String> flowIdValues = new ArrayList<String>();
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
            url = getResourceURL(context, href);
        }
        else {
            NavigationCase navCase = findNavigationCase(context, outcomeTarget);

            if (navCase == null) {
            	throw new FacesException("Could not resolve NavigationCase for outcome: " + outcomeTarget.getOutcome());
            }

            String toViewId = navCase.getToViewId(context);
            boolean isIncludeViewParams = isIncludeViewParams(outcomeTarget, navCase);
            Map<String, List<String>> params = getParams(navCase, outcomeTarget);

            if (params == null)
            {
                params = Collections.emptyMap();
            }

            boolean clientWindowRenderingModeEnabled = false;
            Object clientWindow = null;
            
            try {
                if (RequestContext.getCurrentInstance().getApplicationContext().getConfig().isAtLeastJSF22() && outcomeTarget.isDisableClientWindow()) {
                    clientWindow = context.getExternalContext().getClientWindow();

                    if (clientWindow != null) {
                        clientWindowRenderingModeEnabled = ((ClientWindow) clientWindow).isClientWindowRenderModeEnabled(context);
                        
                        if (clientWindowRenderingModeEnabled) {
                            ((ClientWindow) clientWindow).disableClientWindowRenderMode(context);
                        }
                    }
                }
                
                url = context.getApplication().getViewHandler().getBookmarkableURL(context, toViewId, params, isIncludeViewParams);

            } finally {
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
