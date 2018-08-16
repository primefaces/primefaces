/*
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
package org.primefaces.component.clock;

import javax.faces.component.UIOutput;
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
import org.primefaces.util.ComponentUtils;
import java.util.Map; 


public abstract class ClockBase extends UIOutput implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Clock";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.ClockRenderer";

	public enum PropertyKeys {

		pattern
		,mode
		,autoSync
		,syncInterval
		,timeZone
		,displayMode;
	}

	public ClockBase() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getPattern() {
		return (String) getStateHelper().eval(PropertyKeys.pattern, null);
	}
	public void setPattern(String _pattern) {
		getStateHelper().put(PropertyKeys.pattern, _pattern);
	}

	public java.lang.String getMode() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.mode, "client");
	}
	public void setMode(java.lang.String _mode) {
		getStateHelper().put(PropertyKeys.mode, _mode);
	}

	public boolean isAutoSync() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.autoSync, false);
	}
	public void setAutoSync(boolean _autoSync) {
		getStateHelper().put(PropertyKeys.autoSync, _autoSync);
	}

	public int getSyncInterval() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.syncInterval, 60000);
	}
	public void setSyncInterval(int _syncInterval) {
		getStateHelper().put(PropertyKeys.syncInterval, _syncInterval);
	}

	public java.lang.Object getTimeZone() {
		return (java.lang.Object) getStateHelper().eval(PropertyKeys.timeZone, null);
	}
	public void setTimeZone(java.lang.Object _timeZone) {
		getStateHelper().put(PropertyKeys.timeZone, _timeZone);
	}

	public java.lang.String getDisplayMode() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.displayMode, "digital");
	}
	public void setDisplayMode(java.lang.String _displayMode) {
		getStateHelper().put(PropertyKeys.displayMode, _displayMode);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}