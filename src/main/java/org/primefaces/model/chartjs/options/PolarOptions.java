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

import org.primefaces.model.chartjs.options.animation.PolarAnimation;
import org.primefaces.model.chartjs.options.elements.ArcElements;
import org.primefaces.model.chartjs.options.scales.RadialLinearScale;
import org.primefaces.model.chartjs.options.scales.Scale;

public class PolarOptions extends Options<PolarOptions> {

	/**
	 * @see #setScale(RadialLinearScale scale)
	 */
	private RadialLinearScale scale;
	private ArcElements elements;

	/**
	 * Static factory, constructs a {@link Scale} implementation appropriate
	 * for a {@link PolarOptions} instance.
	 *
	 * @return a new {@link RadialLinearScale} instance
	 */
	public static RadialLinearScale scales() {
		return new RadialLinearScale();
	}

	/**
	 * @see #setScale(RadialLinearScale)
	 */
	public RadialLinearScale getScale() {
		return scale;
	}

	/**
	 * Options for the one scale used on the chart. Use this to style the ticks,
	 * labels, and grid.
	 */
	public PolarOptions setScale(RadialLinearScale scale) {
		this.scale = scale;
		return this;
	}

	/**
	 * @see #setAnimation(be.ceau.chart.options.animation.Animation)
	 */
	@Override
	public PolarAnimation getAnimation() {
		return (PolarAnimation) super.getAnimation();
	}

	/**
	 * @return {@link ArcElements} instance, or {@code null} if not set
	 */
	public ArcElements getElements() {
		return elements;
	}

	/**
	 * @param elements
	 *            an {@link ArcElements} instance, or {@code null}
	 * @return this instance for method chaining
	 */
	public PolarOptions setElements(ArcElements elements) {
		this.elements = elements;
		return this;
	}

}
