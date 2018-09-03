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

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public String getUpdate() {
        return (String) getStateHelper().eval(PropertyKeys.update, null);
    }

    public void setUpdate(String update) {
        getStateHelper().put(PropertyKeys.update, update);
    }

    public String getProcess() {
        return (String) getStateHelper().eval(PropertyKeys.process, null);
    }

    public void setProcess(String process) {
        getStateHelper().put(PropertyKeys.process, process);
    }

    public javax.el.MethodExpression getFileUploadListener() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.fileUploadListener, null);
    }

    public void setFileUploadListener(javax.el.MethodExpression fileUploadListener) {
        getStateHelper().put(PropertyKeys.fileUploadListener, fileUploadListener);
    }

    public boolean isMultiple() {
        return (Boolean) getStateHelper().eval(PropertyKeys.multiple, false);
    }

    public void setMultiple(boolean multiple) {
        getStateHelper().put(PropertyKeys.multiple, multiple);
    }

    public boolean isAuto() {
        return (Boolean) getStateHelper().eval(PropertyKeys.auto, false);
    }

    public void setAuto(boolean auto) {
        getStateHelper().put(PropertyKeys.auto, auto);
    }

    public String getLabel() {
        return (String) getStateHelper().eval(PropertyKeys.label, "Choose");
    }

    public void setLabel(String label) {
        getStateHelper().put(PropertyKeys.label, label);
    }

    public String getAllowTypes() {
        return (String) getStateHelper().eval(PropertyKeys.allowTypes, null);
    }

    public void setAllowTypes(String allowTypes) {
        getStateHelper().put(PropertyKeys.allowTypes, allowTypes);
    }

    public int getFileLimit() {
        return (Integer) getStateHelper().eval(PropertyKeys.fileLimit, Integer.MAX_VALUE);
    }

    public void setFileLimit(int fileLimit) {
        getStateHelper().put(PropertyKeys.fileLimit, fileLimit);
    }

    public Long getSizeLimit() {
        return (Long) getStateHelper().eval(PropertyKeys.sizeLimit, Long.MAX_VALUE);
    }

    public void setSizeLimit(Long sizeLimit) {
        getStateHelper().put(PropertyKeys.sizeLimit, sizeLimit);
    }

    public String getMode() {
        return (String) getStateHelper().eval(PropertyKeys.mode, "advanced");
    }

    public void setMode(String mode) {
        getStateHelper().put(PropertyKeys.mode, mode);
    }

    public String getUploadLabel() {
        return (String) getStateHelper().eval(PropertyKeys.uploadLabel, "Upload");
    }

    public void setUploadLabel(String uploadLabel) {
        getStateHelper().put(PropertyKeys.uploadLabel, uploadLabel);
    }

    public String getCancelLabel() {
        return (String) getStateHelper().eval(PropertyKeys.cancelLabel, "Cancel");
    }

    public void setCancelLabel(String cancelLabel) {
        getStateHelper().put(PropertyKeys.cancelLabel, cancelLabel);
    }

    public String getInvalidSizeMessage() {
        return (String) getStateHelper().eval(PropertyKeys.invalidSizeMessage, null);
    }

    public void setInvalidSizeMessage(String invalidSizeMessage) {
        getStateHelper().put(PropertyKeys.invalidSizeMessage, invalidSizeMessage);
    }

    public String getInvalidFileMessage() {
        return (String) getStateHelper().eval(PropertyKeys.invalidFileMessage, null);
    }

    public void setInvalidFileMessage(String invalidFileMessage) {
        getStateHelper().put(PropertyKeys.invalidFileMessage, invalidFileMessage);
    }

    public String getFileLimitMessage() {
        return (String) getStateHelper().eval(PropertyKeys.fileLimitMessage, null);
    }

    public void setFileLimitMessage(String fileLimitMessage) {
        getStateHelper().put(PropertyKeys.fileLimitMessage, fileLimitMessage);
    }

    public boolean isDragDropSupport() {
        return (Boolean) getStateHelper().eval(PropertyKeys.dragDropSupport, true);
    }

    public void setDragDropSupport(boolean dragDropSupport) {
        getStateHelper().put(PropertyKeys.dragDropSupport, dragDropSupport);
    }

    public String getOnstart() {
        return (String) getStateHelper().eval(PropertyKeys.onstart, null);
    }

    public void setOnstart(String onstart) {
        getStateHelper().put(PropertyKeys.onstart, onstart);
    }

    public String getOncomplete() {
        return (String) getStateHelper().eval(PropertyKeys.oncomplete, null);
    }

    public void setOncomplete(String oncomplete) {
        getStateHelper().put(PropertyKeys.oncomplete, oncomplete);
    }

    public String getOnerror() {
        return (String) getStateHelper().eval(PropertyKeys.onerror, null);
    }

    public void setOnerror(String onerror) {
        getStateHelper().put(PropertyKeys.onerror, onerror);
    }

    public String getOncancel() {
        return (String) getStateHelper().eval(PropertyKeys.oncancel, null);
    }

    public void setOncancel(String oncancel) {
        getStateHelper().put(PropertyKeys.oncancel, oncancel);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public String getMessageTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.messageTemplate, null);
    }

    public void setMessageTemplate(String messageTemplate) {
        getStateHelper().put(PropertyKeys.messageTemplate, messageTemplate);
    }

    public int getPreviewWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.previewWidth, 80);
    }

    public void setPreviewWidth(int previewWidth) {
        getStateHelper().put(PropertyKeys.previewWidth, previewWidth);
    }

    public boolean isSkinSimple() {
        return (Boolean) getStateHelper().eval(PropertyKeys.skinSimple, false);
    }

    public void setSkinSimple(boolean skinSimple) {
        getStateHelper().put(PropertyKeys.skinSimple, skinSimple);
    }

    public String getAccept() {
        return (String) getStateHelper().eval(PropertyKeys.accept, null);
    }

    public void setAccept(String accept) {
        getStateHelper().put(PropertyKeys.accept, accept);
    }

    public boolean isSequential() {
        return (Boolean) getStateHelper().eval(PropertyKeys.sequential, false);
    }

    public void setSequential(boolean sequential) {
        getStateHelper().put(PropertyKeys.sequential, sequential);
    }

    public String getChooseIcon() {
        return (String) getStateHelper().eval(PropertyKeys.chooseIcon, "ui-icon-plusthick");
    }

    public void setChooseIcon(String chooseIcon) {
        getStateHelper().put(PropertyKeys.chooseIcon, chooseIcon);
    }

    public String getUploadIcon() {
        return (String) getStateHelper().eval(PropertyKeys.uploadIcon, "ui-icon-arrowreturnthick-1-n");
    }

    public void setUploadIcon(String uploadIcon) {
        getStateHelper().put(PropertyKeys.uploadIcon, uploadIcon);
    }

    public String getCancelIcon() {
        return (String) getStateHelper().eval(PropertyKeys.cancelIcon, "ui-icon-cancel");
    }

    public void setCancelIcon(String cancelIcon) {
        getStateHelper().put(PropertyKeys.cancelIcon, cancelIcon);
    }

    public String getOnAdd() {
        return (String) getStateHelper().eval(PropertyKeys.onAdd, null);
    }

    public void setOnAdd(String onAdd) {
        getStateHelper().put(PropertyKeys.onAdd, onAdd);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}