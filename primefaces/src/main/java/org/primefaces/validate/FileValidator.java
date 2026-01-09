/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.validate;

import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.file.NativeUploadedFile;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;
import org.primefaces.util.FileUploadUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.MessageFactory;
import org.primefaces.validate.base.AbstractPrimeValidator;
import org.primefaces.virusscan.VirusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.html.HtmlInputFile;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.servlet.http.Part;

public class FileValidator extends AbstractPrimeValidator implements ClientValidator {

    public static final String VALIDATOR_ID = "primefaces.File";
    public static final String FILE_LIMIT_MESSAGE_ID = "primefaces.FileValidator.FILE_LIMIT";
    public static final String ALLOW_TYPES_MESSAGE_ID = "primefaces.FileValidator.ALLOW_TYPES";
    public static final String SIZE_LIMIT_MESSAGE_ID = "primefaces.FileValidator.SIZE_LIMIT";

    public enum PropertyKeys {
        allowTypes,
        fileLimit,
        sizeLimit,
        contentType,
        virusScan,
        allowMediaTypes
    }

    private static final Logger LOGGER = Logger.getLogger(FileValidator.class.getName());

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String componentAccept;
        UploadedFile singleFile = null;
        UploadedFiles multipleFiles = null;
        if (component instanceof FileUpload) {
            FileUpload fileUpload = (FileUpload) component;
            componentAccept = fileUpload.getAccept();
            if (value instanceof UploadedFile) {
                singleFile = (UploadedFile) value;
            }
            else if (value instanceof UploadedFiles) {
                multipleFiles = (UploadedFiles) value;
            }
        }
        else if (component instanceof HtmlInputFile) {
            HtmlInputFile inputFile = (HtmlInputFile) component;
            componentAccept = (String) inputFile.getAttributes().get("accept");
            if (value instanceof Part) {
                singleFile = new NativeUploadedFile((Part) value, getSizeLimit(), null);
            }
            else if (value instanceof List) {
                List<UploadedFile> files = (List<UploadedFile>) ((List) value).stream()
                        .map(part -> new NativeUploadedFile((Part) part, getSizeLimit(), null))
                        .collect(Collectors.toList());
                multipleFiles = new UploadedFiles(files);
            }
        }
        else {
            throw new IllegalArgumentException("Component of type '" + component.getClass().getName() + "' not supported");
        }
        if (value != null && singleFile == null && multipleFiles == null) {
            throw new IllegalArgumentException("Argument of type '" + value.getClass().getName() + "' not supported");
        }
        String accept = resolveAccept(componentAccept);
        String allowTypes = resolveAllowTypes(componentAccept, component);
        if (singleFile != null) {
            validateUploadedFile(context, singleFile, accept, allowTypes);
        }
        else if (multipleFiles != null) {
            validateUploadedFiles(context, multipleFiles, accept, allowTypes);
        }
    }

    /**
     * Resolves the accept value for validation.
     * If content-type validation is enabled, returns either the validator's allowMediaTypes
     * or the component's accept attribute.
     * @param accept Raw accept attribute from the component
     * @return the accept value to use for validation, or null if content type validation is disabled
     */
    private String resolveAccept(String accept) {
        if (Boolean.TRUE.equals(getContentType())) {
            if (LangUtils.isNotBlank(getAllowMediaTypes())) {
                return getAllowMediaTypes();
            }
            return accept;
        }
        return null;
    }

    /**
     * Resolves the allowed file types pattern for validation.
     * Returns the validator's allowTypes if configured. If allowTypes is not configured
     * and content-type validation is enabled, extracts file extensions from the component's
     * accept attribute and converts them to a regex pattern.
     * @param accept Raw accept attribute from the component
     * @param component The UI component being validated
     * @return the file types pattern to use for validation, or null if none found
     */
    private String resolveAllowTypes(String accept, UIComponent component) {
        String allowTypes = getAllowTypes();
        if (LangUtils.isBlank(allowTypes) && Boolean.TRUE.equals(getContentType())) {
            String extractedAllowTypes = FileUploadUtils.extractAllowTypes(accept);
            if (LangUtils.isNotBlank(extractedAllowTypes)) {
                allowTypes = extractedAllowTypes;
            }
            else {
                LOGGER.log(Level.WARNING,
                    "Either allowTypes attribute in p:validateFile or accept attribute with filename extension(s) in p:fileUpload is required. ClientId: "
                    + component.getClientId());
            }
        }
        return allowTypes;
    }

    protected void validateUploadedFiles(FacesContext context, UploadedFiles uploadedFiles, String accept, String allowTypes) {
        Integer fileLimit = getFileLimit();
        if (fileLimit != null && uploadedFiles.getFiles().size() > fileLimit) {
            throw new ValidatorException(
                    MessageFactory.getFacesMessage(context, FILE_LIMIT_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, fileLimit));
        }

        long totalSize = 0;
        for (UploadedFile file : uploadedFiles.getFiles()) {
            totalSize += file.getSize();
            validateUploadedFile(context, file, accept, allowTypes);
        }

        Long sizeLimit = getSizeLimit();
        if (sizeLimit != null && totalSize > sizeLimit) {
            throw new ValidatorException(
                    MessageFactory.getFacesMessage(context, SIZE_LIMIT_MESSAGE_ID, FacesMessage.SEVERITY_ERROR,
                            "*", FileUploadUtils.formatBytes(sizeLimit, LocaleUtils.getCurrentLocale(context))));
        }
    }

    protected void validateUploadedFile(FacesContext context, UploadedFile uploadedFile, String accept, String allowTypes) {
        PrimeApplicationContext applicationContext = PrimeApplicationContext.getCurrentInstance(context);

        Long sizeLimit = getSizeLimit();
        if (sizeLimit != null && uploadedFile.getSize() > sizeLimit) {
            throw new ValidatorException(
                    MessageFactory.getFacesMessage(context, SIZE_LIMIT_MESSAGE_ID, FacesMessage.SEVERITY_ERROR,
                            uploadedFile.getFileName(), FileUploadUtils.formatBytes(sizeLimit, LocaleUtils.getCurrentLocale(context))));
        }

        if (!FileUploadUtils.isValidType(applicationContext, uploadedFile, allowTypes, accept)) {
            throw new ValidatorException(
                    MessageFactory.getFacesMessage(context, ALLOW_TYPES_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, uploadedFile.getFileName(),
                            FileUploadUtils.formatAllowTypes(allowTypes)));
        }

        if (Boolean.TRUE.equals(getVirusScan())) {
            try {
                PrimeApplicationContext.getCurrentInstance(context).getVirusScannerService().performVirusScan(uploadedFile);
            }
            catch (VirusException e) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""), e);
            }

        }
    }

    @Override
    public Map<String, Object> getMetadata() {
        HashMap<String, Object> metadata = new HashMap<>();

        Integer fileLimit = getFileLimit();
        if (fileLimit != null) {
            metadata.put("data-p-filelimit", fileLimit);
        }

        Long sizeLimit = getSizeLimit();
        if (sizeLimit != null) {
            metadata.put("data-p-sizelimit", sizeLimit);
        }

        String allowTypes = getAllowTypes();
        if (LangUtils.isNotBlank(allowTypes)) {
            metadata.put("data-p-allowtypes", allowTypes);
        }

        return metadata;
    }

    @Override
    public String getValidatorId() {
        return VALIDATOR_ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FileValidator that = (FileValidator) o;
        return Objects.equals(getStateHelper(), that.getStateHelper());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStateHelper());
    }

    public Integer getFileLimit() {
        return (Integer) getStateHelper().eval(PropertyKeys.fileLimit, null);
    }

    public void setFileLimit(Integer fileLimit) {
        getStateHelper().put(PropertyKeys.fileLimit, fileLimit);
    }

    public Long getSizeLimit() {
        return (Long) getStateHelper().eval(PropertyKeys.sizeLimit, null);
    }

    public void setSizeLimit(Long sizeLimit) {
        getStateHelper().put(PropertyKeys.sizeLimit, sizeLimit);
    }

    public String getAllowTypes() {
        return (String) getStateHelper().eval(PropertyKeys.allowTypes, null);
    }

    public void setAllowTypes(String allowTypes) {
        getStateHelper().put(PropertyKeys.allowTypes, allowTypes);
    }

    public Boolean getContentType() {
        return (Boolean) getStateHelper().eval(PropertyKeys.contentType, null);
    }

    public void setContentType(Boolean contentType) {
        getStateHelper().put(PropertyKeys.contentType, contentType);
    }

    public Boolean getVirusScan() {
        return (Boolean) getStateHelper().eval(PropertyKeys.virusScan, null);
    }

    public void setVirusScan(Boolean virusScan) {
        getStateHelper().put(PropertyKeys.virusScan, virusScan);
    }

    public String getAllowMediaTypes() {
        return (String) getStateHelper().eval(PropertyKeys.allowMediaTypes, null);
    }

    public void setAllowMediaTypes(String allowMediaTypes) {
        getStateHelper().put(PropertyKeys.allowMediaTypes, allowMediaTypes);
    }
}
