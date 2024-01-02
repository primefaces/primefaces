/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import org.apache.poi.util.StringUtil;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.file.NativeUploadedFile;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;
import org.primefaces.util.FileUploadUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.MessageFactory;
import org.primefaces.virusscan.VirusException;

import javax.faces.application.FacesMessage;
import javax.faces.component.PartialStateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputFile;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileValidator implements Validator, PartialStateHolder, ClientValidator {

    public static final String VALIDATOR_ID = "primefaces.File";
    public static final String FILE_LIMIT_MESSAGE_ID = "primefaces.FileValidator.FILE_LIMIT";
    public static final String ALLOW_TYPES_MESSAGE_ID = "primefaces.FileValidator.ALLOW_TYPES";
    public static final String SIZE_LIMIT_MESSAGE_ID = "primefaces.FileValidator.SIZE_LIMIT";

    private Integer fileLimit;
    private Long sizeLimit;
    private String allowTypes;
    private Boolean contentType;
    private Boolean virusScan;

    private boolean isTransient = false;
    private boolean initialStateMarked = false;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (component instanceof FileUpload) {
            String accept = Boolean.TRUE.equals(contentType)  ? ((FileUpload) component).getAccept() : null;
            if (value instanceof UploadedFile) {
                UploadedFile uploadedFile = (UploadedFile) value;

                validateUploadedFile(context, uploadedFile, accept);
            }
            else if (value instanceof UploadedFiles) {
                UploadedFiles uploadedFiles = (UploadedFiles) value;

                validateUploadedFiles(context, uploadedFiles, accept);
            }
            else if (value != null) {
                throw new IllegalArgumentException("Argument of type '" + value.getClass().getName() + "' not supported");
            }
        }
        else if (component instanceof HtmlInputFile) {
            String accept = Boolean.TRUE.equals(contentType)  ? (String) component.getAttributes().get("accept") : null;

            if (value instanceof Part) {
                UploadedFile uploadedFile = new NativeUploadedFile((Part) value, sizeLimit, null);
                validateUploadedFile(context, uploadedFile, accept);
            }
            else if (value instanceof List) {
                List<UploadedFile> uploadedFiles = (List<UploadedFile>) ((List) value).stream()
                        .map(part -> new NativeUploadedFile((Part) part, sizeLimit, null))
                        .collect(Collectors.toList());
                validateUploadedFiles(context, new UploadedFiles(uploadedFiles), accept);
            }
            else if (value != null) {
                throw new IllegalArgumentException("Argument of type '" + value.getClass().getName() + "' not supported");
            }
        }
        else {
            throw new IllegalArgumentException("Component of type '" + component.getClass() + "' not supported");
        }
    }

    protected void validateUploadedFiles(FacesContext context, UploadedFiles uploadedFiles, String accept) {
        if (fileLimit != null && uploadedFiles.getFiles().size() > fileLimit) {
            throw new ValidatorException(
                    MessageFactory.getFacesMessage(FILE_LIMIT_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, fileLimit));
        }

        long totalSize = 0;
        for (UploadedFile file : uploadedFiles.getFiles()) {
            totalSize += file.getSize();
            validateUploadedFile(context, file, accept);
        }

        if (sizeLimit != null && totalSize > sizeLimit) {
            throw new ValidatorException(
                    MessageFactory.getFacesMessage(SIZE_LIMIT_MESSAGE_ID, FacesMessage.SEVERITY_ERROR,
                            "*", FileUploadUtils.formatBytes(sizeLimit, LocaleUtils.getCurrentLocale(context))));
        }
    }

    protected void validateUploadedFile(FacesContext context, UploadedFile uploadedFile, String accept) {
        PrimeApplicationContext applicationContext = PrimeApplicationContext.getCurrentInstance(context);

        if (sizeLimit != null && uploadedFile.getSize() > sizeLimit) {
            throw new ValidatorException(
                    MessageFactory.getFacesMessage(SIZE_LIMIT_MESSAGE_ID, FacesMessage.SEVERITY_ERROR,
                            uploadedFile.getFileName(), FileUploadUtils.formatBytes(sizeLimit, LocaleUtils.getCurrentLocale(context))));
        }

        if (LangUtils.isNotBlank(allowTypes) && !FileUploadUtils.isValidType(applicationContext, uploadedFile, allowTypes, accept)) {
            throw new ValidatorException(
                    MessageFactory.getFacesMessage(ALLOW_TYPES_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, uploadedFile.getFileName()));
        }

        if (Boolean.TRUE.equals(virusScan)) {
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

        if (fileLimit != null) {
            metadata.put("data-p-filelimit", fileLimit);
        }

        if (sizeLimit != null) {
            metadata.put("data-p-sizelimit", sizeLimit);
        }

        if (!StringUtil.isBlank(allowTypes)) {
            metadata.put("data-p-allowtypes", allowTypes);
        }

        return metadata;
    }

    @Override
    public String getValidatorId() {
        return VALIDATOR_ID;
    }



    @Override
    public void clearInitialState() {
        initialStateMarked = false;
    }

    @Override
    public boolean initialStateMarked() {
        return initialStateMarked;
    }

    @Override
    public void markInitialState() {
        initialStateMarked = true;
    }

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }

        if (!initialStateMarked()) {
            Object[] values = new Object[5];
            values[0] = fileLimit;
            values[1] = sizeLimit;
            values[2] = allowTypes;
            values[3] = contentType;
            values[4] = virusScan;
            return values;
        }
        return null;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        if (context == null) {
            throw new NullPointerException();
        }

        if (state != null) {
            Object[] values = (Object[]) state;
            fileLimit = (Integer) values[0];
            sizeLimit = (Long) values[1];
            allowTypes = (String) values[2];
            contentType = (Boolean) values[3];
            virusScan = (Boolean) values[4];
        }
    }

    @Override
    public boolean isTransient() {
        return isTransient;
    }

    @Override
    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
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
        return Objects.equals(fileLimit, that.fileLimit)
                && Objects.equals(sizeLimit, that.sizeLimit)
                && Objects.equals(allowTypes, that.allowTypes)
                && Objects.equals(contentType, that.contentType)
                && Objects.equals(virusScan, that.virusScan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileLimit, sizeLimit, allowTypes, contentType, virusScan);
    }



    public Integer getFileLimit() {
        return fileLimit;
    }

    public void setFileLimit(Integer fileLimit) {
        this.fileLimit = fileLimit;
    }

    public Long getSizeLimit() {
        return sizeLimit;
    }

    public void setSizeLimit(Long sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public String getAllowTypes() {
        return allowTypes;
    }

    public void setAllowTypes(String allowTypes) {
        this.allowTypes = allowTypes;
    }

    public Boolean getContentType() {
        return contentType;
    }

    public void setContentType(Boolean contentType) {
        this.contentType = contentType;
    }

    public Boolean getVirusScan() {
        return virusScan;
    }

    public void setVirusScan(Boolean virusScan) {
        this.virusScan = virusScan;
    }
}
