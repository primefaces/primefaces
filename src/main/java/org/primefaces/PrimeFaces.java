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
package org.primefaces;

import org.primefaces.component.datatable.TableState;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.expression.ComponentNotFoundException;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.visit.ResetInputVisitCallback;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrimeFaces {

    private static final Logger LOGGER = Logger.getLogger(PrimeFaces.class.getName());

    // There are 2 possible solutions
    // 1) the current static solution + use Faces/RequestContext#getCurrentInstance each time
    // 2) make PrimeFaces requestScoped and receive Faces/RequestContext only once
    private static PrimeFaces instance = new PrimeFaces();

    private final Dialog dialog;
    private final Ajax ajax;

    /**
     * Protected constructor to allow CDI proxying - and also allow customizations, or setting a mock.
     */
    protected PrimeFaces() {
        dialog = new Dialog();
        ajax = new Ajax();
    }

    public static PrimeFaces current() {
        return instance;
    }

    public static void setCurrent(PrimeFaces primeFaces) {
        instance = primeFaces;
    }

    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    protected PrimeRequestContext getRequestContext() {
        return PrimeRequestContext.getCurrentInstance();
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
     * Resolves the search expression, starting from the viewroot, and focus the resolved component.
     *
     * @param expression The search expression.
     */
    public void focus(String expression) {
        focus(expression, FacesContext.getCurrentInstance().getViewRoot());
    }

    /**
     * Resolves the search expression and focus the resolved component.
     *
     * @param expression the search expression.
     * @param base the base component from which we will start to resolve the search expression.
     */
    public void focus(String expression, UIComponent base) {
        if (expression == null || expression.trim().isEmpty()) {
            return;
        }

        FacesContext facesContext = getFacesContext();

        String clientId = SearchExpressionFacade.resolveClientId(facesContext,
                base,
                expression);
        executeScript("PrimeFaces.focus('" + clientId + "');");
    }

    /**
     * Resolves the search expressions, starting from the viewroot and resets all found {@link UIInput} components.
     *
     * @param expressions a list of search expressions.
     */
    public void resetInputs(Collection<String> expressions) {
        if (expressions == null || expressions.isEmpty()) {
            return;
        }

        FacesContext facesContext = getFacesContext();
        VisitContext visitContext = VisitContext.createVisitContext(facesContext, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);


        UIViewRoot root = facesContext.getViewRoot();
        for (String expression : expressions) {
            List<UIComponent> components = SearchExpressionFacade.resolveComponents(facesContext, root, expression);
            for (UIComponent component : components) {
                component.visitTree(visitContext, ResetInputVisitCallback.INSTANCE);
            }
        }
    }

    /**
     * Resolves the search expressions, starting from the viewroot and resets all found {@link UIInput} components.
     *
     * @param expressions a list of search expressions.
     */
    public void resetInputs(String... expressions) {
        if (expressions == null || expressions.length == 0) {
            return;
        }

        resetInputs(Arrays.asList(expressions));
    }

    /**
     * Removes the multiViewState for all DataTables within the current session.
     */
    public void clearTableStates() {
        getFacesContext().getExternalContext().getSessionMap().remove(Constants.TABLE_STATE);
    }

    /**
     * Removes the multiViewState for one specific DataTable within the current session.
     * @param key Key of the DataTable. See {@link org.primefaces.component.datatable.DataTable#getTableState(boolean)} for the namebuild of this key.
     */
    public void clearTableState(String key) {
        Map<String, Object> sessionMap = getFacesContext().getExternalContext().getSessionMap();
        Map<String, TableState> dtState = (Map) sessionMap.get(Constants.TABLE_STATE);
        if (dtState != null) {
            dtState.remove(key);
        }
    }

    /**
     * Removes the multiViewState for all DataLists within the current session.
     */
    public void clearDataListStates() {
        getFacesContext().getExternalContext().getSessionMap().remove(Constants.DATALIST_STATE);
    }

    /**
     * Removes the multiViewState for one specific DataList within the current session.
     * @param key Key of the DataList. See {@link org.primefaces.component.datalist.DataList#getDataListState(boolean)}} for the namebuild of this key.
     */
    public void clearDataListState(String key) {
        Map<String, Object> sessionMap = getFacesContext().getExternalContext().getSessionMap();
        Map<String, TableState> dtState = (Map) sessionMap.get(Constants.DATALIST_STATE);
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
            String pfdlgcid = EscapeUtils.forJavaScript(params.get(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM));

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
            String summary = EscapeUtils.forJavaScript(message.getSummary());
            String detail = EscapeUtils.forJavaScript(message.getDetail());

            executeScript("PrimeFaces.showMessageInDialog({severity:\"" + message.getSeverity()
                    + "\",summary:\"" + summary
                    + "\",detail:\"" + detail + "\"});");
        }
    }

    public Ajax ajax() {
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
         * Updates all components with the given expressions or clientIds.
         *
         * @param expressions a list of expressions or clientIds.
         */
        public void update(Collection<String> expressions) {
            if (expressions == null || expressions.isEmpty()) {
                return;
            }

            FacesContext facesContext = getFacesContext();

            for (String expression : expressions) {

                if (LangUtils.isValueBlank(expression)) {
                    continue;
                }

                try {
                    String clientId =
                            SearchExpressionFacade.resolveClientId(facesContext, facesContext.getViewRoot(), expression);

                    facesContext.getPartialViewContext().getRenderIds().add(clientId);
                }
                catch (ComponentNotFoundException e) {
                    LOGGER.log(Level.WARNING,
                            "PrimeFaces.current().ajax().update() called but component can't be resolved!"
                            + "Expression will just be added to the renderIds.", e);

                    facesContext.getPartialViewContext().getRenderIds().add(expression);
                }
            }
        }

        /**
         * Updates all components with the given expressions or clientIds.
         *
         * @param expressions a list of expressions or clientIds.
         */
        public void update(String... expressions) {
            if (expressions == null || expressions.length == 0) {
                return;
            }

            update(Arrays.asList(expressions));
        }
    }
}
