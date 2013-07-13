/*
 * Copyright 2009-2013 PrimeTek.
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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.validation.Validation;

import org.primefaces.util.Constants;

/**
 * Container for all config parameters.
 */
public class ConfigContainer {

	private static final Logger LOG = Logger.getLogger(ConfigContainer.class.getName());

	// context params
	private boolean validateEmptyFields = false;
	private boolean beanValidationAvailable = false;
	private boolean partialSubmitEnabled = false;
	private boolean resetValuesEnabled = false;
	private boolean interpretEmptyStringAsNull = false;
	private boolean rightToLeft = false;
	private String  secretKey = null;
	private String  pushServerURL = null;
	private String  theme = null;

	// internal config
	private boolean stringConverterAvailable = false;
	private boolean jsf22 = false;

	public ConfigContainer(FacesContext context) {
		initConfig(context);
		initConfigFromContextParams(context);
	}

	private void initConfig(FacesContext context) {
		beanValidationAvailable = checkIfBeanValidationIsAvailable();
		
		jsf22 = detectJSF22();
		
		stringConverterAvailable = null != context.getApplication().createConverter(String.class);
	}
	
	private void initConfigFromContextParams(FacesContext context) {
		ExternalContext externalContext = context.getExternalContext();

        String value = null;

        value = externalContext.getInitParameter(Constants.ContextParams.INTERPRET_EMPTY_STRING_AS_NULL);
        interpretEmptyStringAsNull = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.DIRECTION);
        rightToLeft = (value == null) ? false : value.equalsIgnoreCase("rtl");

        value = externalContext.getInitParameter(Constants.ContextParams.SUBMIT);
        partialSubmitEnabled = (value == null) ? false : value.equalsIgnoreCase("partial");
        
        value = externalContext.getInitParameter(Constants.ContextParams.RESET_VALUES);
        resetValuesEnabled = (value == null) ? false : Boolean.valueOf(value);

        value = externalContext.getInitParameter(Constants.ContextParams.SECRET_KEY);
        secretKey = (value == null) ? "primefaces" : value;
        
        pushServerURL = externalContext.getInitParameter(Constants.ContextParams.PUSH_SERVER_URL);
        
        theme = externalContext.getInitParameter(Constants.ContextParams.THEME);

        value = externalContext.getInitParameter(UIInput.VALIDATE_EMPTY_FIELDS_PARAM_NAME);
        if (null == value) {
            value = (String) externalContext.getApplicationMap().get(UIInput.VALIDATE_EMPTY_FIELDS_PARAM_NAME);
        }
        if (value == null || value.equals("auto")) {
        	validateEmptyFields = beanValidationAvailable;
        } else {
        	validateEmptyFields = Boolean.valueOf(value);
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

	public boolean isValidateEmptyFields() {
		return validateEmptyFields;
	}

	public boolean isBeanValidationAvailable() {
		return beanValidationAvailable;
	}

	public boolean isPartialSubmitEnabled() {
		return partialSubmitEnabled;
	}

	public boolean isInterpretEmptyStringAsNull() {
		return interpretEmptyStringAsNull;
	}

	public boolean isRightToLeft() {
		return rightToLeft;
	}

	public boolean isStringConverterAvailable()
	{
		return stringConverterAvailable;
	}

    public String getSecretKey() {
        return secretKey;
    }

    public boolean isAtLeastJSF22() {
        return jsf22;
    }
    
    public boolean isResetValuesEnabled() {
    	return resetValuesEnabled;
    }

    public String getPushServerURL() {
        return pushServerURL;
    }

    public String getTheme() {
        return theme;
    }
}
