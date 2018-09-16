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
package org.primefaces.model.chartjs.options.scales;

import org.primefaces.model.chartjs.options.ticks.LinearTicks;

/**
 * The linear scale is use to chart numerical data. It can be placed on either
 * the x or y axis. The scatter chart type automatically configures a line chart
 * to use one of these scales for the x axis. As the name suggests, linear
 * interpolation is used to determine where a value lies on the axis.
 */
public class LinearScale extends Scale<LinearScale> {

	private final String type = "linear";
	
	private LinearTicks ticks;
	
	private Boolean stacked;
	
	public String getType() {
		return type;
	}
	
	public Boolean getStacked() {
		return stacked;
	}
	
	public LinearScale setStacked(Boolean stacked) {
		this.stacked = stacked;
		return this;
	}

	@Override
	public LinearTicks getTicks() {
		return ticks;
	}

	public LinearScale setTicks(LinearTicks ticks) {
		this.ticks = ticks;
		return this;
	}

}
