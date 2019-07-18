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
package org.primefaces;

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
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PrimeFaces {

    private static final Logger LOGGER = Logger.getLogger(PrimeFaces.class.getName());

    // There are 2 possible solutions
    // 1) the current static solution + use Faces/RequestContext#getCurrentInstance each time
    // 2) make PrimeFaces requestScoped and receive Faces/RequestContext only once
    private static PrimeFaces instance = new PrimeFaces();

    private final Dialog dialog;
    private final Ajax ajax;
    private final MultiViewState multiViewState;

    /**
     * Protected constructor to allow CDI proxying - and also allow customizations, or setting a mock.
     */
    protected PrimeFaces() {
        dialog = new Dialog();
        ajax = new Ajax();
        multiViewState = new MultiViewState();
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
        if (LangUtils.isValueBlank(expression)) {
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
     *
     * @deprecated Use {@link MultiViewState#clearAll()} instead
     */
    @Deprecated
    public void clearTableStates() {
        multiViewState().clearAll();
    }

    /**
     * Removes the multiViewState for one specific DataTable within the current session.
     * @param key Key of the DataTable. See {@link org.primefaces.component.datatable.DataTable#getTableState(boolean)} for the namebuild of this key.
     *
     * @deprecated Use {@link MultiViewState#clear(String, String)} instead
     */
    @Deprecated
    public void clearTableState(String key) {
        clearDataListState(key);
    }

    /**
     * Removes the multiViewState for all DataLists within the current session.
     *
     * @deprecated Use {@link MultiViewState#clearAll()} instead
     */
    @Deprecated
    public void clearDataListStates() {
        multiViewState().clearAll();
    }

    /**
     * Removes the multiViewState for one specific DataList within the current session.
     * @param key Key of the DataList. See {@link org.primefaces.component.datalist.DataList#getDataListState(boolean)}} for the namebuild of this key.
     *
     * @deprecated Use {@link MultiViewState#clear(String, String)} instead
     */
    @Deprecated
    public void clearDataListState(String key) {
        Pattern p = Pattern.compile("(.*\\.(?:xhtml|jsf|jsp))_(.*)");
        Matcher m = p.matcher(key);
        if (m.find()) {
            multiViewState().clear(m.group(1), m.group(2));
        }
        else {
            LOGGER.warning("'" + key + "' does not follow format: " + p.pattern() + "." +
                    " Use PrimeFaces.multiViewState().clear(String viewId, String clientId) instead");
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

            executeScript("PrimeFaces.closeDialog({pfdlgcid:'" + EscapeUtils.forJavaScript(pfdlgcid) + "'});");
        }

        /**
         * Displays a message in a dynamic dialog with any HTML escaped.
         *
         * @param message the {@link FacesMessage} to be displayed.
         */
        public void showMessageDynamic(FacesMessage message) {
            showMessageDynamic(message, true);
        }

        /**
         * Displays a message in a dynamic dialog with escape control.
         *
         * @param message the {@link FacesMessage} to be displayed.
         * @param escape true to escape HTML content, false to display HTML content
         */
        public void showMessageDynamic(FacesMessage message, boolean escape) {
            String summary = EscapeUtils.forJavaScript(message.getSummary());
            String detail = EscapeUtils.forJavaScript(message.getDetail());

            executeScript("PrimeFaces.showMessageInDialog({severity:\"" + message.getSeverity()
                    + "\",summary:\"" + summary
                    + "\",detail:\"" + detail
                    + "\",escape:" + escape + "});");
        }
    }

    public Ajax ajax() {
        return ajax;
    }

    /**
     * Returns the MultiViewState helpers.
     *
     * @return the MultiViewState helpers.
     */
    public MultiViewState multiViewState() {
        return multiViewState;
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
                            + " Expression will just be added to the renderIds.", e);

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

    public class MultiViewState {

        private static final String SEPARATOR = "_";

        /**
         * Removes all multiViewState within the current session.
         */
        public void clearAll() {
            getFacesContext().getExternalContext().getSessionMap().remove(Constants.MULTI_VIEW_STATES);
        }

        /**
         * Removes all multiViewState in specific view within the current session.
         *
         * @param viewId viewId in which multiview state should be cleared
         */
        public void clearAll(String viewId) {
            clear(viewId, (Consumer<String>) null);
        }

        /**
         * Removes all multiViewState in specific view within the current session.
         *
         * @param viewId viewId in which multiview state should be cleared
         * @param clientIdConsumer operation to execute for every clientId after multiview state has been cleared
         */
        public void clear(String viewId, Consumer<String> clientIdConsumer) {
            String stateKey = createMVSViewId(viewId);
            Map<String, Object> multiViewStates = getMVSSessionMap();
            Set<String> states = multiViewStates.keySet().stream()
                    .filter(s -> s.startsWith(stateKey))
                    .collect(Collectors.toSet());
            if (!states.isEmpty()) {
                multiViewStates.keySet().removeAll(states);

                if (clientIdConsumer != null) {
                    states.stream()
                            .map(s -> s.replace(stateKey + SEPARATOR, Constants.EMPTY_STRING))
                            .forEach(clientIdConsumer);
                }
            }
        }

        /**
         * Removes multiViewState of a component in specific view within the current session.
         *
         * @param viewId viewId of a page
         * @param clientId clientId of a component for which multiview state should be cleared
         */
        public void clear(String viewId, String clientId) {
            String stateKey = createMVSKey(viewId, clientId);
            Map<String, Object> multiViewStates = getMVSSessionMap();
            if (multiViewStates.remove(stateKey) == null) {
                LOGGER.warning("Multiview state for viewId: '" + viewId + "' and clientId '" + clientId + "' not found");
            }
        }

        /**
         * Gets multiview state bean attached to a component in a specific view.
         *
         * @param viewId viewId of a page
         * @param clientId clientId of a component
         * @param create flag indicating if bean state should be created if does not exist in curent session
         * @param supplier bean state instance
         * @param <T> bean state generic
         *
         * @return multiview state bean attached to a component
         */
        public <T> T get(String viewId, String clientId, boolean create, Supplier<T> supplier) {
            FacesContext fc = getFacesContext();
            Map<String, Object> sessionMap = fc.getExternalContext().getSessionMap();
            Map<String, Object> states =  getMVSSessionMap();
            String stateKey = createMVSKey(viewId, clientId);

            if (states == null) {
                states = new HashMap<>();
                sessionMap.put(Constants.MULTI_VIEW_STATES, states);
            }

            T state = (T) states.get(stateKey);
            if (state == null && create) {
                state = supplier.get();
                states.put(stateKey, state);
            }

            return state;
        }

        private String createMVSKey(String viewId, String clientId) {
            String stateKey = createMVSViewId(viewId);
            if (clientId != null) {
                stateKey += SEPARATOR + clientId;
            }
            return stateKey;
        }

        private String createMVSViewId(String viewId) {
            // LEGACY: The reason to remove the first / is unknown
            return viewId.replaceFirst("^/*", Constants.EMPTY_STRING);
        }

        private Map<String, Object> getMVSSessionMap() {
            FacesContext fc = getFacesContext();
            Map<String, Object> sessionMap = fc.getExternalContext().getSessionMap();
            return (Map) sessionMap.get(Constants.MULTI_VIEW_STATES);
        }
    }
}
