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
package org.primefaces.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.util.AjaxRequestBuilder;
import org.primefaces.util.CSVBuilder;
import org.primefaces.util.Constants;
import org.primefaces.util.WidgetBuilder;

/**
 * A {@link PrimeRequestContext} is a contextual store for the current request.
 *
 * It can be accessed via:
 * <blockquote>
 * PrimeRequestContext.getCurrentInstance(context)
 * </blockquote>
 */
public class PrimeRequestContext {

    public static final String INSTANCE_KEY = PrimeRequestContext.class.getName();

    private final static String CALLBACK_PARAMS_KEY = "CALLBACK_PARAMS";
    private final static String EXECUTE_SCRIPT_KEY = "EXECUTE_SCRIPT";

    private WidgetBuilder widgetBuilder;
    private AjaxRequestBuilder ajaxRequestBuilder;
    private CSVBuilder csvBuilder;
    private FacesContext context;
    private PrimeApplicationContext applicationContext;
    private Boolean ignoreAutoUpdate;
    private Boolean rtl;

    public PrimeRequestContext(FacesContext context) {
        this.context = context;
    }

    public static PrimeRequestContext getCurrentInstance() {
        return getCurrentInstance(FacesContext.getCurrentInstance());
    }

    public static PrimeRequestContext getCurrentInstance(FacesContext facesContext) {
        if (facesContext == null) {
            return null;
        }

        PrimeRequestContext context = (PrimeRequestContext) facesContext.getAttributes().get(INSTANCE_KEY);

        if (context == null) {
            context = new PrimeRequestContext(facesContext);
            setCurrentInstance(context, facesContext);
        }

        return context;
    }

    public static void setCurrentInstance(PrimeRequestContext context, FacesContext facesContext) {
        if (context == null) {
            if (facesContext != null) {
                facesContext.getAttributes().remove(INSTANCE_KEY);
            }
        }
        else {
            facesContext.getAttributes().put(INSTANCE_KEY, context);
        }
    }

    /**
     * @return all callback parameters added in the current request.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCallbackParams() {
        Map<String, Object> callbackParams =
            (Map<String, Object>) context.getAttributes().get(CALLBACK_PARAMS_KEY);

        if (callbackParams == null) {
            callbackParams = new HashMap<>();
            context.getAttributes().put(CALLBACK_PARAMS_KEY, callbackParams);
        }

        return callbackParams;
    }

    /**
     * @return all scripts added in the current request.
     */
    @SuppressWarnings("unchecked")
    public List<String> getScriptsToExecute() {
        List<String> scriptsToExecute =
            (List<String>) context.getAttributes().get(EXECUTE_SCRIPT_KEY);

        if (scriptsToExecute == null) {
            scriptsToExecute = new ArrayList<>();
            context.getAttributes().put(EXECUTE_SCRIPT_KEY, scriptsToExecute);
        }

        return scriptsToExecute;
    }

    /**
     * @return Shared WidgetBuilder instance of the current request
     */
    public WidgetBuilder getWidgetBuilder() {
        if (this.widgetBuilder == null) {
            this.widgetBuilder = new WidgetBuilder(context, getApplicationContext().getConfig());
        }

        return widgetBuilder;
    }

    /**
     * @return Shared AjaxRequestBuilder instance of the current request
     */
    public AjaxRequestBuilder getAjaxRequestBuilder() {
        if (this.ajaxRequestBuilder == null) {
            this.ajaxRequestBuilder = new AjaxRequestBuilder(context);
        }

        return ajaxRequestBuilder;
    }

    /**
     * @return Shared Client Side Validation builder instance of the current request
     */
    public CSVBuilder getCSVBuilder() {
        if (this.csvBuilder == null) {
            this.csvBuilder = new CSVBuilder(context);
        }

        return csvBuilder;
    }

    /**
     * @return ApplicationContext instance.
     */
    public PrimeApplicationContext getApplicationContext() {
        if (this.applicationContext == null) {
            this.applicationContext = PrimeApplicationContext.getCurrentInstance(context);
        }

        return applicationContext;
    }

    /**
     * Clear resources.
     */
    public void release() {
        widgetBuilder = null;
        ajaxRequestBuilder = null;
        context = null;
        applicationContext = null;
    }

    /**
     * Returns a boolean indicating whether this request was made using a secure channel, such as HTTPS.
     *
     * @return if secure or not.
     */
    public boolean isSecure() {
        Object request = context.getExternalContext().getRequest();

        if (request instanceof HttpServletRequest) {
            return ((HttpServletRequest) request).isSecure();
        }
        else {
            try {
                Method method = request.getClass().getDeclaredMethod("isSecure", new Class[0]);
                return (Boolean) method.invoke(request, (Object[]) null);
            }
            catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * @return <code>true</code> if {@link AutoUpdatable} components should not be updated automatically in this request.
     */
    public boolean isIgnoreAutoUpdate() {
        if (ignoreAutoUpdate == null) {
            Object ignoreAutoUpdateObject = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.IGNORE_AUTO_UPDATE_PARAM);
            ignoreAutoUpdate = (null != ignoreAutoUpdateObject && "true".equals(ignoreAutoUpdateObject));
        }

        return ignoreAutoUpdate;
    }

    public boolean isRTL() {
        if (rtl == null) {
            String param = context.getExternalContext().getInitParameter(Constants.ContextParams.DIRECTION);
            if (param == null) {
                rtl = false;
            }
            else {
                ELContext elContext = context.getELContext();
                ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
                ValueExpression expression = expressionFactory.createValueExpression(elContext, param, String.class);
                String expressionValue = (String) expression.getValue(elContext);

                rtl = (expressionValue == null) ? false : expressionValue.equalsIgnoreCase("rtl");
            }
        }

        return rtl;
    }
}
