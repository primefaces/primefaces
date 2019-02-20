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

    private static final String CALLBACK_PARAMS_KEY = "CALLBACK_PARAMS";
    private static final String EXECUTE_SCRIPT_KEY = "EXECUTE_SCRIPT";

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
        // currently called once per request - later we might cache the result per request
        // and even the method lookup
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
