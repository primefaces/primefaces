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

import java.util.ArrayList;
import java.util.List;

/**
 * The linear scale is use to chart numerical data. It can be placed on either
 * the x or y axis. The scatter chart type automatically configures a line chart
 * to use one of these scales for the x axis. As the name suggests, linear
 * interpolation is used to determine where a value lies on the axis.
 */
public class LinearScales {

	private final List<LinearScale> xAxes = new ArrayList<LinearScale>();
	
	private final List<LinearScale> yAxes = new ArrayList<LinearScale>();

	public List<LinearScale> getxAxes() {
		return xAxes;
	}

	public LinearScales addxAxis(LinearScale xAxis) {
		xAxes.add(xAxis);
		return this;
	}

	public LinearScales setxAxes(List<LinearScale> xAxes) {
		this.xAxes.clear();
		if (xAxes != null) {
			this.xAxes.addAll(xAxes);
		}
		return this;
	}

	public List<LinearScale> getyAxes() {
		return yAxes;
	}

	public LinearScales addyAxis(LinearScale yAxis) {
		yAxes.add(yAxis);
		return this;
	}

	public LinearScales setyAxes(List<LinearScale> yAxes) {
		this.yAxes.clear();
		if (yAxes != null) {
			this.yAxes.addAll(yAxes);
		}
		return this;
	}

}
