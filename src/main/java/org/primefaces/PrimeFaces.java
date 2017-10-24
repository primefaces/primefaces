/**
 * Copyright 2009-2017 PrimeTek.
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
package org.primefaces;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import org.primefaces.component.datatable.TableState;
import org.primefaces.context.RequestContext;
import org.primefaces.expression.ComponentNotFoundException;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.visit.ResetInputVisitCallback;

public class PrimeFaces {

    private static final Logger LOG = Logger.getLogger(PrimeFaces.class.getName());
    
    // TODO - there are 2 possible solutions
    // 1) the current static solution + use Faces/RequestContext#getCurrentInstance each time
    // 2) make PrimeFaces requestScoped and receive Faces/RequestContext only once
    private static final PrimeFaces INSTANCE = new PrimeFaces();
    
    private final Dialog dialog;
    private final Ajax ajax;
    
    private PrimeFaces() {
        dialog = new Dialog();
        ajax = new Ajax();
    }

    public static PrimeFaces current() {
        return INSTANCE;
    }
    
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
    
    protected RequestContext getRequestContext() {
        return RequestContext.getCurrentInstance();
    }
    
    /**
     * Shortcut for {@link PartialViewContext#isAjaxRequest()}.
     * 
     * @return <code>true</code> if the current request is a AJAX request.
     */
    public boolean isAjaxRequest() {
        return getFacesContext().getPartialViewContext().isAjaxRequest();
    }
    
    /**
     * Executes a JavaScript statement.
     * 
     * @param statement the JavaScript statement.
     */
    public void executeScript(String statement) {
        getRequestContext().getScriptsToExecute().add(statement);
    }
    
    /**
     * Scrolls to a component with the given clientId.
     *
     * @param clientId clientId of the target component.
     */
    public void scrollTo(String clientId) {
        executeScript("PrimeFaces.scrollTo('" + clientId + "');");
    }
    
    /**
     * Resets the resolved inputs.
     *
     * @param expressions a list of search expressions.
     */
    public void resetInputs(Collection<String> expressions) {
        if (expressions == null || expressions.isEmpty()) {
            return;
        }

        FacesContext facesContext = getFacesContext();
        VisitContext visitContext = VisitContext.createVisitContext(facesContext, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);

        for (String expression : expressions) {
            reset(facesContext, visitContext, expression);
        }
    }

    /**
     * Resets the resolved inputs.
     *
     * @param expressions a list of search expressions.
     */
    public void resetInputs(String... expressions) {
        if (expressions == null || expressions.length == 0) {
            return;
        }

        FacesContext facesContext = getFacesContext();
        VisitContext visitContext = VisitContext.createVisitContext(facesContext, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);

        for (String expression : expressions) {
            reset(facesContext, visitContext, expression);
        }
    }
    
    private void reset(FacesContext facesContext, VisitContext visitContext, String expressions) {
        UIViewRoot root = facesContext.getViewRoot();

        List<UIComponent> components = SearchExpressionFacade.resolveComponents(facesContext, root, expressions);
        for (UIComponent component : components) {
            component.visitTree(visitContext, ResetInputVisitCallback.INSTANCE);
        }
    }
    
    public void clearTableStates() {
        getFacesContext().getExternalContext().getSessionMap().remove(Constants.TABLE_STATE);
    }

    public void clearTableState(String key) {
        Map<String, Object> sessionMap = getFacesContext().getExternalContext().getSessionMap();
        Map<String, TableState> dtState = (Map) sessionMap.get(Constants.TABLE_STATE);
        if (dtState != null) {
            dtState.remove(key);
        }
    }

    /**
     * Returns the dialog helpers.
     * 
     * @return the dialog helpers.
     */
    public Dialog dialog() {
        return dialog;
    }

    public class Dialog {

        /**
         * Opens a view in a dynamic dialog.
         *
         * @param outcome the logical outcome used to resolve the navigation case.
         */
        public void openDynamic(String outcome) {
            getFacesContext().getAttributes().put(Constants.DIALOG_FRAMEWORK.OUTCOME, outcome);
        }

        /**
         * Opens a view in a dynamic dialog.
         *
         * @param outcome the logical outcome used to resolve the navigation case.
         * @param options configuration options for the dialog.
         * @param params parameters to send to the view displayed in the dynamic dialog.
         */
        public void openDynamic(String outcome, Map<String, Object> options, Map<String, List<String>> params) {
            FacesContext facesContext = getFacesContext();
            facesContext.getAttributes().put(Constants.DIALOG_FRAMEWORK.OUTCOME, outcome);

            if (options != null) {
                facesContext.getAttributes().put(Constants.DIALOG_FRAMEWORK.OPTIONS, options);
            }

            if (params != null) {
                facesContext.getAttributes().put(Constants.DIALOG_FRAMEWORK.PARAMS, params);
            }
        }

        /**
         * Close the current dynamic dialog.
         *
         * @param data optional data to pass back to a dialogReturn event.
         */
        public void closeDynamic(Object data) {
            FacesContext facesContext = getFacesContext();
            Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
            String pfdlgcid = params.get(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM);

            if (data != null) {
                Map<String, Object> session = facesContext.getExternalContext().getSessionMap();
                session.put(pfdlgcid, data);
            }

            executeScript("PrimeFaces.closeDialog({pfdlgcid:'" + pfdlgcid + "'});");
        }
        
        /**
         * Displays a message in a dynamic dialog.
         *
         * @param message the {@link FacesMessage} to be displayed.
         */
        public void showMessageDynamic(FacesMessage message) {
            String summary = ComponentUtils.escapeText(message.getSummary());
            summary = ComponentUtils.replaceNewLineWithHtml(summary);

            String detail = ComponentUtils.escapeText(message.getDetail());
            detail = ComponentUtils.replaceNewLineWithHtml(detail);

            executeScript("PrimeFaces.showMessageInDialog({severity:\"" + message.getSeverity()
                    + "\",summary:\"" + summary
                    + "\",detail:\"" + detail + "\"});");
        }
    }
    
    public Ajax ajax() {
        if (!isAjaxRequest()) {
            throw new FacesException("ajax() can only be used in AJAX requests!");
        }
        
        return ajax;
    }
    
    public class Ajax {
        /**
         * Add a parameter for ajax oncomplete client side callbacks. Value will be serialized to json.
         * Currently supported values are primitives, POJOs, JSONObject and JSONArray.
         *
         * @param name name of the parameter.
         * @param value value of the parameter.
         */
        public void addCallbackParam(String name, Object value) {
            getRequestContext().getCallbackParams().put(name, value);
        }
        
        /**
         * Updates all components with the given clientIds.
         *
         * @param clientIds a list of clientIds.
         */
        public void update(Collection<String> clientIds) {
            if (clientIds == null || clientIds.isEmpty()) {
                return;
            }
            
            FacesContext facesContext = getFacesContext();
            
            // call SEF to validate if a component with the clientId exists
            if (facesContext.isProjectStage(ProjectStage.Development)) {
                for (String clientId : clientIds) {
                    try {
                        SearchExpressionFacade.resolveClientId(facesContext, facesContext.getViewRoot(), clientId);
                    }
                    catch (ComponentNotFoundException e) {
                        LOG.severe(e.getMessage());
                    }
                }
            }
            
            facesContext.getPartialViewContext().getRenderIds().addAll(clientIds);
        }
        
        /**
         * Updates all components with the given clientIds.
         *
         * @param clientIds a list of clientIds.
         */
        public void update(String... clientIds) {
            if (clientIds == null || clientIds.length == 0) {
                return;
            }

            List<String> collection = Arrays.asList(clientIds);
            update(collection);
        }
    }
}
