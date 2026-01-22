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

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.el.MethodExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;

@FacesComponentBase
public abstract class FileUploadBase extends UIInput implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.FileUploadRenderer";

    public FileUploadBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Custom content to display when no files are selected.")
    public abstract UIComponent getEmptyFacet();

    @Property(description = "Comma separated list of content types to accept.")
    public abstract String getAccept();

    @Property(defaultValue = "false", description = "When enabled, upload starts automatically after selection.")
    public abstract boolean isAuto();

    @Property(description = "Style class of the cancel button.")
    public abstract String getCancelButtonStyleClass();

    @Property(description = "Title attribute of the cancel button.")
    public abstract String getCancelButtonTitle();

    @Property(defaultValue = "pi pi-times", description = "Icon of the cancel button.")
    public abstract String getCancelIcon();

    @Property(defaultValue = "Cancel", description = "Label of the cancel button.")
    public abstract String getCancelLabel();

    @Property(description = "Style class of the choose button.")
    public abstract String getChooseButtonStyleClass();

    @Property(description = "Title attribute of the choose button.")
    public abstract String getChooseButtonTitle();

    @Property(defaultValue = "pi pi-plus", description = "Icon of the choose button.")
    public abstract String getChooseIcon();

    @Property(defaultValue = "false", description = "Disables the component.")
    public abstract boolean isDisabled();

    @Property(description = "Whether to display the filename in simple mode.")
    public abstract boolean isDisplayFilename();

    @Property(defaultValue = "true", description = "Enables drag and drop functionality.")
    public abstract boolean isDragDrop();

    @Property(description = "Search expression to resolve a component as drop zone.")
    public abstract String getDropZone();

    @Property(defaultValue = "true", description = "Global AJAX requests are listened by ajaxStatus component, " +
        "if disabled only ajaxStatus components defined in parent naming containers are notified.")
    public abstract boolean isGlobal();

    @Property(defaultValue = "false", description = "If true, ajax requests will not update components with autoUpdate=\"true\".")
    public abstract boolean isIgnoreAutoUpdate();

    @Property(defaultValue = "Choose", description = "Label of the choose button.")
    public abstract String getLabel();

    @Property(description = "Method expression to invoke when a file is uploaded.")
    public abstract MethodExpression getListener();

    @Property(defaultValue = "0L", description = "Maximum chunk size in bytes for chunked uploads. 0 means no chunking.")
    public abstract Long getMaxChunkSize();

    @Property(defaultValue = "30", description = "Maximum number of retries for failed chunk uploads.")
    public abstract int getMaxRetries();

    @Property(description = "Template for displaying file information.")
    public abstract String getMessageTemplate();

    @Property(defaultValue = "advanced", description = "Mode of the fileupload, 'simple' or 'advanced'.")
    public abstract String getMode();

    @Property(defaultValue = "false", description = "Allows multiple file selection.")
    public abstract boolean isMultiple();

    @Property(description = "Client side callback to execute when a file is added.")
    public abstract String getOnAdd();

    @Property(description = "Client side callback to execute when upload is cancelled.")
    public abstract String getOncancel();

    @Property(description = "Client side callback to execute when upload completes.")
    public abstract String getOncomplete();

    @Property(description = "Client side callback to execute when upload fails.")
    public abstract String getOnerror();

    @Property(description = "Client side callback to execute when upload starts.")
    public abstract String getOnstart();

    @Property(description = "Client side callback to execute when a file is uploaded.")
    public abstract String getOnupload();

    @Property(description = "Client side callback to execute when validation fails.")
    public abstract String getOnvalidationfailure();

    @Property(defaultValue = "80", description = "Width of the preview in pixels.")
    public abstract int getPreviewWidth();

    @Property(description = "Component(s) to process in partial request.")
    public abstract String getProcess();

    @Property(defaultValue = "1000", description = "Timeout in milliseconds between retries.")
    public abstract int getRetryTimeout();

    @Property(defaultValue = "false", description = "Uploads files sequentially instead of in parallel.")
    public abstract boolean isSequential();

    @Property(defaultValue = "false", description = "Applies simple skin to the component.")
    public abstract boolean isSkinSimple();

    @Property(description = "Title attribute of the file input.")
    public abstract String getTitle();

    @Property(description = "Component(s) to update in partial request.")
    public abstract String getUpdate();

    @Property(description = "Style class of the upload button.")
    public abstract String getUploadButtonStyleClass();

    @Property(description = "Title attribute of the upload button.")
    public abstract String getUploadButtonTitle();

    @Property(defaultValue = "pi pi-upload", description = "Icon of the upload button.")
    public abstract String getUploadIcon();

    @Property(defaultValue = "Upload", description = "Label of the upload button.")
    public abstract String getUploadLabel();
}
