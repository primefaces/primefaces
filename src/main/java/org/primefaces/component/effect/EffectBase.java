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
package org.primefaces.component.effect;

import javax.faces.component.UIComponentBase;
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


public abstract class EffectBase extends UIComponentBase implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Effect";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.EffectRenderer";

	public enum PropertyKeys {

		widgetVar
		,event
		,type
		,forValue("for")
		,speed
		,delay
		,queue;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public EffectBase() {
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
	}

	public java.lang.String getEvent() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.event, null);
	}
	public void setEvent(java.lang.String _event) {
		getStateHelper().put(PropertyKeys.event, _event);
	}

	public java.lang.String getType() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.type, null);
	}
	public void setType(java.lang.String _type) {
		getStateHelper().put(PropertyKeys.type, _type);
	}

	public java.lang.String getFor() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.forValue, null);
	}
	public void setFor(java.lang.String _for) {
		getStateHelper().put(PropertyKeys.forValue, _for);
	}

	public int getSpeed() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.speed, 1000);
	}
	public void setSpeed(int _speed) {
		getStateHelper().put(PropertyKeys.speed, _speed);
	}

	public int getDelay() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.delay, 0);
	}
	public void setDelay(int _delay) {
		getStateHelper().put(PropertyKeys.delay, _delay);
	}

	public boolean isQueue() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.queue, true);
	}
	public void setQueue(boolean _queue) {
		getStateHelper().put(PropertyKeys.queue, _queue);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}