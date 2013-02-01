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
package org.primefaces.component.chart.metergauge;

import org.primefaces.component.chart.UIChart;
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
	@ResourceDependency(library="primefaces", name="charts/charts.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js"),
	@ResourceDependency(library="primefaces", name="charts/charts.js")
})
public class MeterGaugeChart extends UIChart implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.chart.MeterGaugeChart";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.chart.MeterGaugeChartRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,value
		,showTickLabels
		,labelHeightAdjust
		,intervalOuterRadius
		,min
		,max
		,label;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public MeterGaugeChart() {
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

	public org.primefaces.model.chart.MeterGaugeChartModel getValue() {
		return (org.primefaces.model.chart.MeterGaugeChartModel) getStateHelper().eval(PropertyKeys.value, null);
	}
	public void setValue(org.primefaces.model.chart.MeterGaugeChartModel _value) {
		getStateHelper().put(PropertyKeys.value, _value);
		handleAttribute("value", _value);
	}

	public boolean isShowTickLabels() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showTickLabels, true);
	}
	public void setShowTickLabels(boolean _showTickLabels) {
		getStateHelper().put(PropertyKeys.showTickLabels, _showTickLabels);
		handleAttribute("showTickLabels", _showTickLabels);
	}

	public int getLabelHeightAdjust() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.labelHeightAdjust, -25);
	}
	public void setLabelHeightAdjust(int _labelHeightAdjust) {
		getStateHelper().put(PropertyKeys.labelHeightAdjust, _labelHeightAdjust);
		handleAttribute("labelHeightAdjust", _labelHeightAdjust);
	}

	public int getIntervalOuterRadius() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.intervalOuterRadius, 85);
	}
	public void setIntervalOuterRadius(int _intervalOuterRadius) {
		getStateHelper().put(PropertyKeys.intervalOuterRadius, _intervalOuterRadius);
		handleAttribute("intervalOuterRadius", _intervalOuterRadius);
	}

	public double getMin() {
		return (java.lang.Double) getStateHelper().eval(PropertyKeys.min, java.lang.Double.MIN_VALUE);
	}
	public void setMin(double _min) {
		getStateHelper().put(PropertyKeys.min, _min);
		handleAttribute("min", _min);
	}

	public double getMax() {
		return (java.lang.Double) getStateHelper().eval(PropertyKeys.max, java.lang.Double.MAX_VALUE);
	}
	public void setMax(double _max) {
		getStateHelper().put(PropertyKeys.max, _max);
		handleAttribute("max", _max);
	}

	public java.lang.String getLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.label, null);
	}
	public void setLabel(java.lang.String _label) {
		getStateHelper().put(PropertyKeys.label, _label);
		handleAttribute("label", _label);
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