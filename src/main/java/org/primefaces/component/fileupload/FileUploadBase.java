/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.fileupload;

import javax.faces.component.UIInput;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class FileUploadBase extends UIInput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.FileUploadRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        update,
        process,
        fileUploadListener,
        multiple,
        auto,
        label,
        allowTypes,
        fileLimit,
        sizeLimit,
        mode,
        uploadLabel,
        cancelLabel,
        invalidSizeMessage,
        invalidFileMessage,
        fileLimitMessage,
        dragDropSupport,
        onstart,
        oncomplete,
        onerror,
        oncancel,
        disabled,
        messageTemplate,
        previewWidth,
        skinSimple,
        accept,
        sequential,
        chooseIcon,
        uploadIcon,
        cancelIcon,
        onAdd
    }

    public FileUploadBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public java.lang.String getUpdate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.update, null);
    }

    public void setUpdate(java.lang.String _update) {
        getStateHelper().put(PropertyKeys.update, _update);
    }

    public java.lang.String getProcess() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.process, null);
    }

    public void setProcess(java.lang.String _process) {
        getStateHelper().put(PropertyKeys.process, _process);
    }

    public javax.el.MethodExpression getFileUploadListener() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.fileUploadListener, null);
    }

    public void setFileUploadListener(javax.el.MethodExpression _fileUploadListener) {
        getStateHelper().put(PropertyKeys.fileUploadListener, _fileUploadListener);
    }

    public boolean isMultiple() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.multiple, false);
    }

    public void setMultiple(boolean _multiple) {
        getStateHelper().put(PropertyKeys.multiple, _multiple);
    }

    public boolean isAuto() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.auto, false);
    }

    public void setAuto(boolean _auto) {
        getStateHelper().put(PropertyKeys.auto, _auto);
    }

    public java.lang.String getLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.label, "Choose");
    }

    public void setLabel(java.lang.String _label) {
        getStateHelper().put(PropertyKeys.label, _label);
    }

    public java.lang.String getAllowTypes() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.allowTypes, null);
    }

    public void setAllowTypes(java.lang.String _allowTypes) {
        getStateHelper().put(PropertyKeys.allowTypes, _allowTypes);
    }

    public int getFileLimit() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.fileLimit, java.lang.Integer.MAX_VALUE);
    }

    public void setFileLimit(int _fileLimit) {
        getStateHelper().put(PropertyKeys.fileLimit, _fileLimit);
    }

    public java.lang.Long getSizeLimit() {
        return (java.lang.Long) getStateHelper().eval(PropertyKeys.sizeLimit, java.lang.Long.MAX_VALUE);
    }

    public void setSizeLimit(java.lang.Long _sizeLimit) {
        getStateHelper().put(PropertyKeys.sizeLimit, _sizeLimit);
    }

    public java.lang.String getMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.mode, "advanced");
    }

    public void setMode(java.lang.String _mode) {
        getStateHelper().put(PropertyKeys.mode, _mode);
    }

    public java.lang.String getUploadLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.uploadLabel, "Upload");
    }

    public void setUploadLabel(java.lang.String _uploadLabel) {
        getStateHelper().put(PropertyKeys.uploadLabel, _uploadLabel);
    }

    public java.lang.String getCancelLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.cancelLabel, "Cancel");
    }

    public void setCancelLabel(java.lang.String _cancelLabel) {
        getStateHelper().put(PropertyKeys.cancelLabel, _cancelLabel);
    }

    public java.lang.String getInvalidSizeMessage() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.invalidSizeMessage, null);
    }

    public void setInvalidSizeMessage(java.lang.String _invalidSizeMessage) {
        getStateHelper().put(PropertyKeys.invalidSizeMessage, _invalidSizeMessage);
    }

    public java.lang.String getInvalidFileMessage() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.invalidFileMessage, null);
    }

    public void setInvalidFileMessage(java.lang.String _invalidFileMessage) {
        getStateHelper().put(PropertyKeys.invalidFileMessage, _invalidFileMessage);
    }

    public java.lang.String getFileLimitMessage() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.fileLimitMessage, null);
    }

    public void setFileLimitMessage(java.lang.String _fileLimitMessage) {
        getStateHelper().put(PropertyKeys.fileLimitMessage, _fileLimitMessage);
    }

    public boolean isDragDropSupport() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dragDropSupport, true);
    }

    public void setDragDropSupport(boolean _dragDropSupport) {
        getStateHelper().put(PropertyKeys.dragDropSupport, _dragDropSupport);
    }

    public java.lang.String getOnstart() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onstart, null);
    }

    public void setOnstart(java.lang.String _onstart) {
        getStateHelper().put(PropertyKeys.onstart, _onstart);
    }

    public java.lang.String getOncomplete() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.oncomplete, null);
    }

    public void setOncomplete(java.lang.String _oncomplete) {
        getStateHelper().put(PropertyKeys.oncomplete, _oncomplete);
    }

    public java.lang.String getOnerror() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onerror, null);
    }

    public void setOnerror(java.lang.String _onerror) {
        getStateHelper().put(PropertyKeys.onerror, _onerror);
    }

    public java.lang.String getOncancel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.oncancel, null);
    }

    public void setOncancel(java.lang.String _oncancel) {
        getStateHelper().put(PropertyKeys.oncancel, _oncancel);
    }

    public boolean isDisabled() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean _disabled) {
        getStateHelper().put(PropertyKeys.disabled, _disabled);
    }

    public java.lang.String getMessageTemplate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.messageTemplate, null);
    }

    public void setMessageTemplate(java.lang.String _messageTemplate) {
        getStateHelper().put(PropertyKeys.messageTemplate, _messageTemplate);
    }

    public int getPreviewWidth() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.previewWidth, 80);
    }

    public void setPreviewWidth(int _previewWidth) {
        getStateHelper().put(PropertyKeys.previewWidth, _previewWidth);
    }

    public boolean isSkinSimple() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.skinSimple, false);
    }

    public void setSkinSimple(boolean _skinSimple) {
        getStateHelper().put(PropertyKeys.skinSimple, _skinSimple);
    }

    public java.lang.String getAccept() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.accept, null);
    }

    public void setAccept(java.lang.String _accept) {
        getStateHelper().put(PropertyKeys.accept, _accept);
    }

    public boolean isSequential() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.sequential, false);
    }

    public void setSequential(boolean _sequential) {
        getStateHelper().put(PropertyKeys.sequential, _sequential);
    }

    public java.lang.String getChooseIcon() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.chooseIcon, "ui-icon-plusthick");
    }

    public void setChooseIcon(java.lang.String _chooseIcon) {
        getStateHelper().put(PropertyKeys.chooseIcon, _chooseIcon);
    }

    public java.lang.String getUploadIcon() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.uploadIcon, "ui-icon-arrowreturnthick-1-n");
    }

    public void setUploadIcon(java.lang.String _uploadIcon) {
        getStateHelper().put(PropertyKeys.uploadIcon, _uploadIcon);
    }

    public java.lang.String getCancelIcon() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.cancelIcon, "ui-icon-cancel");
    }

    public void setCancelIcon(java.lang.String _cancelIcon) {
        getStateHelper().put(PropertyKeys.cancelIcon, _cancelIcon);
    }

    public java.lang.String getOnAdd() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onAdd, null);
    }

    public void setOnAdd(java.lang.String _onAdd) {
        getStateHelper().put(PropertyKeys.onAdd, _onAdd);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}