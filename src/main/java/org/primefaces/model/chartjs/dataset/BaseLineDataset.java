/*
	Copyright 2017 Marceau Dewilde <m@ceau.be>

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package org.primefaces.model.chartjs.dataset;

public abstract class BaseLineDataset<T extends PointDataset<T, O>, O> extends PointDataset<T, O> {

	/**
	 * @see #setXAxisID(String)
	 */
	private String xAxisID;
	/**
	 * @see #setYAxisID(String)
	 */
	private String yAxisID;
	/**
	 * @see #setShowLine(Boolean)
	 */
	private Boolean showLine;

	/**
	 * @see #setSpanGaps(Boolean)
	 */
	private Boolean spanGaps;

	/**
	 * @see #setSteppedLine(Boolean)
	 */
	private Boolean steppedLine;

	/**
	 * @see #setXAxisID(String)
	 */
	public String getXAxisID() {
		return xAxisID;
	}

	/**
	 * The ID of the x axis to plot this dataset on. The value for this property
	 * should equal the ID set at {@code chart.options.scales.xAxes.id}
	 */
	@SuppressWarnings("unchecked")
	public T setXAxisID(String xAxisID) {
		this.xAxisID = xAxisID;
		return (T) this;
	}

	/**
	 * @see #setYAxisID(String)
	 */
	public String getYAxisID() {
		return yAxisID;
	}

	/**
	 * The ID of the y axis to plot this dataset on. The value for this property
	 * should equal the ID set at {@code chart.options.scales.yAxes.id}
	 */
	@SuppressWarnings("unchecked")
	public T setYAxisID(String yAxisID) {
		this.yAxisID = yAxisID;
		return (T) this;
	}

	/**
	 * @see #setShowLine(Boolean)
	 */
	public Boolean getShowLine() {
		return showLine;
	}

	/**
	 * If false, the line is not drawn for this dataset
	 */
	@SuppressWarnings("unchecked")
	public T setShowLine(Boolean showLine) {
		this.showLine = showLine;
		return (T) this;
	}

	/**
	 * @see #setSpanGaps(Boolean)
	 */
	public Boolean getSpanGaps() {
		return spanGaps;
	}

	/**
	 * If true, lines will be drawn between points with no or null data
	 */
	@SuppressWarnings("unchecked")
	public T setSpanGaps(Boolean spanGaps) {
		this.spanGaps = spanGaps;
		return (T) this;
	}

	/**
	 * @see #setSteppedLine(Boolean)
	 */
	public Boolean getSteppedLine() {
		return steppedLine;
	}

	/**
	 * If true, the line is shown as a steeped line and 'lineTension' will be
	 * ignored
	 */
	@SuppressWarnings("unchecked")
	public T setSteppedLine(Boolean steppedLine) {
		this.steppedLine = steppedLine;
		return (T) this;
	}

}
