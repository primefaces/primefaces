/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FileUploadUtils;
import org.primefaces.virusscan.VirusException;

import javax.el.MethodExpression;
import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.validator.ValidatorException;
import org.primefaces.event.FilesUploadEvent;

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
    public static final String BUTTON_BAR_CLASS = "ui-fileupload-buttonbar ui-widget-header ui-corner-top";
    public static final String CONTENT_CLASS = "ui-fileupload-content ui-widget-content ui-corner-bottom";
    public static final String FILES_CLASS = "ui-fileupload-files";
    public static final String CHOOSE_BUTTON_CLASS = "ui-fileupload-choose";
    public static final String UPLOAD_BUTTON_CLASS = "ui-fileupload-upload";
    public static final String CANCEL_BUTTON_CLASS = "ui-fileupload-cancel";
    public static final String BUTTON_ICON_ONLY = "ui-fileupload-icon-only";

    public static final String CONTAINER_CLASS_SIMPLE = "ui-fileupload-simple ui-widget";
    public static final String FILENAME_CLASS = "ui-fileupload-filename";

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

        if (isValid() && ComponentUtils.isRequestSource(this, context)) {
            try {
                if (newValue instanceof UploadedFile) {
                    FileUploadUtils.tryValidateFile(context, this, (UploadedFile) newValue);
                }
                else if (newValue instanceof UploadedFiles) {
                    FileUploadUtils.tryValidateFiles(context, this, ((UploadedFiles) newValue).getFiles());
                }
                else {
                    throw new IllegalArgumentException("Argument of type '" + newValue.getClass().getName() + "' not supported");
                }

                if (newValue instanceof UploadedFile) {
                    queueEvent(new FileUploadEvent(this, (UploadedFile) newValue));
                }
                else if (newValue instanceof UploadedFiles) {
                    queueEvent(new FilesUploadEvent(this, (UploadedFiles) newValue));
                }
            }
            catch (VirusException | ValidatorException e) {
                setValid(false);
                context.addMessage(getClientId(context), new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            }
        }
    }
}