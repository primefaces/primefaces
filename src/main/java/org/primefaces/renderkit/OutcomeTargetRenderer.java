/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import org.primefaces.behavior.confirm.ConfirmBehavior;
import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.UIOutcomeTarget;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.util.ComponentTraversalUtils;

import javax.faces.FacesException;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionListener;
import javax.faces.flow.FlowHandler;
import javax.faces.lifecycle.ClientWindow;
import java.io.IOException;
import java.util.*;
import org.primefaces.component.api.ClientBehaviorRenderingMode;
import org.primefaces.component.api.DialogReturnAware;
import org.primefaces.util.Constants;

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

    protected void encodeOnClick(FacesContext context, UIComponent source, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        setConfirmationScript(context, menuitem);

        String onclick = menuitem.getOnclick();

        //GET
        if (menuitem.getUrl() != null || menuitem.getOutcome() != null) {
            String targetURL = getTargetURL(context, (UIOutcomeTarget) menuitem);
            writer.writeAttribute("href", targetURL, null);

            if (menuitem.getTarget() != null) {
                writer.writeAttribute("target", menuitem.getTarget(), null);
            }
        }
        //POST
        else {
            writer.writeAttribute("href", "#", null);

            UIForm form = ComponentTraversalUtils.closestForm(context, source);
            if (form == null) {
                throw new FacesException("MenuItem must be inside a form element");
            }

            String command;
            if (menuitem.isDynamic()) {
                String menuClientId = source.getClientId(context);
                Map<String, List<String>> params = menuitem.getParams();
                if (params == null) {
                    params = new LinkedHashMap<>();
                }
                List<String> idParams = Collections.singletonList(menuitem.getId());
                params.put(menuClientId + "_menuid", idParams);

                command = menuitem.isAjax()
                        ? buildAjaxRequest(context, source, (AjaxSource) menuitem, form, params)
                        : buildNonAjaxRequest(context, source, form, menuClientId, params, true);
            }
            else {
                command = menuitem.isAjax()
                        ? buildAjaxRequest(context, (UIComponent & AjaxSource) menuitem, form)
                        : buildNonAjaxRequest(context, ((UIComponent) menuitem), form, ((UIComponent) menuitem).getClientId(context), true);
            }

            onclick = (onclick == null) ? command : onclick + ";" + command;
        }

        if (onclick != null) {
            if (menuitem.requiresConfirmation()) {
                writer.writeAttribute("data-pfconfirmcommand", onclick, null);
                writer.writeAttribute("onclick", menuitem.getConfirmationScript(), "onclick");
            }
            else {
                writer.writeAttribute("onclick", onclick, null);
            }
        }

        if (menuitem instanceof DialogReturnAware) {
            List<ClientBehaviorContext.Parameter> behaviorParams = new ArrayList<>();
            behaviorParams.add(new ClientBehaviorContext.Parameter(Constants.CLIENT_BEHAVIOR_RENDERING_MODE, ClientBehaviorRenderingMode.UNOBSTRUSIVE));
            String dialogReturnBehavior = getEventBehaviors(context, (ClientBehaviorHolder) menuitem, DialogReturnAware.EVENT_DIALOG_RETURN,
                    behaviorParams);
            if (dialogReturnBehavior != null) {
                writer.writeAttribute(DialogReturnAware.ATTRIBUTE_DIALOG_RETURN_SCRIPT, dialogReturnBehavior, null);
            }
        }
    }

    protected void setConfirmationScript(FacesContext context, MenuItem item) {
        if (item instanceof ClientBehaviorHolder) {
            Map<String, List<ClientBehavior>> behaviors = ((ClientBehaviorHolder) item).getClientBehaviors();
            List<ClientBehavior> clickBehaviors = (behaviors == null) ? null : behaviors.get("click");

            if (clickBehaviors != null && !clickBehaviors.isEmpty()) {
                for (int i = 0; i < clickBehaviors.size(); i++) {
                    ClientBehavior clientBehavior = clickBehaviors.get(i);
                    if (clientBehavior instanceof ConfirmBehavior) {
                        ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(
                                context, (UIComponent) item, "click", item.getClientId(), Collections.EMPTY_LIST);
                        clientBehavior.getScript(cbc);
                        break;
                    }
                }
            }
        }
    }
}
