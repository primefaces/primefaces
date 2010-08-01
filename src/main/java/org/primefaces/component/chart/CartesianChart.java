/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.component.chart;

public abstract class CartesianChart extends UIChart {

    protected enum PropertyKeys {
		xfield
        ,yfield
        ,minX
        ,maxX
		,minY
		,maxY
		,titleX
		,titleY
		,labelFunctionX
		,labelFunctionY;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

        @Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    public Object getXfield() {
		return (Object) getStateHelper().eval(PropertyKeys.xfield, null);
	}
	public void setXfield(Object _xfield) {
		getStateHelper().put(PropertyKeys.xfield, _xfield);
	}

    public Object getYfield() {
		return (Object) getStateHelper().eval(PropertyKeys.yfield, null);
	}
	public void setYfield(Object _yfield) {
		getStateHelper().put(PropertyKeys.yfield, _yfield);
	}

    public double getMinX() {
		return (Double) getStateHelper().eval(PropertyKeys.minX, Double.MIN_VALUE);
	}
	public void setMinX(double _minX) {
		getStateHelper().put(PropertyKeys.minX, _minX);
	}

	public double getMaxX() {
		return (Double) getStateHelper().eval(PropertyKeys.maxX, Double.MIN_VALUE);
	}
	public void setMaxX(double _maxX) {
		getStateHelper().put(PropertyKeys.maxX, _maxX);
	}

	public double getMinY() {
		return (Double) getStateHelper().eval(PropertyKeys.minY, Double.MIN_VALUE);
	}
	public void setMinY(double _minY) {
		getStateHelper().put(PropertyKeys.minY, _minY);
	}

	public double getMaxY() {
		return (Double) getStateHelper().eval(PropertyKeys.maxY, Double.MIN_VALUE);
	}
	public void setMaxY(double _maxY) {
		getStateHelper().put(PropertyKeys.maxY, _maxY);
	}

	public String getTitleX() {
		return (String) getStateHelper().eval(PropertyKeys.titleX, null);
	}
	public void setTitleX(String _titleX) {
		getStateHelper().put(PropertyKeys.titleX, _titleX);
	}

	public String getTitleY() {
		return (String) getStateHelper().eval(PropertyKeys.titleY, null);
	}
	public void setTitleY(String _titleY) {
		getStateHelper().put(PropertyKeys.titleY, _titleY);
	}

	public String getLabelFunctionX() {
		return (String) getStateHelper().eval(PropertyKeys.labelFunctionX, null);
	}
	public void setLabelFunctionX(String _labelFunctionX) {
		getStateHelper().put(PropertyKeys.labelFunctionX, _labelFunctionX);
	}

	public String getLabelFunctionY() {
		return (String) getStateHelper().eval(PropertyKeys.labelFunctionY, null);
	}
	public void setLabelFunctionY(String _labelFunctionY) {
		getStateHelper().put(PropertyKeys.labelFunctionY, _labelFunctionY);
	}

    protected abstract String getCategoryField();

    protected abstract String getCategoryAxis();

    protected abstract String getNumericAxis();

    protected abstract String getChartWidget();
}
