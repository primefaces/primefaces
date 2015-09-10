/*
 * Copyright 2009-2014 PrimeTek.
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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.ViewHandler;

import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.validation.Validation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.primefaces.util.ComponentUtils;

import org.primefaces.util.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Container for all config parameters.
 */
public class ConfigContainer {

    private static final Logger LOG = Logger.getLogger(ConfigContainer.class.getName());

    // context params
    private boolean validateEmptyFields = false;
    private boolean partialSubmitEnabled = false;
    private boolean resetValuesEnabled = false;
    private boolean interpretEmptyStringAsNull = false;
    private String  secretKey = null;
    private String  pushServerURL = null;
    private String  theme = null;
    private String  mobileTheme = null;
    private boolean fontAwesomeEnabled = false;
    private boolean clientSideValidationEnabled = false;
    private String  uploader = null;
    private boolean transformMetadataEnabled = false;
    private boolean legacyWidgetNamespace = false;
    private boolean beanValidationDisabled = false;

    // internal config
    private boolean beanValidationAvailable = false;
    private boolean stringConverterAvailable = false;
    private boolean el22Available = false;
    private boolean jsf22 = false;
    private boolean jsf21 = false;

    // build properties
    private String buildVersion = null;

    // web.xml
    private Map<String, String> errorPages = null;

    protected ConfigContainer() {

    }

    public ConfigContainer(FacesContext context) {
        initConfigFromContextParams(context);
        initConfig(context);
        initBuildProperties();
        initConfigFromWebXml(context);
        initValidateEmptyFields(context);
    }

    protected void initConfig(FacesContext context) {
        el22Available = checkIfEL22IsAvailable();
        beanValidationAvailable = checkIfBeanValidationIsAvailable();

        jsf22 = detectJSF22();
        if (jsf22) {
            jsf21 = true;
        }
        else {
            jsf21 = detectJSF21();
        }

        stringConverterAvailable = null != context.getApplication().createConverter(String.class);
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
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Could not load pom.properties", e);
        }

        if (is != null) {
            try {
                is.close();
            }
            catch (IOException e) { }
        }
    }

    private boolean checkIfBeanValidationIsAvailable() {
    	boolean available = false;

    	// check if class is available
        try {
            available = Class.forName("javax.validation.Validation") != null;
        } catch (ClassNotFoundException e) {
            available = false;
        }

        if (available) {
            // Trial-error approach to check for Bean Validation impl existence.
            // If any Exception occurs here, we assume that Bean Validation is not available.
            // The cause may be anything, i.e. NoClassDef, config error...
            try {
            	Validation.buildDefaultValidatorFactory().getValidator();
            } catch (Throwable t) {
            	LOG.log(Level.FINE, "BV not available - Could not build default ValidatorFactory.");
            	available = false;
            }
        }

        return available && !beanValidationDisabled && el22Available;
    }
    
    private boolean checkIfEL22IsAvailable() {
    	boolean available;

        try {
            available = Class.forName("javax.el.ValueReference") != null;
        } catch (ClassNotFoundException e) {
            available = false;
        }

        return available;
    }

    private boolean detectJSF22() {
        String version = FacesContext.class.getPackage().getImplementationVersion();

        if(version != null) {
            return version.startsWith("2.2");
        }
        else {
            //fallback
            try {
                Class.forName("javax.faces.flow.Flow");
                return true;
            }
            catch (ClassNotFoundException ex) {
                return false;
            }
        }
    }

    private boolean detectJSF21() {
        String version = FacesContext.class.getPackage().getImplementationVersion();

        if(version != null) {
            return version.startsWith("2.1");
        }
        else {
            //fallback
            try {
                ViewHandler.class.getDeclaredMethod("deriveLogicalViewId", FacesContext.class, String.class);
                return true;
            }
            catch (NoSuchMethodException ex) {
                return false;
            }
        }
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
    
    public boolean isEL22Available() {
        return el22Available;
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

    public boolean isAtLeastJSF22() {
        return jsf22;
    }

    public boolean isAtLeastJSF21() {
        return jsf21;
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
}
