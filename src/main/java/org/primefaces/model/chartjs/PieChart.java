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
import org.primefaces.model.chartjs.data.PieData;
import org.primefaces.model.chartjs.dataset.PieDataset;
import org.primefaces.model.chartjs.gson.GSON;
import org.primefaces.model.chartjs.options.Options;
import org.primefaces.model.chartjs.options.PieOptions;

public class PieChart implements Chart {

	/**
	 * Static factory, constructs an {@link Data} implementation appropriate
	 * for a {@link PieChart}.
	 * 
	 * @return a new {@link PieData} instance
	 */
	public static PieData data() {
		return new PieData();
	}

	/**
	 * Static factory, constructs an {@link Options} implementation appropriate
	 * for a {@link PieChart}.
	 * 
	 * @return a new {@link PieOptions} instance
	 */
	public static PieOptions options() {
		return new PieOptions();
	}

	private final String type = "pie";

	private PieData data;

	private PieOptions options;

	public PieChart() {
	}

	public PieChart(PieData data) {
		this.data = data;
	}

	public PieChart(PieData data, PieOptions options) {
		this.data = data;
		this.options = options;
	}

	public PieData getData() {
		return data;
	}

	public PieChart setData(PieData data) {
		this.data = data;
		return this;
	}

	public PieOptions getOptions() {
		return options;
	}

	public PieChart setOptions(PieOptions options) {
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
	 * {@code PieChart} is drawable if at least one dataset has at least one
	 * data point.
	 * </p>
	 */
	@Override
	public boolean isDrawable() {
		for (PieDataset dataset : data.getDatasets()) {
			if (dataset.getData().size() > 1) {
				return true;
			}
		}
		return false;
	}

}
