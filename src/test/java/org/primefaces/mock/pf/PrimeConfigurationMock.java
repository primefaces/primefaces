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
    private String  theme = null;
    private boolean fontAwesomeEnabled = false;
    private boolean clientSideValidationEnabled = false;
    private String  uploader = null;
    private boolean transformMetadataEnabled = false;
    private boolean legacyWidgetNamespace = false;
    private boolean beanValidationDisabled = false;
    private boolean interpolateClientSideValidationMessages = false;
    private boolean earlyPostParamEvaluation = false;
    private boolean moveScriptsToBottom = false;

    private boolean stringConverterAvailable = false;

    // web.xml
    private Map<String, String> errorPages = null;

    public PrimeConfigurationMock(FacesContext context) {

    }

    @Override
    public boolean isValidateEmptyFields() {
        return validateEmptyFields;
    }

    @Override
    public void setValidateEmptyFields(boolean validateEmptyFields) {
        this.validateEmptyFields = validateEmptyFields;
    }

    @Override
    public boolean isPartialSubmitEnabled() {
        return partialSubmitEnabled;
    }

    @Override
    public void setPartialSubmitEnabled(boolean partialSubmitEnabled) {
        this.partialSubmitEnabled = partialSubmitEnabled;
    }

    @Override
    public boolean isResetValuesEnabled() {
        return resetValuesEnabled;
    }

    @Override
    public void setResetValuesEnabled(boolean resetValuesEnabled) {
        this.resetValuesEnabled = resetValuesEnabled;
    }

    @Override
    public boolean isInterpretEmptyStringAsNull() {
        return interpretEmptyStringAsNull;
    }

    @Override
    public void setInterpretEmptyStringAsNull(boolean interpretEmptyStringAsNull) {
        this.interpretEmptyStringAsNull = interpretEmptyStringAsNull;
    }

    @Override
    public String getTheme() {
        return theme;
    }

    @Override
    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Override
    public boolean isFontAwesomeEnabled() {
        return fontAwesomeEnabled;
    }

    @Override
    public void setFontAwesomeEnabled(boolean fontAwesomeEnabled) {
        this.fontAwesomeEnabled = fontAwesomeEnabled;
    }

    @Override
    public boolean isClientSideValidationEnabled() {
        return clientSideValidationEnabled;
    }

    @Override
    public void setClientSideValidationEnabled(boolean clientSideValidationEnabled) {
        this.clientSideValidationEnabled = clientSideValidationEnabled;
    }

    @Override
    public String getUploader() {
        return uploader;
    }

    @Override
    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    @Override
    public boolean isTransformMetadataEnabled() {
        return transformMetadataEnabled;
    }

    @Override
    public void setTransformMetadataEnabled(boolean transformMetadataEnabled) {
        this.transformMetadataEnabled = transformMetadataEnabled;
    }

    @Override
    public boolean isLegacyWidgetNamespace() {
        return legacyWidgetNamespace;
    }

    @Override
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

    @Override
    public void setInterpolateClientSideValidationMessages(boolean interpolateClientSideValidationMessages) {
        this.interpolateClientSideValidationMessages = interpolateClientSideValidationMessages;
    }

    @Override
    public boolean isEarlyPostParamEvaluation() {
        return earlyPostParamEvaluation;
    }

    @Override
    public void setEarlyPostParamEvaluation(boolean earlyPostParamEvaluation) {
        this.earlyPostParamEvaluation = earlyPostParamEvaluation;
    }

    @Override
    public boolean isStringConverterAvailable() {
        return stringConverterAvailable;
    }

    @Override
    public void setStringConverterAvailable(boolean stringConverterAvailable) {
        this.stringConverterAvailable = stringConverterAvailable;
    }

    @Override
    public Map<String, String> getErrorPages() {
        return errorPages;
    }

    @Override
    public void setErrorPages(Map<String, String> errorPages) {
        this.errorPages = errorPages;
    }

    @Override
    public boolean isMoveScriptsToBottom() {
        return moveScriptsToBottom;
    }

    @Override
    public void setMoveScriptsToBottom(boolean moveScriptsToBottom) {
        this.moveScriptsToBottom = moveScriptsToBottom;
    }

}
