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
package org.primefaces.component.fileupload;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FilesUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FileUploadUtils;
import org.primefaces.validate.FileValidator;

import java.util.Arrays;
import java.util.Map;

import jakarta.el.MethodExpression;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.validator.ValidatorException;

@FacesComponent(value = FileUpload.COMPONENT_TYPE, namespace = FileUpload.COMPONENT_FAMILY)
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "fileupload/fileupload.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "fileupload/fileupload.js")
public class FileUpload extends FileUploadBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.FileUpload";
    public static final String CONTAINER_CLASS = "ui-fileupload ui-widget ui-fileupload-responsive";
    public static final String DRAG_OVERLAY_CLASS = "ui-fileupload-drag-overlay";
    public static final String DRAG_OVERLAY_CONTENT_CLASS = "ui-fileupload-drag-overlay-content";
    public static final String BUTTON_BAR_CLASS = "ui-fileupload-buttonbar ui-widget-header";
    public static final String CONTENT_CLASS = "ui-fileupload-content ui-widget-content";
    public static final String EMPTY_CLASS = "ui-fileupload-empty";
    public static final String FILES_CLASS = "ui-fileupload-files";
    public static final String CHOOSE_BUTTON_CLASS = "ui-fileupload-choose";
    public static final String UPLOAD_BUTTON_CLASS = "ui-fileupload-upload";
    public static final String CANCEL_BUTTON_CLASS = "ui-fileupload-cancel";
    public static final String BUTTON_ICON_ONLY = "ui-fileupload-icon-only";

    public static final String CONTAINER_CLASS_SIMPLE = "ui-fileupload-simple ui-widget";
    public static final String FILENAME_CLASS = "ui-fileupload-filename";
    public static final String WITHDROPZONE_CLASS = "ui-fileupload-withdropzone";

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);

        FacesContext facesContext = getFacesContext();

        if (event instanceof FileUploadEvent || event instanceof FilesUploadEvent) {
            MethodExpression me = getListener();
            if (me != null) {
                me.invoke(facesContext.getELContext(), new Object[]{event});
            }
        }
    }

    @Override
    protected void validateValue(FacesContext context, Object newValue) {
        super.validateValue(context, newValue);

        if (isValid()) {

            if (newValue instanceof UploadedFile) {
                validateFilename(context, (UploadedFile) newValue);
            }
            else if (newValue instanceof UploadedFiles) {
                for (UploadedFile uploadedFile : ((UploadedFiles) newValue).getFiles()) {
                    validateFilename(context, uploadedFile);
                }
            }

            if (isValid()  && ComponentUtils.isRequestSource(this, context)) {
                if (newValue instanceof UploadedFile) {
                    int totalFilesCount = 0;
                    if ("advanced".equals(getMode())) {
                        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
                        totalFilesCount = Integer.parseInt(params.get(this.getClientId(context) + "_totalFilesCount"));
                    }

                    queueEvent(new FileUploadEvent(this, (UploadedFile) newValue, totalFilesCount));
                }
                else if (newValue instanceof UploadedFiles) {
                    queueEvent(new FilesUploadEvent(this, (UploadedFiles) newValue));
                }
            }
        }
    }

    public void validateFilename(FacesContext context, UploadedFile file) {
        try {
            FileUploadUtils.requireValidFilename(context, file.getFileName());
        }
        catch (ValidatorException ve) {
            setValid(false);
            context.addMessage(getClientId(context), ve.getFacesMessage());
        }
    }

    public FileValidator getFileValidator() {
        return Arrays.stream(getValidators())
                .filter(v -> v instanceof FileValidator)
                .map(v -> (FileValidator) v)
                .findFirst().orElse(null);
    }

    public Long getSizeLimit() {
        FileValidator fileValidator = getFileValidator();
        return fileValidator == null ? null : fileValidator.getSizeLimit();
    }
}