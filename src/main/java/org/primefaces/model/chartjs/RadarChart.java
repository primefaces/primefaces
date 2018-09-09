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
package org.primefaces.model.chartjs;

import org.primefaces.model.chartjs.data.Data;
import org.primefaces.model.chartjs.data.RadarData;
import org.primefaces.model.chartjs.dataset.RadarDataset;
import org.primefaces.model.chartjs.gson.GSON;
import org.primefaces.model.chartjs.options.Options;
import org.primefaces.model.chartjs.options.RadarOptions;

public class RadarChart implements Chart {

	/**
	 * Static factory, constructs an {@link Data} implementation appropriate for
	 * a {@link RadarChart}.
	 * 
	 * @return a new {@link RadarData} instance
	 */
	public static RadarData data() {
		return new RadarData();
	}

	/**
	 * Static factory, constructs an {@link Options} implementation appropriate
	 * for a {@link RadarChart}.
	 * 
	 * @return a new {@link RadarOptions} instance
	 */
	public static RadarOptions options() {
		return new RadarOptions();
	}

	private final String type = "radar";

	private RadarData data;

	private RadarOptions options;

	public RadarChart() {
	}

	public RadarChart(RadarData data) {
		this.data = data;
	}

	public RadarChart(RadarData data, RadarOptions options) {
		this.data = data;
		this.options = options;
	}

	public RadarData getData() {
		return data;
	}

	public RadarChart setData(RadarData data) {
		this.data = data;
		return this;
	}

	public RadarOptions getOptions() {
		return options;
	}

	public RadarChart setOptions(RadarOptions options) {
		this.options = options;
		return this;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String toJson() {
		return GSON.INSTANCE.toJson(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * {@code RadarChart} is drawable if at least one dataset has at least three
	 * data points.
	 * </p>
	 */
	@Override
	public boolean isDrawable() {
		for (RadarDataset dataset : data.getDatasets()) {
			if (dataset.getData().size() > 2) {
				return true;
			}
		}
		return false;
	}

}
