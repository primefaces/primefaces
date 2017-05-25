/*
 * Copyright 2009-2016 PrimeTek.
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
package org.primefaces.mock.pf;

import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.config.PrimeConfiguration;

public class PrimeConfigurationMock extends PrimeConfiguration {
    
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
    private boolean interpolateClientSideValidationMessages = false;
    private boolean earlyPostParamEvaluation = false;

    // internal config
    private boolean beanValidationAvailable = false;
    private boolean stringConverterAvailable = false;
    private boolean el22Available = false;
    private boolean jsf22 = false;
    private boolean jsf21 = false;
    private boolean bv11 = false;

    // build properties
    private String buildVersion = null;

    // web.xml
    private Map<String, String> errorPages = null;
    
    public PrimeConfigurationMock(FacesContext context) {
        
    }

    @Override
    public boolean isValidateEmptyFields() {
        return validateEmptyFields;
    }

    public void setValidateEmptyFields(boolean validateEmptyFields) {
        this.validateEmptyFields = validateEmptyFields;
    }

    @Override
    public boolean isPartialSubmitEnabled() {
        return partialSubmitEnabled;
    }

    public void setPartialSubmitEnabled(boolean partialSubmitEnabled) {
        this.partialSubmitEnabled = partialSubmitEnabled;
    }

    @Override
    public boolean isResetValuesEnabled() {
        return resetValuesEnabled;
    }

    public void setResetValuesEnabled(boolean resetValuesEnabled) {
        this.resetValuesEnabled = resetValuesEnabled;
    }

    @Override
    public boolean isInterpretEmptyStringAsNull() {
        return interpretEmptyStringAsNull;
    }

    public void setInterpretEmptyStringAsNull(boolean interpretEmptyStringAsNull) {
        this.interpretEmptyStringAsNull = interpretEmptyStringAsNull;
    }

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String getPushServerURL() {
        return pushServerURL;
    }

    public void setPushServerURL(String pushServerURL) {
        this.pushServerURL = pushServerURL;
    }

    @Override
    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Override
    public String getMobileTheme() {
        return mobileTheme;
    }

    public void setMobileTheme(String mobileTheme) {
        this.mobileTheme = mobileTheme;
    }

    @Override
    public boolean isFontAwesomeEnabled() {
        return fontAwesomeEnabled;
    }

    public void setFontAwesomeEnabled(boolean fontAwesomeEnabled) {
        this.fontAwesomeEnabled = fontAwesomeEnabled;
    }

    @Override
    public boolean isClientSideValidationEnabled() {
        return clientSideValidationEnabled;
    }

    public void setClientSideValidationEnabled(boolean clientSideValidationEnabled) {
        this.clientSideValidationEnabled = clientSideValidationEnabled;
    }

    @Override
    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    @Override
    public boolean isTransformMetadataEnabled() {
        return transformMetadataEnabled;
    }

    public void setTransformMetadataEnabled(boolean transformMetadataEnabled) {
        this.transformMetadataEnabled = transformMetadataEnabled;
    }

    @Override
    public boolean isLegacyWidgetNamespace() {
        return legacyWidgetNamespace;
    }

    public void setLegacyWidgetNamespace(boolean legacyWidgetNamespace) {
        this.legacyWidgetNamespace = legacyWidgetNamespace;
    }

    public boolean isBeanValidationDisabled() {
        return beanValidationDisabled;
    }

    public void setBeanValidationDisabled(boolean beanValidationDisabled) {
        this.beanValidationDisabled = beanValidationDisabled;
    }

    @Override
    public boolean isInterpolateClientSideValidationMessages() {
        return interpolateClientSideValidationMessages;
    }

    public void setInterpolateClientSideValidationMessages(boolean interpolateClientSideValidationMessages) {
        this.interpolateClientSideValidationMessages = interpolateClientSideValidationMessages;
    }

    @Override
    public boolean isEarlyPostParamEvaluation() {
        return earlyPostParamEvaluation;
    }

    public void setEarlyPostParamEvaluation(boolean earlyPostParamEvaluation) {
        this.earlyPostParamEvaluation = earlyPostParamEvaluation;
    }

    @Override
    public boolean isBeanValidationAvailable() {
        return beanValidationAvailable;
    }

    public void setBeanValidationAvailable(boolean beanValidationAvailable) {
        this.beanValidationAvailable = beanValidationAvailable;
    }

    @Override
    public boolean isStringConverterAvailable() {
        return stringConverterAvailable;
    }

    public void setStringConverterAvailable(boolean stringConverterAvailable) {
        this.stringConverterAvailable = stringConverterAvailable;
    }

    public boolean isEl22Available() {
        return el22Available;
    }

    public void setEl22Available(boolean el22Available) {
        this.el22Available = el22Available;
    }

    public boolean isJsf22() {
        return jsf22;
    }

    public void setJsf22(boolean jsf22) {
        this.jsf22 = jsf22;
    }

    public boolean isJsf21() {
        return jsf21;
    }

    public void setJsf21(boolean jsf21) {
        this.jsf21 = jsf21;
    }

    public boolean isBv11() {
        return bv11;
    }

    public void setBv11(boolean bv11) {
        this.bv11 = bv11;
    }

    @Override
    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    @Override
    public Map<String, String> getErrorPages() {
        return errorPages;
    }

    public void setErrorPages(Map<String, String> errorPages) {
        this.errorPages = errorPages;
    }

    @Override
    public boolean isAtLeastEL22() {
        return el22Available;
    }
}
