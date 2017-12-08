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
package org.primefaces.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.validation.Validation;
import org.primefaces.util.ClassUtils;

import org.primefaces.util.Constants;

/**
 * Container for all config parameters.
 */
public class PrimeConfiguration {

    private static final Logger LOG = Logger.getLogger(PrimeConfiguration.class.getName());

    // context params
    private boolean validateEmptyFields = false;
    private boolean partialSubmitEnabled = false;
    private boolean resetValuesEnabled = false;
    private boolean interpretEmptyStringAsNull = false;
    private String secretKey = null;
    private String pushServerURL = null;
    private String theme = null;
    private String mobileTheme = null;
    private boolean fontAwesomeEnabled = false;
    private boolean clientSideValidationEnabled = false;
    private String uploader = null;
    private boolean transformMetadataEnabled = false;
    private boolean legacyWidgetNamespace = false;
    private boolean beanValidationDisabled = false;
    private boolean interpolateClientSideValidationMessages = false;
    private boolean earlyPostParamEvaluation = false;
    private boolean collectScripts = false;

    // environment config
    private boolean beanValidationAvailable = false;
    private boolean atLeastEl22 = false;
    private boolean atLeastJsf23 = false;
    private boolean atLeastJsf22 = false;
    private boolean atLeastJsf21 = false;
    private boolean atLeastBv11 = false;

    // internal config
    private boolean stringConverterAvailable = false;
    
    // build properties
    private String buildVersion = null;

    // web.xml
    private Map<String, String> errorPages = null;

    protected PrimeConfiguration() {

    }

    public PrimeConfiguration(FacesContext context) {
        initConfigFromContextParams(context);
        initEnvironmentConfig();
        initInternalConfig(context);
        initBuildProperties();
        initConfigFromWebXml(context);
        initValidateEmptyFields(context);
    }

    protected void initEnvironmentConfig() {
        atLeastEl22 = ClassUtils.tryToLoadClassForName("javax.el.ValueReference") != null;

        atLeastJsf23 = ClassUtils.tryToLoadClassForName("javax.faces.component.UIImportConstants") != null;
        atLeastJsf22 = ClassUtils.tryToLoadClassForName("javax.faces.flow.Flow") != null;
        atLeastJsf21 = ClassUtils.tryToLoadClassForName("javax.faces.component.TransientStateHolder") != null;

        atLeastBv11 = ClassUtils.tryToLoadClassForName("javax.validation.executable.ExecutableValidator") != null;
    }
    
    protected void initInternalConfig(FacesContext context) {
        stringConverterAvailable = null != context.getApplication().createConverter(String.class);
        beanValidationAvailable = checkIfBeanValidationIsAvailable();
    }

    protected void initConfigFromContextParams(FacesContext context) {
        ExternalContext externalContext = context.getExternalContext();

        String value = null;

        value = externalContext.getInitParameter(Constants.ContextParams.INTERPRET_EMPTY_STRING_AS_NULL);
        interpretEmptyStringAsNull = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.SUBMIT);
        partialSubmitEnabled = (value == null) ? false : value.equalsIgnoreCase("partial");

        value = externalContext.getInitParameter(Constants.ContextParams.RESET_VALUES);
        resetValuesEnabled = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.SECRET_KEY);
        secretKey = (value == null) ? "primefaces" : value;

        value = externalContext.getInitParameter(Constants.ContextParams.PFV_KEY);
        clientSideValidationEnabled = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.UPLOADER);
        uploader = (value == null) ? "auto" : value;

        pushServerURL = externalContext.getInitParameter(Constants.ContextParams.PUSH_SERVER_URL);

        theme = externalContext.getInitParameter(Constants.ContextParams.THEME);

        mobileTheme = externalContext.getInitParameter(Constants.ContextParams.MOBILE_THEME);

        value = externalContext.getInitParameter(Constants.ContextParams.FONT_AWESOME);
        fontAwesomeEnabled = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.TRANSFORM_METADATA);
        transformMetadataEnabled = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.LEGACY_WIDGET_NAMESPACE);
        legacyWidgetNamespace = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.BEAN_VALIDATION_DISABLED);
        beanValidationDisabled = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.INTERPOLATE_CLIENT_SIDE_VALIDATION_MESSAGES);
        interpolateClientSideValidationMessages = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.EARLY_POST_PARAM_EVALUATION);
        earlyPostParamEvaluation = (value == null) ? false : Boolean.valueOf(value);
        
        value = externalContext.getInitParameter(Constants.ContextParams.COLLECT_SCRIPTS);
        collectScripts = (value == null) ? false : Boolean.valueOf(value);
    }

    protected void initValidateEmptyFields(FacesContext context) {
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

        validateEmptyFields = (param.equals("auto") && beanValidationAvailable) || param.equals("true");
    }

    protected void initBuildProperties() {

        Properties buildProperties = new Properties();
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream("/META-INF/maven/org.primefaces/primefaces/pom.properties");
            buildProperties.load(is);
            buildVersion = buildProperties.getProperty("version");
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Could not load pom.properties", e);
        }

        if (is != null) {
            try {
                is.close();
            }
            catch (IOException e) {
            }
        }
    }

    private boolean checkIfBeanValidationIsAvailable() {
        boolean available = ClassUtils.tryToLoadClassForName("javax.validation.Validation") != null;

        if (available) {
            // Trial-error approach to check for Bean Validation impl existence.
            // If any Exception occurs here, we assume that Bean Validation is not available.
            // The cause may be anything, i.e. NoClassDef, config error...
            try {
                Validation.buildDefaultValidatorFactory().getValidator();
            }
            catch (Throwable t) {
                LOG.log(Level.FINE, "BV not available - Could not build default ValidatorFactory.");
                available = false;
            }
        }

        return available && !beanValidationDisabled && atLeastEl22;
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

    public boolean isBeanValidationAvailable() {
        return beanValidationAvailable;
    }

    public boolean isAtLeastEL22() {
        return atLeastEl22;
    }

    public boolean isPartialSubmitEnabled() {
        return partialSubmitEnabled;
    }

    public boolean isInterpretEmptyStringAsNull() {
        return interpretEmptyStringAsNull;
    }

    public boolean isStringConverterAvailable() {
        return stringConverterAvailable;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public boolean isAtLeastJSF23() {
        return atLeastJsf23;
    }

    public boolean isAtLeastJSF22() {
        return atLeastJsf22;
    }

    public boolean isAtLeastJSF21() {
        return atLeastJsf21;
    }

    public boolean isResetValuesEnabled() {
        return resetValuesEnabled;
    }

    public boolean isClientSideValidationEnabled() {
        return clientSideValidationEnabled;
    }

    public String getUploader() {
        return uploader;
    }

    public String getPushServerURL() {
        return pushServerURL;
    }

    public String getTheme() {
        return theme;
    }

    public String getMobileTheme() {
        return mobileTheme;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public Map<String, String> getErrorPages() {
        return errorPages;
    }

    public boolean isTransformMetadataEnabled() {
        return transformMetadataEnabled;
    }

    public boolean isLegacyWidgetNamespace() {
        return legacyWidgetNamespace;
    }

    public boolean isFontAwesomeEnabled() {
        return fontAwesomeEnabled;
    }

    public boolean isInterpolateClientSideValidationMessages() {
        return interpolateClientSideValidationMessages;
    }

    public boolean isAtLeastBV11() {
        return atLeastBv11;
    }

    public boolean isEarlyPostParamEvaluation() {
        return earlyPostParamEvaluation;
    }

    public boolean isCollectScripts() {
        return collectScripts;
    }
}
