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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RadarDataset extends PointDataset<RadarDataset, BigDecimal> {

	/**
	 * @see #setHitRadius(List)
	 */
	private final List<Integer> hitRadius = new ArrayList<Integer>();

	/**
	 * @see #setHitRadius(List)
	 */
	public List<Integer> getHitRadius() {
		return hitRadius;
	}

	/**
	 * @see #setHitRadius(List)
	 */
	public RadarDataset addHitRadius(Integer hitRadius) {
		this.hitRadius.add(hitRadius);
		return this;
	}

	/**
	 * The pixel size of the non-displayed point that reacts to mouse events
	 */
	public RadarDataset setHitRadius(List<Integer> hitRadius) {
		this.hitRadius.clear();
		if (hitRadius != null) {
			this.hitRadius.addAll(hitRadius);
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
	public RadarDataset setData(int... data) {
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
	public RadarDataset setData(double... data) {
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
	public RadarDataset addData(int data) {
		this.data.add(new BigDecimal(data));
		return this;
	}

	/**
	 * Add the data point to this {@code Dataset}
	 *
	 * @see #setData(Collection)
	 */
	public RadarDataset addData(double data) {
		this.data.add(new BigDecimal(String.valueOf(data)));
		return this;
	}

}
