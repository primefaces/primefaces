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
package org.primefaces.model.chartjs.options;

import org.primefaces.model.chartjs.options.elements.RectangleElements;
import org.primefaces.model.chartjs.options.scales.BarScale;
import org.primefaces.model.chartjs.options.scales.Scale;

public class BarOptions extends Options<BarOptions> {

	private BarScale scales;
	private RectangleElements elements;

	/**
	 * Static factory, constructs a {@link Scale} implementation appropriate for
	 * a {@link BarOptions} instance.
	 *
	 * @return a new {@link BarScale} instance
	 */
	public static BarScale scales() {
		return new BarScale();
	}

	/**
	 * @return {@link BarScale} instance or {@code null}
	 * @see #setScales(BarScale)
	 */
	public BarScale getScales() {
		return scales;
	}

	/**
	 * <p>
	 * Default {@code -}
	 * </p>
	 * 
	 * @param scales
	 *            {@link BarScale} instance or {@code null}
	 * @return this {@link BarOptions} instance for method chaining
	 */
	public BarOptions setScales(BarScale scales) {
		this.scales = scales;
		return this;
	}

	/**
	 * @return {@link RectangleElements} instance, or {@code null} if not set
	 */
	public RectangleElements getElements() {
		return elements;
	}

	/**
	 * @param elements
	 *            an {@link RectangleElements} instance, or {@code null}
	 * @return this instance for method chaining
	 */
	public BarOptions setElements(RectangleElements elements) {
		this.elements = elements;
		return this;
	}

}
