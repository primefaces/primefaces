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
package org.primefaces.component.slider;

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
import org.primefaces.util.ComponentUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.event.SlideEndEvent;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import javax.faces.event.PhaseId;
import javax.faces.event.BehaviorEvent;
import org.primefaces.expression.SearchExpressionFacade;
import javax.faces.application.FacesMessage;
import org.primefaces.util.MessageFactory;


public abstract class SliderBase extends UIInput implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder,org.primefaces.component.api.PrimeClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Slider";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.SliderRenderer";

	public enum PropertyKeys {

		widgetVar
		,forValue("for")
		,display
		,minValue
		,maxValue
		,style
		,styleClass
		,animate
		,type
		,step
		,disabled
		,onSlideStart
		,onSlide
		,onSlideEnd
		,range
		,displayTemplate;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public SliderBase() {
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

	public java.lang.String getFor() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.forValue, null);
	}
	public void setFor(java.lang.String _for) {
		getStateHelper().put(PropertyKeys.forValue, _for);
	}

	public java.lang.String getDisplay() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.display, null);
	}
	public void setDisplay(java.lang.String _display) {
		getStateHelper().put(PropertyKeys.display, _display);
	}

	public double getMinValue() {
		return (java.lang.Double) getStateHelper().eval(PropertyKeys.minValue, 0.0);
	}
	public void setMinValue(double _minValue) {
		getStateHelper().put(PropertyKeys.minValue, _minValue);
	}

	public double getMaxValue() {
		return (java.lang.Double) getStateHelper().eval(PropertyKeys.maxValue, 100.0);
	}
	public void setMaxValue(double _maxValue) {
		getStateHelper().put(PropertyKeys.maxValue, _maxValue);
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

	public boolean isAnimate() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.animate, true);
	}
	public void setAnimate(boolean _animate) {
		getStateHelper().put(PropertyKeys.animate, _animate);
	}

	public java.lang.String getType() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.type, "horizontal");
	}
	public void setType(java.lang.String _type) {
		getStateHelper().put(PropertyKeys.type, _type);
	}

	public double getStep() {
		return (java.lang.Double) getStateHelper().eval(PropertyKeys.step, 1.0);
	}
	public void setStep(double _step) {
		getStateHelper().put(PropertyKeys.step, _step);
	}

	public boolean isDisabled() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
	}
	public void setDisabled(boolean _disabled) {
		getStateHelper().put(PropertyKeys.disabled, _disabled);
	}

	public java.lang.String getOnSlideStart() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onSlideStart, null);
	}
	public void setOnSlideStart(java.lang.String _onSlideStart) {
		getStateHelper().put(PropertyKeys.onSlideStart, _onSlideStart);
	}

	public java.lang.String getOnSlide() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onSlide, null);
	}
	public void setOnSlide(java.lang.String _onSlide) {
		getStateHelper().put(PropertyKeys.onSlide, _onSlide);
	}

	public java.lang.String getOnSlideEnd() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onSlideEnd, null);
	}
	public void setOnSlideEnd(java.lang.String _onSlideEnd) {
		getStateHelper().put(PropertyKeys.onSlideEnd, _onSlideEnd);
	}

	public boolean isRange() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.range, false);
	}
	public void setRange(boolean _range) {
		getStateHelper().put(PropertyKeys.range, _range);
	}

	public java.lang.String getDisplayTemplate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.displayTemplate, null);
	}
	public void setDisplayTemplate(java.lang.String _displayTemplate) {
		getStateHelper().put(PropertyKeys.displayTemplate, _displayTemplate);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}