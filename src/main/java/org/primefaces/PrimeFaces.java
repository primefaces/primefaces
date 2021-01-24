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
package org.primefaces;

import org.primefaces.component.api.MultiViewStateAware;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.expression.ComponentNotFoundException;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.visit.ResetInputVisitCallback;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.lifecycle.ClientWindow;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PrimeFaces {

    private static final Logger LOGGER = Logger.getLogger(PrimeFaces.class.getName());

    // There are 2 possible solutions
    // 1) the current static solution + use Faces/RequestContext#getCurrentInstance each time
    // 2) make PrimeFaces requestScoped and receive Faces/RequestContext only once (more complex and requires cleanup)
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
     * Checks if the current request is a Faces request.
     *
     * @return <code>true</code> if the current request is a Faces request.
     */
    public boolean isFacesRequest() {
        return getFacesContext() != null;
    }

    /**
     * Shortcut for {@link PartialViewContext#isAjaxRequest()}.
     *
     * @return <code>true</code> if the current request is a AJAX request.
     */
    public boolean isAjaxRequest() {
        FacesContext context = getFacesContext();
        if (context == null) {
            return false;
        }

        return context.getPartialViewContext().isAjaxRequest();
    }

    /**
     * Executes a JavaScript statement before all other PrimeFaces scripts are executed.
     * Useful when you need to do some initialization.
     *
     * @param statement the JavaScript statement.
     */
    public void executeInitScript(String statement) {
        getRequestContext().getInitScriptsToExecute().add(statement);
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
            getFacesContext().getAttributes().put(Constants.DialogFramework.OUTCOME, outcome);
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
            facesContext.getAttributes().put(Constants.DialogFramework.OUTCOME, outcome);

            if (options != null) {
                facesContext.getAttributes().put(Constants.DialogFramework.OPTIONS, options);
            }

            if (params != null) {
                facesContext.getAttributes().put(Constants.DialogFramework.PARAMS, params);
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
            String pfdlgcid = params.get(Constants.DialogFramework.CONVERSATION_PARAM);

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
                    if (facesContext.isProjectStage(ProjectStage.Development)) {
                        LOGGER.log(Level.WARNING,
                                "PrimeFaces.current().ajax().update() called but component can't be resolved!"
                                + " Expression will just be added to the renderIds: {0}", expression);
                    }

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

        /**
         * Updates all the given components.
         *
         * @param components the {@link UIComponent}s.
         */
        public void update(UIComponent... components) {
            if (components == null || components.length == 0) {
                return;
            }

            FacesContext facesContext = getFacesContext();

            for (UIComponent component : components) {
                facesContext.getPartialViewContext().getRenderIds().add(component.getClientId(facesContext));
            }
        }
    }

    public class MultiViewState {

        /**
         * Removes all multiViewState within the current session.
         */
        public void clearAll() {
            clearAll(true, null);
        }

        /**
         * Removes all multiViewState within the current session.
         *
         * @param reset indicates whether or not the component should be reset, if it is in the current view
         */
        public void clearAll(boolean reset) {
            clearAll(reset, null);
        }

        /**
         * Removes all multiViewState within the current session.
         *
         * @param reset indicates whether or not the component should be reset, if it is in the current view
         * @param clientIdConsumer Callback for each removed clientId
         */
        public void clearAll(boolean reset, Consumer<String> clientIdConsumer) {
            if (reset || clientIdConsumer != null) {
                Set<MVSKey> keys = Collections.unmodifiableSet(getMVSKeys());
                clearMVSKeys(keys, reset, clientIdConsumer);
            }

            getFacesContext().getExternalContext().getSessionMap().remove(Constants.MULTI_VIEW_STATES);
        }

        /**
         * Removes all multiViewState in specific view within the current session.
         *
         * @param viewId viewId in which multiview state should be cleared
         * @param reset indicates whether or not the component should be reset, if it is in the current view
         */
        public void clearAll(String viewId, boolean reset) {
            clearAll(viewId, reset, null);
        }

        /**
         * Removes all multiViewState in specific view within the current session.
         *
         * @param viewId viewId in which multiview state should be cleared
         * @param reset indicates whether or not the component should be reset, if it is in the current view
         * @param clientIdConsumer operation to execute for every clientId after multiview state has been cleared
         */
        public void clearAll(String viewId, boolean reset, Consumer<String> clientIdConsumer) {
            Set<MVSKey> keys = getMVSKeys().stream()
                    .filter(k -> Objects.equals(k.viewId, viewId))
                    .collect(Collectors.toSet());
            if (!keys.isEmpty()) {
                clearMVSKeys(keys, reset, clientIdConsumer);
            }
        }

        /**
         * Removes multiViewState of a component in specific view within the current session.
         *
         * @param viewId viewId of a page
         * @param clientId clientId of a component for which multiview state should be cleared
         */
        public void clear(String viewId, String clientId) {
            clear(viewId, clientId, true);
        }

        /**
         * Removes multiViewState of a component in specific view within the current session.
         *
         * @param viewId viewId of a page
         * @param clientId clientId of a component for which multiview state should be cleared, if it is in the current view
         * @param reset indicates whether or not the component should be reset
         */
        public void clear(String viewId, String clientId, boolean reset) {
            MVSKey key = MVSKey.of(viewId, clientId);
            clearMVSKeys(Collections.singleton(key), reset, null);
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
            Map<MVSKey, Object> mvsMap = getMVSStore(create);
            MVSKey mvsKey = MVSKey.of(viewId, clientId);

            T state = (T) mvsMap.get(mvsKey);
            if (state == null && create) {
                state = supplier.get();
                mvsMap.put(mvsKey, state);
            }

            return state;
        }

        private Set<MVSKey> getMVSKeys() {
            return getMVSStore(false).keySet();
        }

        private Map<MVSKey, Object> getMVSStore(boolean create) {
            FacesContext fc = getFacesContext();
            Map<String, Object> sessionMap = fc.getExternalContext().getSessionMap();

            PrimeApplicationContext primeApplicationContext = PrimeApplicationContext.getCurrentInstance(fc);
            String clientWindowId = "session";
            if (primeApplicationContext.getEnvironment().isAtLeastJsf22() &&
                    "client-window".equals(primeApplicationContext.getConfig().getMultiViewStateStore())) {
                ExternalContext externalContext = fc.getExternalContext();
                ClientWindow clientWindow = externalContext.getClientWindow();
                if (clientWindow != null && !LangUtils.isValueBlank(clientWindow.getId())) {
                    clientWindowId = clientWindow.getId();
                }
            }

            Map<String, Map<MVSKey, Object>> clientWindowMap = (Map) sessionMap.get(Constants.MULTI_VIEW_STATES);
            if (clientWindowMap == null) {
                clientWindowMap = new ConcurrentHashMap<>();
                sessionMap.put(Constants.MULTI_VIEW_STATES, clientWindowMap);
            }

            Map<MVSKey, Object> mvsMap = clientWindowMap.get(clientWindowId);

            if (mvsMap == null) {
                if (create) {
                    mvsMap = new ConcurrentHashMap<>();
                    clientWindowMap.put(clientWindowId, mvsMap);
                }
                else {
                    mvsMap = Collections.emptyMap();
                }
            }

            return mvsMap;
        }

        private void reset(String clientId) {
            FacesContext context = getFacesContext();
            context.getViewRoot().invokeOnComponent(context, clientId, (fc, component) -> {
                if (!(component instanceof MultiViewStateAware)) {
                    throw new FacesException("Multi view state not supported for: " + component.getClass().getSimpleName());
                }
                ((MultiViewStateAware) component).resetMultiViewState();
            });
        }

        private void clearMVSKeys(Set<MVSKey> keysToRemove, boolean reset, Consumer<String> clientIdConsumer) {
            Set<MVSKey> mvsKeys = getMVSKeys();
            for (MVSKey mvsKey : keysToRemove) {
                if (!mvsKeys.remove(mvsKey)) {
                    LOGGER.log(Level.WARNING,
                            "Multiview state for viewId: \"{0}\" and clientId \"{1}\" not found",
                            new Object[]{mvsKey.viewId, mvsKey.clientId});
                    continue;
                }

                if (reset) {
                    reset(mvsKey.clientId);
                }

                if (clientIdConsumer != null) {
                    clientIdConsumer.accept(mvsKey.clientId);
                }
            }
        }
    }

    private static class MVSKey implements Serializable {

        private static final long serialVersionUID = 1L;

        private String viewId;
        private String clientId;

        // serialization
        private MVSKey() {
            // NOOP
        }

        private MVSKey(String viewId, String clientId) {
            this.viewId = viewId;
            this.clientId = clientId;
        }

        public static MVSKey of(String viewId, String clientId) {
            return new MVSKey(viewId, clientId);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + Objects.hashCode(viewId);
            hash = 23 * hash + Objects.hashCode(clientId);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            MVSKey other = (MVSKey) obj;
            return Objects.equals(viewId, other.viewId) &&
                    Objects.equals(clientId, other.clientId);
        }

        @Override
        public String toString() {
            return "MVSKey{" +
                    "viewId='" + viewId + '\'' +
                    ", clientId='" + clientId + '\'' +
                    '}';
        }
    }
}
