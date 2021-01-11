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
package org.primefaces.config;

import java.util.Map;

import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

/**
 * Container for all config parameters.
 */
public class PrimeConfiguration {

    // context params
    private final boolean validateEmptyFields;
    private final boolean partialSubmitEnabled;
    private final boolean resetValuesEnabled;
    private final boolean interpretEmptyStringAsNull;
    private final String theme;
    private final boolean fontAwesomeEnabled;
    private final boolean clientSideValidationEnabled;
    private final String uploader;
    private final boolean transformMetadataEnabled;
    private final boolean legacyWidgetNamespace;
    private final boolean interpolateClientSideValidationMessages;
    private final boolean earlyPostParamEvaluation;
    private final boolean moveScriptsToBottom;
    private boolean csp;
    private String cspPolicy;
    private String[] exceptionTypesToIgnoreInLogging;
    private final String multiViewStateStore;
    private final boolean markInputAsInvalidOnErrorMsg;

    // internal config
    private final boolean stringConverterAvailable;

    private final boolean beanValidationEnabled;

    // web.xml
    private final Map<String, String> errorPages;

    private boolean cookiesSecure;
    private String cookiesSameSite;

    public PrimeConfiguration(FacesContext context, PrimeEnvironment environment) {
        ExternalContext externalContext = context.getExternalContext();

        stringConverterAvailable = null != context.getApplication().createConverter(String.class);

        errorPages = WebXmlParser.getErrorPages(context);

        validateEmptyFields = resolveValidateEmptyFields(context, environment);


        // parse context params
        String value = externalContext.getInitParameter(Constants.ContextParams.INTERPRET_EMPTY_STRING_AS_NULL);
        interpretEmptyStringAsNull = Boolean.parseBoolean(value);

        value = externalContext.getInitParameter(Constants.ContextParams.SUBMIT);
        partialSubmitEnabled = "partial".equalsIgnoreCase(value);

        value = externalContext.getInitParameter(Constants.ContextParams.RESET_VALUES);
        resetValuesEnabled = Boolean.parseBoolean(value);

        value = externalContext.getInitParameter(Constants.ContextParams.PFV_KEY);
        clientSideValidationEnabled = Boolean.parseBoolean(value);

        value = externalContext.getInitParameter(Constants.ContextParams.UPLOADER);
        uploader = (value == null) ? "auto" : value;

        theme = externalContext.getInitParameter(Constants.ContextParams.THEME);

        value = externalContext.getInitParameter(Constants.ContextParams.FONT_AWESOME);
        fontAwesomeEnabled = Boolean.parseBoolean(value);

        value = externalContext.getInitParameter(Constants.ContextParams.TRANSFORM_METADATA);
        transformMetadataEnabled = Boolean.parseBoolean(value);

        value = externalContext.getInitParameter(Constants.ContextParams.LEGACY_WIDGET_NAMESPACE);
        legacyWidgetNamespace = Boolean.parseBoolean(value);

        value = externalContext.getInitParameter(Constants.ContextParams.BEAN_VALIDATION_DISABLED);
        beanValidationEnabled = environment.isBeanValidationAvailable() && !Boolean.parseBoolean(value);

        value = externalContext.getInitParameter(Constants.ContextParams.INTERPOLATE_CLIENT_SIDE_VALIDATION_MESSAGES);
        interpolateClientSideValidationMessages = Boolean.parseBoolean(value);

        value = externalContext.getInitParameter(Constants.ContextParams.EARLY_POST_PARAM_EVALUATION);
        earlyPostParamEvaluation = Boolean.parseBoolean(value);

        value = externalContext.getInitParameter(Constants.ContextParams.MOVE_SCRIPTS_TO_BOTTOM);
        moveScriptsToBottom = Boolean.parseBoolean(value);

        value = externalContext.getInitParameter(Constants.ContextParams.CSP);
        csp = Boolean.parseBoolean(value);
        if (csp) {
            cspPolicy = externalContext.getInitParameter(Constants.ContextParams.CSP_POLICY);
        }

        value = externalContext.getInitParameter(Constants.ContextParams.EXCEPTION_TYPES_TO_IGNORE_IN_LOGGING);
        if (LangUtils.isValueBlank(value)) {
            exceptionTypesToIgnoreInLogging = new String[0];
        }
        else {
            exceptionTypesToIgnoreInLogging = value.split(",");
        }

        multiViewStateStore = externalContext.getInitParameter(Constants.ContextParams.MULTI_VIEW_STATE_STORE);

        value = externalContext.getInitParameter(Constants.ContextParams.MARK_INPUT_AS_INVALID_ON_ERROR_MSG);
        markInputAsInvalidOnErrorMsg = Boolean.parseBoolean(value);

        cookiesSameSite = externalContext.getInitParameter(Constants.ContextParams.COOKIES_SAME_SITE);

        cookiesSecure = true;
        if (externalContext.getContext() instanceof ServletContext) {
            ServletContext se = (ServletContext) externalContext.getContext();
            if (environment.isAtLeastServlet30()) {
                cookiesSecure = se.getSessionCookieConfig().isSecure();
            }
        }
    }

    protected boolean resolveValidateEmptyFields(FacesContext context, PrimeEnvironment environment) {
        ExternalContext externalContext = context.getExternalContext();

        String param = externalContext.getInitParameter(UIInput.VALIDATE_EMPTY_FIELDS_PARAM_NAME);

        if (param == null && externalContext.getApplicationMap().containsKey(UIInput.VALIDATE_EMPTY_FIELDS_PARAM_NAME)) {
            Object applicationMapValue = externalContext.getApplicationMap().get(UIInput.VALIDATE_EMPTY_FIELDS_PARAM_NAME);
            if (applicationMapValue instanceof String) {
                param = (String) applicationMapValue;
            }
            else if (applicationMapValue instanceof Boolean) {
                return (Boolean) applicationMapValue;
            }
        }

        if (param == null) {
            // null means the same as auto.
            param = "auto";
        }
        else {
            // The environment variables are case insensitive.
            param = param.toLowerCase();
        }

        return ("auto".equals(param) && environment.isBeanValidationAvailable()) || "true".equals(param);
    }

    public boolean isValidateEmptyFields() {
        return validateEmptyFields;
    }

    public boolean isPartialSubmitEnabled() {
        return partialSubmitEnabled;
    }

    public boolean isResetValuesEnabled() {
        return resetValuesEnabled;
    }

    public boolean isInterpretEmptyStringAsNull() {
        return interpretEmptyStringAsNull;
    }

    public String getTheme() {
        return theme;
    }

    public boolean isFontAwesomeEnabled() {
        return fontAwesomeEnabled;
    }

    public boolean isClientSideValidationEnabled() {
        return clientSideValidationEnabled;
    }

    public String getUploader() {
        return uploader;
    }

    public boolean isTransformMetadataEnabled() {
        return transformMetadataEnabled;
    }

    public boolean isLegacyWidgetNamespace() {
        return legacyWidgetNamespace;
    }

    public boolean isInterpolateClientSideValidationMessages() {
        return interpolateClientSideValidationMessages;
    }

    public boolean isEarlyPostParamEvaluation() {
        return earlyPostParamEvaluation;
    }

    public boolean isMoveScriptsToBottom() {
        return moveScriptsToBottom;
    }

    public boolean isStringConverterAvailable() {
        return stringConverterAvailable;
    }

    public boolean isBeanValidationEnabled() {
        return beanValidationEnabled;
    }

    public Map<String, String> getErrorPages() {
        return errorPages;
    }

    public boolean isCsp() {
        return csp;
    }

    public String getCspPolicy() {
        return cspPolicy;
    }

    public String[] getExceptionTypesToIgnoreInLogging() {
        return exceptionTypesToIgnoreInLogging;
    }

    public String getMultiViewStateStore() {
        return multiViewStateStore;
    }

    public boolean isMarkInputAsInvalidOnErrorMsg() {
        return markInputAsInvalidOnErrorMsg;
    }

    public boolean isCookiesSecure() {
        return cookiesSecure;
    }

    public String getCookiesSameSite() {
        return cookiesSameSite;
    }
}
