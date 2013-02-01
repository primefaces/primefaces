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
package org.primefaces.component.chart.pie;

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
public class PieChart extends UIChart implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.chart.PieChart";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.chart.PieChartRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,diameter
		,sliceMargin
		,fill
		,showDataLabels
		,dataFormat;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public PieChart() {
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

	public int getDiameter() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.diameter, java.lang.Integer.MIN_VALUE);
	}
	public void setDiameter(int _diameter) {
		getStateHelper().put(PropertyKeys.diameter, _diameter);
		handleAttribute("diameter", _diameter);
	}

	public int getSliceMargin() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.sliceMargin, 0);
	}
	public void setSliceMargin(int _sliceMargin) {
		getStateHelper().put(PropertyKeys.sliceMargin, _sliceMargin);
		handleAttribute("sliceMargin", _sliceMargin);
	}

	public boolean isFill() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.fill, true);
	}
	public void setFill(boolean _fill) {
		getStateHelper().put(PropertyKeys.fill, _fill);
		handleAttribute("fill", _fill);
	}

	public boolean isShowDataLabels() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showDataLabels, false);
	}
	public void setShowDataLabels(boolean _showDataLabels) {
		getStateHelper().put(PropertyKeys.showDataLabels, _showDataLabels);
		handleAttribute("showDataLabels", _showDataLabels);
	}

	public java.lang.String getDataFormat() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.dataFormat, null);
	}
	public void setDataFormat(java.lang.String _dataFormat) {
		getStateHelper().put(PropertyKeys.dataFormat, _dataFormat);
		handleAttribute("dataFormat", _dataFormat);
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