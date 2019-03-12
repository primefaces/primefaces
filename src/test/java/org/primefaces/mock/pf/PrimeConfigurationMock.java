/* 
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
package org.primefaces.mock.pf;

import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.config.PrimeEnvironment;

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

    public PrimeConfigurationMock(FacesContext context, PrimeEnvironment environment) {
        super(context, environment);
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
    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
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
    public boolean isStringConverterAvailable() {
        return stringConverterAvailable;
    }

    public void setStringConverterAvailable(boolean stringConverterAvailable) {
        this.stringConverterAvailable = stringConverterAvailable;
    }

    @Override
    public Map<String, String> getErrorPages() {
        return errorPages;
    }

    public void setErrorPages(Map<String, String> errorPages) {
        this.errorPages = errorPages;
    }

    @Override
    public boolean isMoveScriptsToBottom() {
        return moveScriptsToBottom;
    }

    public void setMoveScriptsToBottom(boolean moveScriptsToBottom) {
        this.moveScriptsToBottom = moveScriptsToBottom;
    }

}
