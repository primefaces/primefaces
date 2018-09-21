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
package org.primefaces.config;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.util.Constants;

/**
 * Container for all config parameters.
 */
public class PrimeConfiguration {

    // context params
    private boolean validateEmptyFields = false;
    private boolean partialSubmitEnabled = false;
    private boolean resetValuesEnabled = false;
    private boolean interpretEmptyStringAsNull = false;
    private String theme = null;
    private boolean fontAwesomeEnabled = false;
    private boolean clientSideValidationEnabled = false;
    private String uploader = null;
    private boolean transformMetadataEnabled = false;
    private boolean legacyWidgetNamespace = false;
    private boolean interpolateClientSideValidationMessages = false;
    private boolean earlyPostParamEvaluation = false;
    private boolean moveScriptsToBottom = false;

    // internal config
    private boolean stringConverterAvailable = false;

    private boolean beanValidationEnabled = false;

    // web.xml
    private Map<String, String> errorPages = null;

    protected PrimeConfiguration() {

    }

    public PrimeConfiguration(FacesContext context, PrimeEnvironment environment) {
        initConfigFromContextParams(context, environment);
        initInternalConfig(context);
        initConfigFromWebXml(context);
        initValidateEmptyFields(context, environment);
    }

    protected void initInternalConfig(FacesContext context) {
        stringConverterAvailable = null != context.getApplication().createConverter(String.class);
    }

    protected void initConfigFromContextParams(FacesContext context, PrimeEnvironment environment) {
        ExternalContext externalContext = context.getExternalContext();

        String value = null;

        value = externalContext.getInitParameter(Constants.ContextParams.INTERPRET_EMPTY_STRING_AS_NULL);
        interpretEmptyStringAsNull = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.SUBMIT);
        partialSubmitEnabled = (value == null) ? false : value.equalsIgnoreCase("partial");

        value = externalContext.getInitParameter(Constants.ContextParams.RESET_VALUES);
        resetValuesEnabled = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.PFV_KEY);
        clientSideValidationEnabled = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.UPLOADER);
        uploader = (value == null) ? "auto" : value;

        theme = externalContext.getInitParameter(Constants.ContextParams.THEME);

        value = externalContext.getInitParameter(Constants.ContextParams.FONT_AWESOME);
        fontAwesomeEnabled = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.TRANSFORM_METADATA);
        transformMetadataEnabled = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.LEGACY_WIDGET_NAMESPACE);
        legacyWidgetNamespace = (value == null) ? false : Boolean.valueOf(value);

        if (environment.isBeanValidationAvailable()) {
            value = externalContext.getInitParameter(Constants.ContextParams.BEAN_VALIDATION_DISABLED);
            beanValidationEnabled = (value == null) ? true : !Boolean.valueOf(value);
        }

        value = externalContext.getInitParameter(Constants.ContextParams.INTERPOLATE_CLIENT_SIDE_VALIDATION_MESSAGES);
        interpolateClientSideValidationMessages = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.EARLY_POST_PARAM_EVALUATION);
        earlyPostParamEvaluation = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.MOVE_SCRIPTS_TO_BOTTOM);
        moveScriptsToBottom = (value == null) ? false : Boolean.valueOf(value);
    }

    protected void initValidateEmptyFields(FacesContext context, PrimeEnvironment environment) {
        ExternalContext externalContext = context.getExternalContext();

        String param = externalContext.getInitParameter(UIInput.VALIDATE_EMPTY_FIELDS_PARAM_NAME);

        if (param == null && externalContext.getApplicationMap().containsKey(UIInput.VALIDATE_EMPTY_FIELDS_PARAM_NAME)) {
            Object applicationMapValue = externalContext.getApplicationMap().get(UIInput.VALIDATE_EMPTY_FIELDS_PARAM_NAME);
            if (applicationMapValue instanceof String) {
                param = (String) applicationMapValue;
            }
            else if (applicationMapValue instanceof Boolean) {
                validateEmptyFields = (Boolean) applicationMapValue;
                // already initialized - skip further processing
                return;
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

        validateEmptyFields = (param.equals("auto") && environment.isBeanValidationAvailable()) || param.equals("true");
    }

    protected void initConfigFromWebXml(FacesContext context) {
        errorPages = WebXmlParser.getErrorPages(context);
        if (errorPages == null) {
            errorPages = new HashMap<String, String>();
        }
    }

    public boolean isValidateEmptyFields() {
        return validateEmptyFields;
    }

    public void setValidateEmptyFields(boolean validateEmptyFields) {
        this.validateEmptyFields = validateEmptyFields;
    }

    public boolean isPartialSubmitEnabled() {
        return partialSubmitEnabled;
    }

    public void setPartialSubmitEnabled(boolean partialSubmitEnabled) {
        this.partialSubmitEnabled = partialSubmitEnabled;
    }

    public boolean isResetValuesEnabled() {
        return resetValuesEnabled;
    }

    public void setResetValuesEnabled(boolean resetValuesEnabled) {
        this.resetValuesEnabled = resetValuesEnabled;
    }

    public boolean isInterpretEmptyStringAsNull() {
        return interpretEmptyStringAsNull;
    }

    public void setInterpretEmptyStringAsNull(boolean interpretEmptyStringAsNull) {
        this.interpretEmptyStringAsNull = interpretEmptyStringAsNull;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isFontAwesomeEnabled() {
        return fontAwesomeEnabled;
    }

    public void setFontAwesomeEnabled(boolean fontAwesomeEnabled) {
        this.fontAwesomeEnabled = fontAwesomeEnabled;
    }

    public boolean isClientSideValidationEnabled() {
        return clientSideValidationEnabled;
    }

    public void setClientSideValidationEnabled(boolean clientSideValidationEnabled) {
        this.clientSideValidationEnabled = clientSideValidationEnabled;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public boolean isTransformMetadataEnabled() {
        return transformMetadataEnabled;
    }

    public void setTransformMetadataEnabled(boolean transformMetadataEnabled) {
        this.transformMetadataEnabled = transformMetadataEnabled;
    }

    public boolean isLegacyWidgetNamespace() {
        return legacyWidgetNamespace;
    }

    public void setLegacyWidgetNamespace(boolean legacyWidgetNamespace) {
        this.legacyWidgetNamespace = legacyWidgetNamespace;
    }

    public boolean isInterpolateClientSideValidationMessages() {
        return interpolateClientSideValidationMessages;
    }

    public void setInterpolateClientSideValidationMessages(boolean interpolateClientSideValidationMessages) {
        this.interpolateClientSideValidationMessages = interpolateClientSideValidationMessages;
    }

    public boolean isEarlyPostParamEvaluation() {
        return earlyPostParamEvaluation;
    }

    public void setEarlyPostParamEvaluation(boolean earlyPostParamEvaluation) {
        this.earlyPostParamEvaluation = earlyPostParamEvaluation;
    }

    public boolean isMoveScriptsToBottom() {
        return moveScriptsToBottom;
    }

    public void setMoveScriptsToBottom(boolean moveScriptsToBottom) {
        this.moveScriptsToBottom = moveScriptsToBottom;
    }

    public boolean isStringConverterAvailable() {
        return stringConverterAvailable;
    }

    public void setStringConverterAvailable(boolean stringConverterAvailable) {
        this.stringConverterAvailable = stringConverterAvailable;
    }

    public boolean isBeanValidationEnabled() {
        return beanValidationEnabled;
    }

    public void setBeanValidationEnabled(boolean beanValidationEnabled) {
        this.beanValidationEnabled = beanValidationEnabled;
    }

    public Map<String, String> getErrorPages() {
        return errorPages;
    }

    public void setErrorPages(Map<String, String> errorPages) {
        this.errorPages = errorPages;
    }
}
