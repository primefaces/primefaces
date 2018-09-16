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

import java.math.BigDecimal;

import org.primefaces.model.chartjs.options.animation.DoughnutAnimation;
import org.primefaces.model.chartjs.options.elements.ArcElements;

public class DoughnutOptions extends Options<DoughnutOptions> {

	/**
	 * Default {@code 50}
	 * 
	 * @see #setCutoutPercentage(BigDecimal cutoutPercentage)
	 */
	private BigDecimal cutoutPercentage;

	/**
	 * Default {@code -0.5 * Math.PI}
	 * 
	 * @see #setRotation(BigDecimal rotation)
	 */
	private BigDecimal rotation;

	/**
	 * Default {@code 2 * Math.PI}
	 * 
	 * @see #setCircumference(BigDecimal circumference)
	 */
	private BigDecimal circumference;

	private ArcElements elements;

	/**
	 * @see #setCutoutPercentage(BigDecimal cutoutPercentage)
	 */
	public BigDecimal getCutoutPercentage() {
		return cutoutPercentage;
	}

	/**
	 * The percentage of the chart that is cut out of the middle.
	 */
	public DoughnutOptions setCutoutPercentage(BigDecimal cutoutPercentage) {
		this.cutoutPercentage = cutoutPercentage;
		return this;
	}

	/**
	 * @see #setRotation(BigDecimal rotation)
	 */
	public BigDecimal getRotation() {
		return rotation;
	}

	/**
	 * Starting angle to draw arcs from
	 */
	public DoughnutOptions setRotation(BigDecimal rotation) {
		this.rotation = rotation;
		return this;
	}

	/**
	 * @see #setCircumference(BigDecimal circumference)
	 */
	public BigDecimal getCircumference() {
		return circumference;
	}

	/**
	 * Sweep to allow arcs to cover
	 */
	public DoughnutOptions setCircumference(BigDecimal circumference) {
		this.circumference = circumference;
		return this;
	}

	/**
	 * @see #setAnimation(be.ceau.chart.options.animation.Animation)
	 */
	@Override
    public DoughnutAnimation getAnimation() {
		return (DoughnutAnimation) super.getAnimation();
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
	public DoughnutOptions setElements(ArcElements elements) {
		this.elements = elements;
		return this;
	}

}
