/*
 * Generated, Do Not Modify
 */
/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="fileupload/fileupload.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="jquery/jquery-plugins.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js"),
	@ResourceDependency(library="primefaces", name="fileupload/fileupload.js")
})
public class FileUpload extends UIInput implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.FileUpload";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.FileUploadRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,update
		,process
		,fileUploadListener
		,multiple
		,auto
		,label
		,allowTypes
		,sizeLimit
		,showButtons
		,style
		,styleClass
		,mode
		,uploadLabel
		,cancelLabel
		,invalidSizeMessage
		,invalidFileMessage
		,dragDropSupport
		,onstart
		,oncomplete
		,disabled;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public FileUpload() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
		handleAttribute("widgetVar", _widgetVar);
	}

	public java.lang.String getUpdate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.update, null);
	}
	public void setUpdate(java.lang.String _update) {
		getStateHelper().put(PropertyKeys.update, _update);
		handleAttribute("update", _update);
	}

	public java.lang.String getProcess() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.process, null);
	}
	public void setProcess(java.lang.String _process) {
		getStateHelper().put(PropertyKeys.process, _process);
		handleAttribute("process", _process);
	}

	public javax.el.MethodExpression getFileUploadListener() {
		return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.fileUploadListener, null);
	}
	public void setFileUploadListener(javax.el.MethodExpression _fileUploadListener) {
		getStateHelper().put(PropertyKeys.fileUploadListener, _fileUploadListener);
		handleAttribute("fileUploadListener", _fileUploadListener);
	}

	public boolean isMultiple() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.multiple, false);
	}
	public void setMultiple(boolean _multiple) {
		getStateHelper().put(PropertyKeys.multiple, _multiple);
		handleAttribute("multiple", _multiple);
	}

	public boolean isAuto() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.auto, false);
	}
	public void setAuto(boolean _auto) {
		getStateHelper().put(PropertyKeys.auto, _auto);
		handleAttribute("auto", _auto);
	}

	public java.lang.String getLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.label, "Choose");
	}
	public void setLabel(java.lang.String _label) {
		getStateHelper().put(PropertyKeys.label, _label);
		handleAttribute("label", _label);
	}

	public java.lang.String getAllowTypes() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.allowTypes, null);
	}
	public void setAllowTypes(java.lang.String _allowTypes) {
		getStateHelper().put(PropertyKeys.allowTypes, _allowTypes);
		handleAttribute("allowTypes", _allowTypes);
	}

	public int getSizeLimit() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.sizeLimit, java.lang.Integer.MAX_VALUE);
	}
	public void setSizeLimit(int _sizeLimit) {
		getStateHelper().put(PropertyKeys.sizeLimit, _sizeLimit);
		handleAttribute("sizeLimit", _sizeLimit);
	}

	public boolean isShowButtons() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showButtons, true);
	}
	public void setShowButtons(boolean _showButtons) {
		getStateHelper().put(PropertyKeys.showButtons, _showButtons);
		handleAttribute("showButtons", _showButtons);
	}

	public java.lang.String getStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
	}
	public void setStyle(java.lang.String _style) {
		getStateHelper().put(PropertyKeys.style, _style);
		handleAttribute("style", _style);
	}

	public java.lang.String getStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	public void setStyleClass(java.lang.String _styleClass) {
		getStateHelper().put(PropertyKeys.styleClass, _styleClass);
		handleAttribute("styleClass", _styleClass);
	}

	public java.lang.String getMode() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.mode, "advanced");
	}
	public void setMode(java.lang.String _mode) {
		getStateHelper().put(PropertyKeys.mode, _mode);
		handleAttribute("mode", _mode);
	}

	public java.lang.String getUploadLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.uploadLabel, "Upload");
	}
	public void setUploadLabel(java.lang.String _uploadLabel) {
		getStateHelper().put(PropertyKeys.uploadLabel, _uploadLabel);
		handleAttribute("uploadLabel", _uploadLabel);
	}

	public java.lang.String getCancelLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.cancelLabel, "Cancel");
	}
	public void setCancelLabel(java.lang.String _cancelLabel) {
		getStateHelper().put(PropertyKeys.cancelLabel, _cancelLabel);
		handleAttribute("cancelLabel", _cancelLabel);
	}

	public java.lang.String getInvalidSizeMessage() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.invalidSizeMessage, null);
	}
	public void setInvalidSizeMessage(java.lang.String _invalidSizeMessage) {
		getStateHelper().put(PropertyKeys.invalidSizeMessage, _invalidSizeMessage);
		handleAttribute("invalidSizeMessage", _invalidSizeMessage);
	}

	public java.lang.String getInvalidFileMessage() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.invalidFileMessage, null);
	}
	public void setInvalidFileMessage(java.lang.String _invalidFileMessage) {
		getStateHelper().put(PropertyKeys.invalidFileMessage, _invalidFileMessage);
		handleAttribute("invalidFileMessage", _invalidFileMessage);
	}

	public boolean isDragDropSupport() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dragDropSupport, true);
	}
	public void setDragDropSupport(boolean _dragDropSupport) {
		getStateHelper().put(PropertyKeys.dragDropSupport, _dragDropSupport);
		handleAttribute("dragDropSupport", _dragDropSupport);
	}

	public java.lang.String getOnstart() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onstart, null);
	}
	public void setOnstart(java.lang.String _onstart) {
		getStateHelper().put(PropertyKeys.onstart, _onstart);
		handleAttribute("onstart", _onstart);
	}

	public java.lang.String getOncomplete() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.oncomplete, null);
	}
	public void setOncomplete(java.lang.String _oncomplete) {
		getStateHelper().put(PropertyKeys.oncomplete, _oncomplete);
		handleAttribute("oncomplete", _oncomplete);
	}

	public boolean isDisabled() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
	}
	public void setDisabled(boolean _disabled) {
		getStateHelper().put(PropertyKeys.disabled, _disabled);
		handleAttribute("disabled", _disabled);
	}


    public final static String CONTAINER_CLASS = "ui-fileupload ui-widget";
    public final static String BUTTON_BAR_CLASS = "fileupload-buttonbar ui-widget-header ui-corner-top";
    public final static String CONTENT_CLASS = "fileupload-content ui-widget-content ui-corner-bottom";
    public final static String FILES_CLASS = "files";
    public final static String CHOOSE_BUTTON_CLASS = "fileinput-button";
    public final static String UPLOAD_BUTTON_CLASS = "start";
    public final static String CANCEL_BUTTON_CLASS = "cancel";

    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getFileUploadListener();
		
		if (me != null && event instanceof org.primefaces.event.FileUploadEvent) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	public String resolveWidgetVar() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userWidgetVar = (String) getAttributes().get("widgetVar");

		if(userWidgetVar != null)
			return userWidgetVar;
		 else
			return "widget_" + getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
	}

	public void handleAttribute(String name, Object value) {
		List<String> setAttributes = (List<String>) this.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");
		if(setAttributes == null) {
			String cname = this.getClass().getName();
			if(cname != null && cname.startsWith(OPTIMIZED_PACKAGE)) {
				setAttributes = new ArrayList<String>(6);
				this.getAttributes().put("javax.faces.component.UIComponentBase.attributesThatAreSet", setAttributes);
			}
		}
		if(setAttributes != null) {
			if(value == null) {
				ValueExpression ve = getValueExpression(name);
				if(ve == null) {
					setAttributes.remove(name);
				} else if(!setAttributes.contains(name)) {
					setAttributes.add(name);
				}
			}
		}
	}
}