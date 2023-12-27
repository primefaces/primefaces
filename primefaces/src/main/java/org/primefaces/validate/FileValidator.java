/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;
import org.primefaces.util.FileUploadUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;
import org.primefaces.virusscan.VirusException;

import javax.faces.application.FacesMessage;
import javax.faces.component.PartialStateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FileValidator implements Validator, PartialStateHolder, ClientValidator {

    private static final String VALIDATOR_ID = "primefaces.File";
    private static final String FILE_LIMIT_MESSAGE_ID = "primefaces.FileValidator.FILE_LIMIT";
    private static final String ALLOW_TYPES_MESSAGE_ID = "primefaces.FileValidator.ALLOW_TYPES";
    private static final String SIZE_LIMIT_MESSAGE_ID = "primefaces.FileValidator.SIZE_LIMIT";

    private Integer fileLimit;
    private String fileLimitMessage;
    private Long sizeLimit;
    private String sizeLimitMessage;
    private String allowTypes;
    private String allowTypesMessage;

    private Boolean virusScan;

    private boolean isTransient = false;
    private boolean initialStateMarked = false;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (component instanceof FileUpload) {
            FileUpload fileUpload = (FileUpload) component;

            if (value instanceof UploadedFile) {
                UploadedFile uploadedFile = (UploadedFile) value;

                validateUploadedFile(context, fileUpload, uploadedFile);
            }
            else if (value instanceof UploadedFiles) {
                UploadedFiles uploadedFiles = (UploadedFiles) value;

                validateUploadedFiles(context, fileUpload, uploadedFiles);
            }
        }
    }

    protected void validateUploadedFiles(FacesContext context, FileUpload fileUpload, UploadedFiles uploadedFiles) {
        if (fileLimit != null && uploadedFiles.getFiles().size() > fileLimit) {
            if (LangUtils.isNotBlank(fileLimitMessage)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, fileLimitMessage, ""));
            }
            throw new ValidatorException(
                    MessageFactory.getFacesMessage(FILE_LIMIT_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, fileLimit));
        }

        long totalSize = 0;
        for (UploadedFile file : uploadedFiles.getFiles()) {
            totalSize += file.getSize();
            validate(context, fileUpload, file);
        }

        if (sizeLimit != null && totalSize > sizeLimit) {
            if (LangUtils.isNotBlank(sizeLimitMessage)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, sizeLimitMessage, ""));
            }
            throw new ValidatorException(
                    MessageFactory.getFacesMessage(SIZE_LIMIT_MESSAGE_ID, FacesMessage.SEVERITY_ERROR,
                            "*", FileUploadUtils.formatBytes(sizeLimit)));
        }
    }

    protected void validateUploadedFile(FacesContext context, FileUpload fileUpload, UploadedFile uploadedFile) {
        PrimeApplicationContext applicationContext = PrimeApplicationContext.getCurrentInstance(context);

        if (sizeLimit != null && uploadedFile.getSize() > sizeLimit) {
            if (LangUtils.isNotBlank(sizeLimitMessage)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, sizeLimitMessage, ""));
            }
            throw new ValidatorException(
                    MessageFactory.getFacesMessage(SIZE_LIMIT_MESSAGE_ID, FacesMessage.SEVERITY_ERROR,
                            uploadedFile.getFileName(), FileUploadUtils.formatBytes(sizeLimit)));
        }

        if (LangUtils.isNotBlank(allowTypes) && !FileUploadUtils.isValidType(applicationContext, fileUpload, uploadedFile, allowTypes)) {
            if (LangUtils.isNotBlank(allowTypesMessage)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, allowTypesMessage, ""));
            }
            throw new ValidatorException(
                    MessageFactory.getFacesMessage(ALLOW_TYPES_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, uploadedFile.getFileName()));
        }

        if (virusScan != null && virusScan) {
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

            if (LangUtils.isNotBlank(fileLimitMessage)) {
                metadata.put("data-p-filelimitmsg", fileLimitMessage);
            }
        }

        if (sizeLimit != null) {
            metadata.put("data-p-sizelimit", sizeLimit);

            if (LangUtils.isNotBlank(sizeLimitMessage)) {
                metadata.put("data-p-sizelimitmsg", sizeLimitMessage);
            }
        }

        if (!StringUtil.isBlank(allowTypes)) {
            metadata.put("data-p-allowtypes", allowTypes);

            if (LangUtils.isNotBlank(allowTypesMessage)) {
                metadata.put("data-p-allowtypesmsg", allowTypesMessage);
            }
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
            Object[] values = new Object[7];
            values[0] = fileLimit;
            values[1] = fileLimitMessage;
            values[2] = sizeLimit;
            values[3] = sizeLimitMessage;
            values[4] = allowTypes;
            values[5] = allowTypesMessage;
            values[6] = virusScan;
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
            fileLimitMessage = (String) values[1];
            sizeLimit = (Long) values[2];
            sizeLimitMessage = (String) values[3];
            allowTypes = (String) values[4];
            allowTypesMessage = (String) values[5];
            virusScan = (Boolean) values[6];
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
                && Objects.equals(fileLimitMessage, that.fileLimitMessage)
                && Objects.equals(sizeLimit, that.sizeLimit)
                && Objects.equals(sizeLimitMessage, that.sizeLimitMessage)
                && Objects.equals(allowTypes, that.allowTypes)
                && Objects.equals(allowTypesMessage, that.allowTypesMessage)
                && Objects.equals(virusScan, that.virusScan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileLimit, fileLimitMessage, sizeLimit, sizeLimitMessage, allowTypes, allowTypesMessage, virusScan);
    }



    public Integer getFileLimit() {
        return fileLimit;
    }

    public void setFileLimit(Integer fileLimit) {
        this.fileLimit = fileLimit;
    }

    public String getFileLimitMessage() {
        return fileLimitMessage;
    }

    public void setFileLimitMessage(String fileLimitMessage) {
        this.fileLimitMessage = fileLimitMessage;
    }

    public Long getSizeLimit() {
        return sizeLimit;
    }

    public void setSizeLimit(Long sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public String getSizeLimitMessage() {
        return sizeLimitMessage;
    }

    public void setSizeLimitMessage(String sizeLimitMessage) {
        this.sizeLimitMessage = sizeLimitMessage;
    }

    public String getAllowTypes() {
        return allowTypes;
    }

    public void setAllowTypes(String allowTypes) {
        this.allowTypes = allowTypes;
    }

    public String getAllowTypesMessage() {
        return allowTypesMessage;
    }

    public void setAllowTypesMessage(String allowTypesMessage) {
        this.allowTypesMessage = allowTypesMessage;
    }

    public Boolean getVirusScan() {
        return virusScan;
    }

    public void setVirusScan(Boolean virusScan) {
        this.virusScan = virusScan;
    }
}
