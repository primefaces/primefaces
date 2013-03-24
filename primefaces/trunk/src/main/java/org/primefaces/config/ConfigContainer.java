/*
 * Copyright 2009-2013 Prime Teknoloji.
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
import javax.faces.validator.BeanValidator;
import javax.validation.Validation;

import org.primefaces.util.Constants;

/**
 * Container for all config parameters.
 */
public class ConfigContainer {

	private static final Logger LOG = Logger.getLogger(ConfigContainer.class.getName());
	
	private boolean validateEmptyFields = false;
	private boolean beanValidationAvailable = false;
	private boolean partialSubmitEnabled = false;
	private boolean interpretEmptyStringAsNull = false;
	private boolean rightToLeft = false;

	public ConfigContainer(FacesContext context) {
		ExternalContext externalContext = context.getExternalContext();

        String value = null;
        
        value = externalContext.getInitParameter(Constants.INTERPRET_EMPTY_STRING_AS_NULL);
        interpretEmptyStringAsNull = (value == null) ? false : Boolean.valueOf(value);
        
        value = externalContext.getInitParameter(Constants.DIRECTION_PARAM);
        rightToLeft = (value == null) ? false : value.equalsIgnoreCase("rtl");
        
        value = externalContext.getInitParameter(Constants.SUBMIT_PARAM);
        partialSubmitEnabled = (value == null) ? false : value.equalsIgnoreCase("partial");
        
        beanValidationAvailable = checkIfBeanValidationIsAvailable();

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

    private boolean checkIfBeanValidationIsAvailable()
    {
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
            	LOG.log(Level.SEVERE, "Could not build BeanValidation default ValidatorFactory.", t);
            	available = false;
            }
        }

        return available;
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
}
