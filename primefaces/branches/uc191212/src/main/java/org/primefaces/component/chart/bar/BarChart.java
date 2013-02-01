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
package org.primefaces.component.chart.bar;

import org.primefaces.component.chart.CartesianChart;
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
public class BarChart extends CartesianChart implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.chart.BarChart";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.chart.BarChartRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,barPadding
		,barMargin
		,orientation
		,stacked
		,min
		,max
		,breakOnNull
		,zoom
		,animate
		,showDatatip
		,datatipFormat;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public BarChart() {
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

	public int getBarPadding() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.barPadding, 8);
	}
	public void setBarPadding(int _barPadding) {
		getStateHelper().put(PropertyKeys.barPadding, _barPadding);
		handleAttribute("barPadding", _barPadding);
	}

	public int getBarMargin() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.barMargin, 10);
	}
	public void setBarMargin(int _barMargin) {
		getStateHelper().put(PropertyKeys.barMargin, _barMargin);
		handleAttribute("barMargin", _barMargin);
	}

	public java.lang.String getOrientation() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.orientation, "vertical");
	}
	public void setOrientation(java.lang.String _orientation) {
		getStateHelper().put(PropertyKeys.orientation, _orientation);
		handleAttribute("orientation", _orientation);
	}

	public boolean isStacked() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.stacked, false);
	}
	public void setStacked(boolean _stacked) {
		getStateHelper().put(PropertyKeys.stacked, _stacked);
		handleAttribute("stacked", _stacked);
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

	public boolean isBreakOnNull() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.breakOnNull, false);
	}
	public void setBreakOnNull(boolean _breakOnNull) {
		getStateHelper().put(PropertyKeys.breakOnNull, _breakOnNull);
		handleAttribute("breakOnNull", _breakOnNull);
	}

	public boolean isZoom() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.zoom, false);
	}
	public void setZoom(boolean _zoom) {
		getStateHelper().put(PropertyKeys.zoom, _zoom);
		handleAttribute("zoom", _zoom);
	}

	public boolean isAnimate() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.animate, false);
	}
	public void setAnimate(boolean _animate) {
		getStateHelper().put(PropertyKeys.animate, _animate);
		handleAttribute("animate", _animate);
	}

	public boolean isShowDatatip() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showDatatip, true);
	}
	public void setShowDatatip(boolean _showDatatip) {
		getStateHelper().put(PropertyKeys.showDatatip, _showDatatip);
		handleAttribute("showDatatip", _showDatatip);
	}

	public java.lang.String getDatatipFormat() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.datatipFormat, null);
	}
	public void setDatatipFormat(java.lang.String _datatipFormat) {
		getStateHelper().put(PropertyKeys.datatipFormat, _datatipFormat);
		handleAttribute("datatipFormat", _datatipFormat);
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