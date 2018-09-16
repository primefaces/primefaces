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

import org.primefaces.model.chartjs.enums.BorderSkipped;
import org.primefaces.model.chartjs.objects.OptionalArray;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public class BarDataset extends BackgroundBorderHoverDataset<BarDataset, BigDecimal> {

	/**
	 * @see #setLabel(String)
	 */
	private String label;

	/**
	 * @see #setXAxisID(String)
	 */
	private String xAxisID;

	/**
	 * @see #setYAxisID(String)
	 */
	private String yAxisID;

	/**
	 * @see #setBorderSkipped(List)
	 */
	private final List<BorderSkipped> borderSkipped = new OptionalArray<BorderSkipped>();

	/**
	 * @see #setLabel(String)
	 */
	public String getLabel() {
	    return label;
	}

	/**
	 * The label for the dataset which appears in the legend and tooltips
	 */
	public BarDataset setLabel(String label) {
	    this.label = label;
		return this;
	}

	/**
	 * @see #setXAxisID(String)
	 */
	public String getXAxisID() {
	    return xAxisID;
	}

	/**
	 * The ID of the x axis to plot this dataset on
	 */
	public BarDataset setXAxisID(String xAxisID) {
	    this.xAxisID = xAxisID;
		return this;
	}

	/**
	 * @see #setYAxisID(String)
	 */
	public String getYAxisID() {
	    return yAxisID;
	}

	/**
	 * The ID of the y axis to plot this dataset on
	 */
	public BarDataset setYAxisID(String yAxisID) {
	    this.yAxisID = yAxisID;
		return this;
	}

	/**
	 * @see #setBorderSkipped(List)
	 */
	public List<BorderSkipped> getBorderSkipped() {
	    return borderSkipped;
	}

	/**
	 * @see #setBorderSkipped(List)
	 */
	public BarDataset addBorderSkipped(BorderSkipped borderSkipped) {
	    this.borderSkipped.add(borderSkipped);
		return this;
	}

	/**
	 * Which edge to skip drawing the border for. Options are 'bottom', 'left', 'top', and 'right'
	 */
	public BarDataset setBorderSkipped(List<BorderSkipped> borderSkipped) {
	    this.borderSkipped.clear();
	    if (borderSkipped != null) {
	    	this.borderSkipped.addAll(borderSkipped);
	    }
		return this;
	}

	/**
	 * Sets the backing data list to the argument, replacing any data already
	 * added or set
	 * 
	 * @param data
	 *            The data to plot in a line
	 */
	public BarDataset setData(int... data) {
		clearData();
		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				this.data.add(new BigDecimal(data[i]));
			}
		}
		return this;
	}

	/**
	 * Sets the backing data list to the argument, replacing any data already
	 * added or set
	 * 
	 * @param data
	 *            The data to plot in a line
	 */
	public BarDataset setData(double... data) {
		clearData();
		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				this.data.add(new BigDecimal(String.valueOf(data[i])));
			}
		}
		return this;
	}

	/**
	 * Add the data point to this {@code Dataset}
	 * 
	 * @see #setData(Collection)
	 */
	public BarDataset addData(int data) {
		this.data.add(new BigDecimal(data));
		return this;
	}

	/**
	 * Add the data point to this {@code Dataset}
	 *
	 * @see #setData(Collection)
	 */
	public BarDataset addData(double data) {
		this.data.add(new BigDecimal(String.valueOf(data)));
		return this;
	}

}
