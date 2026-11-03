/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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

import org.primefaces.cdk.api.Function;
import org.primefaces.util.AjaxRequestBuilder;
import org.primefaces.util.CSVBuilder;
import org.primefaces.util.Constants;
import org.primefaces.util.StyleBuilder;
import org.primefaces.util.StyleClassBuilder;
import org.primefaces.util.WidgetBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

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

    // the name of a theme library primefaces-<name>: no path, no ".."
    private static final Pattern THEME_NAME = Pattern.compile("[\\w-]+(\\.[\\w-]+)*");
    private static final String CALLBACK_PARAMS_KEY = "CALLBACK_PARAMS";
    private static final String EXECUTE_INIT_SCRIPTS_KEY = "EXECUTE_INIT_SCRIPTS";
    private static final String EXECUTE_SCRIPTS_KEY = "EXECUTE_SCRIPTS";
    private static final Class<?>[] EMPTY_PARAMS = new Class<?>[0];

    private WidgetBuilder widgetBuilder;
    private StyleClassBuilder styleClassBuilder;
    private StyleBuilder styleBuilder;
    private AjaxRequestBuilder ajaxRequestBuilder;
    private CSVBuilder csvBuilder;
    private FacesContext context;
    private PrimeApplicationContext applicationContext;
    private Boolean ignoreAutoUpdate;
    private Boolean rtl;
    private String theme;
    private Boolean touchable;
    private Boolean secure;
    private Boolean flex;
    private Boolean hideResourceVersion;

    public PrimeRequestContext(FacesContext context) {
        this.context = context;
    }

    public static PrimeRequestContext getCurrentInstance() {
        return getCurrentInstance(FacesContext.getCurrentInstance());
    }

    public static PrimeRequestContext getCurrentInstance(FacesContext facesContext) {
        if (facesContext == null) {
            throw new IllegalArgumentException("FacesContext must not be null.");
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
     * @return all scripts added in the current request and called first before other scripts are executed.
     */
    @SuppressWarnings("unchecked")
    public List<String> getInitScriptsToExecute() {
        List<String> initScriptsToExecute =
            (List<String>) context.getAttributes().get(EXECUTE_INIT_SCRIPTS_KEY);

        if (initScriptsToExecute == null) {
            initScriptsToExecute = new ArrayList<>(5);
            context.getAttributes().put(EXECUTE_INIT_SCRIPTS_KEY, initScriptsToExecute);
        }

        return initScriptsToExecute;
    }

    /**
     * @return all scripts added in the current request.
     */
    @SuppressWarnings("unchecked")
    public List<String> getScriptsToExecute() {
        List<String> scriptsToExecute =
            (List<String>) context.getAttributes().get(EXECUTE_SCRIPTS_KEY);

        if (scriptsToExecute == null) {
            scriptsToExecute = new ArrayList<>();
            context.getAttributes().put(EXECUTE_SCRIPTS_KEY, scriptsToExecute);
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
     *  @return Shared StyleClassBuilder instance of the current request
     */
    public StyleClassBuilder getStyleClassBuilder() {
        if (styleClassBuilder == null) {
            styleClassBuilder = new StyleClassBuilder(context);
        }

        return styleClassBuilder;
    }

    /**
     *  @return Shared StyleBuilder instance of the current request
     */
    public StyleBuilder getStyleBuilder() {
        if (styleBuilder == null) {
            styleBuilder = new StyleBuilder(context);
        }

        return styleBuilder;
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
        if (secure == null) {
            Object request = context.getExternalContext().getRequest();

            if (request instanceof HttpServletRequest servletRequest) {
                secure = servletRequest.isSecure();
            }
            else {
                try {
                    Method method = request.getClass().getDeclaredMethod("isSecure", EMPTY_PARAMS);
                    secure = (Boolean) method.invoke(request, (Object[]) null);
                }
                catch (Exception e) {
                    secure = false;
                }
            }
        }

        return secure;
    }

    /**
     * @return <code>true</code> if auto-updatable components should not be updated automatically in this request.
     */
    public boolean isIgnoreAutoUpdate() {
        if (ignoreAutoUpdate == null) {
            Object ignoreAutoUpdateObject = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.IGNORE_AUTO_UPDATE_PARAM);
            ignoreAutoUpdate = "true".equals(ignoreAutoUpdateObject);
        }

        return ignoreAutoUpdate;
    }

    /**
     * Overrides eventually set ignoreAutoUpdate from client.
     * May be used e.g. combined with server-side update via <code>PrimeFaces.current().ajax().update("xxx")</code>
     * @param ignoreAutoUpdate
     */
    public void setIgnoreAutoUpdate(Boolean ignoreAutoUpdate) {
        this.ignoreAutoUpdate = ignoreAutoUpdate;
    }

    /**
     * @return the preset of this request: the one chosen by the user (cookie {@value Constants.Cookies#THEME}, only with
     * {@code primefaces.THEME_CONFIGURATOR}) if it exists, otherwise {@code primefaces.THEME} or aura; blank for none.
     */
    public String getTheme() {
        if (theme == null) {
            String chosen = getThemeConfiguratorCookie(Constants.Cookies.THEME);
            if (chosen != null && THEME_NAME.matcher(chosen).matches()
                    && context.getApplication().getResourceHandler().createResource("theme.css", "primefaces-" + chosen) != null) {
                theme = chosen;
            }
            else {
                String param = getApplicationContext().getConfig().getTheme();
                if (param == null) {
                    theme = "aura";
                }
                else {
                    String expressionValue = context.getApplication().evaluateExpressionGet(context, param, String.class);
                    theme = expressionValue == null ? "" : expressionValue.trim();
                }
            }
        }

        return theme;
    }

    /**
     * @return the preset of the current request, see {@link #getTheme()}
     */
    @Function(name = "theme", description = "Gets the preset of the current request: the one chosen by the user "
            + "(primefaces.THEME_CONFIGURATOR) or primefaces.THEME.")
    public static String currentTheme() {
        return getCurrentInstance().getTheme();
    }

    /**
     * @return the direction of the current request, ltr or rtl, see {@link #isRTL()}
     */
    @Function(name = "dir", description = "Gets the direction of the current request, ltr or rtl: the one chosen by the user "
            + "(primefaces.THEME_CONFIGURATOR) or primefaces.DIR.")
    public static String currentDir() {
        return getCurrentInstance().isRTL() ? "rtl" : "ltr";
    }

    public boolean isRTL() {
        if (rtl == null) {
            String chosen = getThemeConfiguratorCookie(Constants.Cookies.DIR);
            if ("ltr".equals(chosen) || "rtl".equals(chosen)) {
                rtl = "rtl".equals(chosen);
            }
            else {
                String param = context.getExternalContext().getInitParameter(Constants.ContextParams.DIRECTION);
                if (param == null) {
                    rtl = false;
                }
                else {
                    String expressionValue = context.getApplication().evaluateExpressionGet(context, param, String.class);

                    rtl = "rtl".equalsIgnoreCase(expressionValue);
                }
            }
        }

        return rtl;
    }

    public boolean isTouchable() {
        if (touchable == null) {
            touchable = evalBooleanExpression(Constants.ContextParams.TOUCHABLE, true);
        }

        return touchable;
    }

    public boolean isFlex() {
        if (flex == null) {
            flex = evalBooleanExpression(Constants.ContextParams.FLEX, false);
        }

        return flex;
    }

    public boolean isHideResourceVersion() {
        if (hideResourceVersion == null) {
            hideResourceVersion = evalBooleanExpression(Constants.ContextParams.HIDE_RESOURCE_VERSION, false);
        }

        return hideResourceVersion;
    }

    private String getThemeConfiguratorCookie(String name) {
        if (!getApplicationContext().getConfig().isThemeConfiguratorEnabled()) {
            return null;
        }
        Object cookie = context.getExternalContext().getRequestCookieMap().get(name);
        return cookie instanceof Cookie servletCookie ? servletCookie.getValue() : null;
    }

    private boolean evalBooleanExpression(String param, boolean defaultValueIfNull) {
        String paramValue = context.getExternalContext().getInitParameter(param);
        if (paramValue == null) {
            return defaultValueIfNull;
        }
        else {
            String expressionValue = context.getApplication().evaluateExpressionGet(context, paramValue, String.class);
            return expressionValue == null || Boolean.parseBoolean(expressionValue);
        }
    }
}
