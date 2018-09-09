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
package org.primefaces.model.chartjs.options.ticks;

public class CategoryTicks extends Ticks<CategoryTicks> {

	/**
	 * @see #setMin(String)
	 */
	private String min;

	/**
	 * @see #setMax(String)
	 */
	private String max;

	/**
	 * @see #setMin(String)
	 */
	public String getMin() {
		return min;
	}

	/**
	 * The minimum item to display. Must be a value in the labels array
	 */
	public CategoryTicks setMin(String min) {
		this.min = min;
		return this;
	}

	/**
	 * @see #setMax(String)
	 */
	public String getMax() {
		return max;
	}

	/**
	 * The maximum item to display. Must be a value in the labels array
	 */
	public CategoryTicks setMax(String max) {
		this.max = max;
		return this;
	}

}
